package sfsecurity.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import sfsecurity.model.Cam;
import sfsecurity.util.EmailThread;
import sfsecurity.util.FileParser;
import sfsecurity.util.SecurityLevel;

public class Core {
	public static final String USER_IPS_FILENAME = "data/user_ip.txt";
	private Object statusLock = new Object();
	// status: initialized to green level
	public volatile SecurityLevel status = SecurityLevel.GREEN;
	public volatile boolean isRunning = true;
	
	private static ArrayList<PingThread> pingThreads;
	private static ArrayList<CamThread> camThreads;
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
		System.out.println("Ping threads started");
		// start cam thread //
		for (CamThread camThread : camThreads) {
			camThread.start();
		}
		System.out.println("Cam thread started");
		
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
		System.out.println("initialized ping threads");
		camThreads = new ArrayList<CamThread>();
		camThreads.add(new CamThread(this));
		System.out.println("initialized camera thread");
		
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
	
	
	private static final int MOTION_WAIT_GREEN = 1000;
	private static final int MOTION_WAIT_ORANGE = 50;
	private static final int MOTION_WAIT_RED = 50;
	private long lastMotion = System.currentTimeMillis();
	private long lastAlertTime = 0;
	private final static long ALERT_COOLDOWN_INTERVAL = 20000; 
	public long handleMotion(boolean motion, Cam cam) {
		SecurityLevel previousStatus;
		SecurityLevel newStatus;
		synchronized(statusLock) {
		previousStatus = status;
		newStatus = status;
		for (int i=0;i<1;i++) { // just a single iteration, so the breaks work
			if (status.equals(SecurityLevel.GREEN)) {
				newStatus = status;
			}
			if (motion == false) {
				if (status.equals(SecurityLevel.RED)) {
					if (System.currentTimeMillis() - lastMotion >= ALERT_COOLDOWN_INTERVAL) {
						status = SecurityLevel.ORANGE;
						newStatus = SecurityLevel.ORANGE;
						break;
					}
					break;
				}
			} else {
				// motion is true
				lastMotion = System.currentTimeMillis();
				if (status.equals(SecurityLevel.ORANGE)) {
					status = SecurityLevel.RED;
					newStatus = SecurityLevel.RED;
				}
				break;
			}
		}}
		
		// so the status is properly changed.  Now make do the appropriate stuff
		// and return the appropriate amount.  Just wanted to relieve the lock.
		if (newStatus.equals(SecurityLevel.GREEN)) {
			return MOTION_WAIT_GREEN;
		}
		if (newStatus.equals(SecurityLevel.ORANGE)) {
			return MOTION_WAIT_ORANGE;
		}
		// newStatus is red //
		if (previousStatus.equals(SecurityLevel.ORANGE)) {
			lastAlertTime = System.currentTimeMillis();
			sendEmailAlert(cam);
		} else {
			if (motion && (System.currentTimeMillis() - lastAlertTime > ALERT_COOLDOWN_INTERVAL)) {
				// send out another alert
				lastAlertTime = System.currentTimeMillis();
				sendEmailAlert(cam);
			}
			return MOTION_WAIT_RED;
		}
		System.err.println("It should not reach this line");
		return 0;
	}
	
	/**
	 * Captures image from the camera and sends out an email alert
	 * 
	 */
	private void sendEmailAlert(Cam cam) {
		ArrayList<String> attachments = new ArrayList<String>();
		try {
			attachments.add(cam.savePicture());
			attachments.add(cam.savePicture());
			EmailThread.sendEmail(attachments);
			cam.recordVideo(10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Core core = new Core();
		core.go();
		Scanner s = new Scanner(System.in);
		while(true) {
			
		}
	}
}
