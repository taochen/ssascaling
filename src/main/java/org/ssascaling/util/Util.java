package org.ssascaling.util;

import java.io.File;
import java.util.Random;

import org.ssascaling.monitor.Monitor;
import org.ssascaling.util.Logger;

public class Util {

	
	public static double calculateEuclideanDistance(double[] input, double[] another) {
		// The two array needs to be as the same dimension.
		double result = 0;
		for (int i = 0; i < input.length; i++) {
			  result += Math.pow( (input[i] - another[i]), 2);
		}
		
		return Math.sqrt(result);
	}
	
	public static double calculateManhattanDistance(double[] input, double[] another) {
		// The two array needs to be as the same dimension.
		double result = 0;
		for (int i = 0; i < input.length; i++) {
			  result += Math.abs(input[i] - another[i]);
		}
		
		return result;
	}
	
	public static long calculateNashProduct(double[] input) {
		
		long result = 0;
		for (int i = 0; i < input.length; i++) {
			  result *= input[i];
		}
		
		return result;
	}
	
	public static double calculateSMAPE (double a, double b) {

		if (a == 0) {
			return b; 
		}
		
		double result = 0.0;
		   if ((a > 0 && b < 0) ||
				   (a < 0 && b > 0)) {
			   result = Math.abs((b + a) / 
					   (b - a));
		   } else {
			   result = Math.abs((b- a) / 
					   (b+ a));
		   }
		   
		   return result;
	}
	
	public static double calculateMAPE (double a, double b) {
		if (a == 0) {
			return b; 
		}
		
		   return Math.abs(b- a) / a;
	}
	
	
	public static void readMeasuredData(){
	
		// We assume that historical data have been stored in memory.
		// TODO it may be possible that to remove the in-memory data after each training.
		
		
		File root = new File(Logger.prefix);
		for (File folder : root.listFiles()) {
			
			// Means it is a hardware primitive
			if (!folder.isDirectory()) {
				
			} else {
				
				// Could have many services
				for (File file : folder.listFiles()) {
					
				}
			}
		}
	}
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
