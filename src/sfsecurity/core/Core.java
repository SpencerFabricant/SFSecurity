package sfsecurity.core;

import sfsecurity.util.SecurityLevel;

public class Core {
	private Object statusLock = new Object();
	// status: initialized to green level
	public volatile SecurityLevel status = SecurityLevel.GREEN;
	public volatile boolean isRunning = true;
	
	public Core() {
		
		initSensors();
	}
	
	/**
	 * Listen to ping and motion detection and adjust the core's
	 * security level accordingly.
	 * 
	 * This is going to involve 3 threads:
	 *     One for the ping
	 *     One for the motion
	 *     One to combine the two
	 */
	private void initSensors() {
		
		/* init default ping variables */
		lastPingTime = System.currentTimeMillis();
		interval = 960000; // 16 minutes in milliseconds //
		greenWait = 1000;
		orangeWait = 1000;
		redWait = 1000;
		// then load properties file and attempt to update variables accordingly //
		
		
		// initialize and start running ping threads //
	}
	
	/** object variables for ping handling */
	private long lastPingTime; // the previous time that the user was successfully pinged
	private long interval; // max interval between pings before the system concludes that the user is not present
	private int greenWait; // wait time for ping when status is green
	private int orangeWait;
	private int redWait;

	/**
	 * synchronously handles a ping.  Called by ping thread.
	 * @param ping -- true if the host is reached.  False otherwise
	 * @return -- int: The number of milliseconds the calling thread should sleep for.
	 */
	public int handlePing(boolean ping) {
		synchronized(statusLock) {
			if (ping == true) {
				// User is home.  Three possibilities for status:
					// green -> green
					// orange -> green
					// red -> green
				if (! this.status.equals(SecurityLevel.GREEN)) {
					status = SecurityLevel.GREEN;
					lastPingTime = System.currentTimeMillis();
				}
			} else { // ping returned false //
				if (System.currentTimeMillis() - lastPingTime >= interval) {
					// so the user is not home.  Three possibilities for status:
						// green -> orange
						// orange -> orange
						// red -> red
					if (status.equals(SecurityLevel.GREEN)) {
						status = SecurityLevel.ORANGE;
					}
				}
			}
			if (status.equals(SecurityLevel.GREEN)) {
				return greenWait;
			} else if (status.equals(SecurityLevel.ORANGE)) {
				return orangeWait;
			} else if (status.equals(SecurityLevel.RED)) {
				return redWait;
			} else {
				// this *should not* happen //
				System.err.println("Warning: Invalid state for core status: " + status.toString());
				return 0;
			}
		}
	}
	
	private long lastMotion = System.currentTimeMillis();
	private final static long maxMotionInterval = 5000; 
	public void handleMotion(boolean motion) {
		if (status.equals(SecurityLevel.GREEN)) return;
		synchronized(statusLock) {
			if (motion == false) {
				if (status.equals(SecurityLevel.RED)) {
					if (System.currentTimeMillis() - lastMotion >= maxMotionInterval) {
						status = SecurityLevel.ORANGE;
					}
				}
			} else {
				// motion is true
				lastMotion = System.currentTimeMillis();
				if (status.equals(SecurityLevel.ORANGE)) {
					status = SecurityLevel.RED;
					// then alert some thread to capture motion
				}
			}
		}
		return;
	}
	
}
