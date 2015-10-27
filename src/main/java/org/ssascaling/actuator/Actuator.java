package org.ssascaling.actuator;

public interface Actuator  {
	
	/**
	 * Alias and a set of values
	 * @param alias
	 * @param value
	 * @return
	 */
	public boolean execute(String alias, long... value);

}
