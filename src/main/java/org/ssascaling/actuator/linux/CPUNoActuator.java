package org.ssascaling.actuator.linux;

import org.ssascaling.actuator.Actuator;
import org.ssascaling.executor.Executor;

public class CPUNoActuator  implements Actuator {
	final String command = "/bin/sh /home/tao/actuator/cpu_no_actuator";

	
	@Override
	public boolean execute(String alias, long... value) {
		
		if (Executor.isTest) {
			System.out.print("Setting " + alias + " with CPU no " + value[0] + "\n");
			return true;
		}
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command + " " + alias + " " + value[0]);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			p.destroy();
		}

		return true;
	}
}
