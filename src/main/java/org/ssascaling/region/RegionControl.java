package org.ssascaling.region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.ssascaling.ControlBus;
import org.ssascaling.Service;
import org.ssascaling.objective.Objective;
import org.ssascaling.observation.event.ModelChangeEvent;
import org.ssascaling.observation.listener.ModelListener;
import org.ssascaling.primitive.ControlPrimitive;
import org.ssascaling.primitive.Primitive;
import org.ssascaling.util.Tuple;

/**
 * Create new instance whenever super region distribution change.
 * @author tao
 *
 */
public class RegionControl implements ModelListener {
	
	
	protected String id;

	private Map<Objective, Region> objectiveMap;
	
	
	//private Set<Primitive> possibleInput;
	
	private Object lock = new Object();
	
	
    // Ensure to trigger re assign region only when all models are up-to-date. 
    // In case where failure occur, this instance is invalid anyway as the optimization thread
    // would be stopped by the SuperRegionControl.
    private int knownConunter = 0;
    
    private boolean isStop = false;
    
    // Include the decomposition of scaled objective as those objective
    // has more then one notification upon significant changes, but they can be considered as one.
    // Updated with calculateRegions
    private int totalNumberOfObjective;
    
    
    private Map<Region,Tuple<Objective, Thread>> map;
    
    public RegionControl(Set<Objective> objs){
    	objectiveMap = new HashMap<Objective, Region>();
    	map = new HashMap<Region,Tuple<Objective, Thread>>();
    	id = UUID.randomUUID().toString();
    	calculateRegions(objs);
    }
	
    
    private RegionControl(String id){
    	this.id = id;
    	objectiveMap = new HashMap<Objective, Region>();
    }
    /**
     * It is possible that, the return solution is invalid, e.g., critical change occur right after
     * this method return. So when doing the actions, we need to validate.
     * @param obj
     */
	public LinkedHashMap<ControlPrimitive, Double> optimize(Objective obj){
		Region region = null;
		synchronized (lock) {
			region = objectiveMap.get(obj);
			// This should not occur, but just in case there is duplicate restarting due to super region and model
			// change at closed time.
			
			// This is because it should have been filtered in Analyzer
			if(map.containsKey(region)) {
				return null;
			}
			map.put(region, new Tuple(obj, Thread.currentThread()));
		}
		
		LinkedHashMap<ControlPrimitive, Double> result = region.optimize();
		
		synchronized (lock) {
			map.remove(region);
		}
		
		return result;
		
	}
	
	/**
	 * Only deal with critical changes.
	 * 
	 * 
	 * 
	 * 
	 * Unlike non-critical change, which can still be a valid solution even if the dynamic has not been
	 * resolved. Critical change could result in an invalid solution, which needs to be avoided.
	 */
	@Override
	public void updateWhenModelChange(ModelChangeEvent event) {
		synchronized (lock) {
			knownConunter++;
			if (event.isCritical()) {
				isStop = true;
			}
			
			if (knownConunter != totalNumberOfObjective) {
				return;
			}
			
			if (isStop) {
				// Making the source un-affect by the clearance of objective map.
				Set<Objective> set = new HashSet<Objective>();
				set.addAll( objectiveMap.keySet());
				calculateRegions(set);
				restartExistingOptimization();
			}
			
			
			
			knownConunter=0;
			isStop = false;
			
		}
		
		
	}
	
	/**
	 * Used in the Analyzer
	 * @param result
	 */
	public void filterObjective (List<Objective> result) {
		
		final Set<Region> set = new HashSet<Region>();
		
		synchronized (lock) {
			for (Objective obj : result) {
				Region reg = objectiveMap.get(obj);
				if (!set.contains(reg)) {
					set.add(reg);
				} else {
					// This is removal of the current object,
					// so it does not affect integrity of the list.
					result.remove(obj);
				}
			}
		}
	}
	
	public RegionControl copy(){
		RegionControl sr = new RegionControl(id);
		return sr;
	}
	
	/**
	 * Should be used within lock, whether it is from regioncontrol or superregioncontrol
	 * @param objs
	 */
	public void calculateRegions(Set<Objective> objs){
		objectiveMap.clear();
		totalNumberOfObjective = 0;
		for (Objective obj : objs) {
			totalNumberOfObjective += obj.countObjective();
			for (Objective subObj : objs) {
				
				// If both have been assigned.
				if (objectiveMap.containsKey(obj) && objectiveMap.containsKey(subObj) ) {
					continue;
				}
				
				if (obj.equals(subObj) ) {
			   	     continue;
			    }
				
				
				if (obj.isSensitiveToTheSamePrimitive(subObj, null)) {
					//System.out.print(obj.getName() + " and " + subObj.getName() + " does sensitive\n");
					if (objectiveMap.containsKey(obj) || objectiveMap.containsKey(subObj) ) {
						
						if (objectiveMap.containsKey(obj)) {
							Region region = objectiveMap.get(obj);
							region.addObjective(subObj);
							objectiveMap.put(subObj, region);
						} else if (objectiveMap.containsKey(subObj)) {
							Region region = objectiveMap.get(subObj);
							region.addObjective(obj);
							objectiveMap.put(obj, region);
						}
						
					} else {
						Region region = new Region();
						region.addObjective(obj);
						region.addObjective(subObj);
						objectiveMap.put(obj, region);
						objectiveMap.put(subObj, region);
					}
				}
			}
			
		}
		
		// Assign the regions that have only single objective.
		if (objectiveMap.size() != objs.size()) {
			//System.out.print("single:\n");
			for (Objective obj : objs) {
				if (!objectiveMap.containsKey(obj)) {
					//System.out.print("single: " + obj.getName()+"\n");
					Region region = new Region();
					region.addObjective(obj);;
					objectiveMap.put(obj, region);
				}
			}
		}
		print();
	}
	
	
	/**
	 * This should be sync on 'lock', at the place where it is called.
	 */
	private void restartExistingOptimization(){
		for (final Map.Entry<Region,Tuple<Objective, Thread>> entry : map.entrySet()) {
			
			entry.getValue().getVal2().stop();
			
			// Only trigger if the objective is still violated, should be fine as the Analyzer ensures
			// that the new set of objective always trigger after this method. This is because thie method is
			// called in the same thread as the model training.
			
			if (entry.getValue().getVal1().isViolate()) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						ControlBus.doDecisionMaking(entry.getValue().getVal1());
					}

				}).start();

			}
		}
		// This should be fine as it is called with the lock's sync block.
		map.clear();
	}
	
	private void print(){
		for (Map.Entry<Objective, Region> entry : objectiveMap.entrySet()) {
			System.out.print("O: " + entry.getKey().getName() + ", region: " + entry.getValue() + "\n");
			//entry.getValue().print();
		}
	}
}