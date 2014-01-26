package sfsecurity.core;

import java.io.IOException;
import java.util.ArrayList;

import sfsecurity.util.FileParser;
import sfsecurity.util.SecurityLevel;

public class Core {
	public static final String USER_IPS_FILENAME = "data/user_ip.txt";
	private Object statusLock = new Object();
	// status: initialized to green level
	public volatile SecurityLevel status = SecurityLevel.GREEN;
	public volatile boolean isRunning = true;
	
	private static ArrayList<PingThread> pingThreads;
	
	public Core() {
		MAX_PING_INTERVAL = 3000; // 16 minutes in milliseconds //
		initSensors();
	}
	
	public void go() {
		/* start ping threads */
		for (PingThread p : pingThreads) {
			System.out.println("Starting thread....");
			p.start();
		}
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

		greenWait = 100;
		orangeWait = 100;
		redWait = 100;

		// then load properties file and attempt to update variables accordingly //
			// TODO
		
		/* Obtain user IP addresses */
		ArrayList<String> ips = null;
		try {
			ips = FileParser.readFile(USER_IPS_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not find user IP address file.  Exiting...");
			System.exit(-1);
		}
		/* initialize ping threads */
		pingThreads = new ArrayList<PingThread>();
		for (String ip : ips) {
			System.out.println(ip);
			pingThreads.add(new PingThread(this, ip));
		}
	}
	
	/** object variables for ping handling */
	private long lastPingTime; // the previous time that the user was successfully pinged
	private final long MAX_PING_INTERVAL; // max interval between pings before the system concludes that the user is not present
	private int greenWait; // wait time for ping when status is green
	private int orangeWait;
	private int redWait;

	/**
	 * synchronously handles a ping.  Called by ping thread.
	 * @param ping -- true if the host is reached.  False otherwise
	 * @return -- int: The number of milliseconds the calling thread should sleep for.
	 */
	public int handlePing(boolean ping) {
		System.out.println(status.toString());
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
				if (System.currentTimeMillis() - lastPingTime >= MAX_PING_INTERVAL) {
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
	
	
	public static void main(String[] args) {
		Core core = new Core();
		core.go();
		
		while(true) {
			Thread.yield();
		}
	}
}
