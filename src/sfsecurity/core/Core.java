package sfsecurity.core;

import sfsecurity.util.SecurityLevel;

public class Core {
	// status: initialized to green level
	public volatile SecurityLevel status = SecurityLevel.GREEN;
	public volatile boolean isRunning = true;
	
	/**
	 * Listen to ping and motion detection and adjust the core's
	 * security level accordingly.
	 * 
	 * This is going to involve 3 threads:
	 *     One for the ping
	 *     One for the motion
	 *     One to combine the two
	 */	
	public void initSensors() {
		
	}
	
}
