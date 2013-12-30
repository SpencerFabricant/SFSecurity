package sfsecurity.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import sfsecurity.network.Ping;
import sfsecurity.util.FileParser;
import sfsecurity.util.SecurityLevel;

public class PingStatusThread extends Thread {
	boolean found;
	long lastIntervalTime;
	int interval;
	long lastPing;
	long maxInterval;
	long lastInterval;
	
	Core core;
	ArrayList<String> userIPAddresses;
	private static String IP_FILENAME = "data/user_ip.txt";
	
	public PingStatusThread(Core core) {
		this.core = core;
		found = false;
		lastIntervalTime = System.currentTimeMillis();
//		interval = 960000; // 16 minutes in milliseconds //
		interval = 60000; // 1 minute in milliseconds //
		lastInterval = 0;
		lastPing = System.currentTimeMillis();
		maxInterval = 0;
	}
	
	public void run() {
		// <setup>
		try {
			userIPAddresses = FileParser.readFile(IP_FILENAME);
		} catch (IOException e) {
			System.err.println("ERROR:  COULD NOT OPEN USER IP ADDRESS FILE.");
			System.err.println("closing...");
			e.printStackTrace();
			System.exit(-1);
		}
		// </setup>
		System.out.println("All times are in minutes\nStarting at " + (new Date()).toString());
		while(core.isRunning) {
			if (isUserHome()) {
				if (!found) {
					lastInterval = System.currentTimeMillis() - lastPing;
					if (lastInterval > maxInterval)
						maxInterval = lastInterval;
				}
				lastPing = System.currentTimeMillis();
				found = true;
//				System.out.println(true);
				core.status.equals(SecurityLevel.GREEN);
			} else {
//				System.out.println(false);
				// user is not home
				// 3 possible cases for core.status:
					// if green, it needs to be updated to orange
					// if orange, nothing needs to be done.
					// if red, it needs to stay red
				if (core.status.equals(SecurityLevel.GREEN)) {
					core.status = SecurityLevel.ORANGE;
				}
			}
			if (System.currentTimeMillis() - lastIntervalTime >= interval) {
				if (found) {
					int seconds = (int)lastInterval/1000;
					double intervalMinutes = (double) seconds / 60;
					seconds = (int) maxInterval/1000;
					double maxMinutes = (double) seconds / 60;
					System.out.println(String.format("Pinged.  Interval: %.2f.  max interval: %.2f.  Date: %s", intervalMinutes, maxMinutes, (new Date()).toString()));
				}
				found = false;
				lastIntervalTime = System.currentTimeMillis();
			}
			
			
		}
	}
	
	public static void main(String[] args) {
		PingStatusThread ps = new PingStatusThread(new Core());
		ps.start();
	}
	
	/**
	 * Assumes that userIPAddresses has been initialized and attempts to ping all
	 * addresses that are on the list, with each ping on a different thread.
	 * 
	 * @return true if any of the addresses can be pinged, false otherwise.
	 */
	private boolean isUserHome() {
		ArrayList<Ping> pingThreads = new ArrayList<Ping>(userIPAddresses.size());
		// Create and start all of the threads
		for (String host : userIPAddresses) {
			Ping p = new Ping(host);
			pingThreads.add(p);
			p.start();
		}

		// wait for the threads to finish, then check if any of them returned true
		boolean result = false;
		for (Ping p : pingThreads) {
			try {
				p.join();
				if (p.result == true) {
					result = true;
					break;
				}
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
		return result;
	}
}
