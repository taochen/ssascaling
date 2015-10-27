package org.ssascaling.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.ssascaling.ControlBus;
import org.ssascaling.Service;
import org.ssascaling.executor.VM;
import org.ssascaling.objective.Cost;
import org.ssascaling.objective.Objective;
import org.ssascaling.primitive.ControlPrimitive;
import org.ssascaling.primitive.HardwareControlPrimitive;
import org.ssascaling.primitive.Primitive;
import org.ssascaling.qos.QualityOfService;
import org.ssascaling.region.SuperRegionControl;
import org.ssascaling.util.Repository;
import org.ssascaling.util.SSAScalingThreadPool;

public class Analyzer {

	/**
	 * 
	 * TODO Maria:
	 * 
	 * You need to put your own analyzer approach here, I have already left the code for the detection of violation, and the code
	 * for updating the sensed values in cloud primitive and QoS objects.
	 * 
	 * The return is a list of objectives that is considered being violated.
	 * 
	 * @param samples the index of sample, e.g., 1, 2, 3 ...
	 * @return a list of objectives that is considered being violated
	 */
	public static List<Objective> doAnalysis(long samples){
		
		
		// Actually adding the values
		doAddValues(samples);
		
		
		return detectSymptoms();
	}
	
	
	
	private static void doAddValues(long samples){
		
		for (Service s : Repository.getAllServices() ) {
			for (Primitive p : s.getPrimitives()) {
				p.addValue(samples);
			}
		}
		
		for (VM v : Repository.getAllVMs()) {
			for (Primitive p : v.getAllPrimitives()){
				p.addValue(samples);
			}
		}
		
		for (final QualityOfService qos : Repository.getQoSSet()) {
			qos.doAddValue(samples);
		}
			

		doResetValues();
	}
	
	/**
	 * This is used to ensure that the 'values' attribute is empty,
	 * 
	 * so that the CP, EP and QoS can follow the workflow that uses 'value attribute'
	 */
	private static void doResetValues(){
		
		for (Service s : Repository.getAllServices() ) {
			for (Primitive p : s.getPrimitives()) {
				p.resetValues();
			}
		}
		
		for (VM v : Repository.getAllVMs()) {
			for (Primitive p : v.getAllPrimitives()){
				p.resetValues();
			}
		}
		
		for (final QualityOfService qos : Repository.getQoSSet()) {
			qos.resetValues();
		}
			
	}
	
	
	private static  List<Objective> detectSymptoms(){
	
		
		// TODO add proactive detection based on the QoS models.
		// as here we have only reactive detection on violation of QoS and CP utilization.
		 List<Objective> result = new ArrayList<Objective>();
		 
		 for (QualityOfService q : Repository.getQoSSet()) {
			 if (q.isViolate()) {
				 result.add(q);
			 }
		 }
		 
		 for (Cost c : Repository.getCostSet()) {
			 if (c.isViolate()) {
				 result.add(c);
			 }
		 }
		 
		 /*for (Service s : Repository.getAllServices() ) {
			 s.updateCostModelInputs();
		 }*/
		 
		
		  
		 return result;
		 
	}
	
}
