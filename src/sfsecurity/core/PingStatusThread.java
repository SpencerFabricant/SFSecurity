package sfsecurity.core;

import java.io.IOException;
import java.util.ArrayList;

import sfsecurity.network.Ping;
import sfsecurity.util.FileParser;
import sfsecurity.util.SecurityLevel;

public class PingStatusThread extends Thread {
	long lastPingTime; // the time of the last ping
	int interval;
	
	
	Core core;
	ArrayList<String> userIPAddresses;
	private static String IP_FILENAME = "data/user_ip.txt";
	
	public PingStatusThread(Core core) {
		this.core = core;
		lastPingTime = System.currentTimeMillis();
		interval = 960000; // 16 minutes in milliseconds //
//		interval = 60000; // 1 minute in milliseconds //
//		interval = 1000;
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
		while(core.isRunning) {
			if (isUserHome()) {
				System.out.println("True");
				// for debugging
				lastPingTime = System.currentTimeMillis();
				core.status.equals(SecurityLevel.GREEN);
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (System.currentTimeMillis() - lastPingTime >= interval) {
				System.out.println("False");
				// user is not home
				// 3 possible cases for core.status:
					// if green, it needs to be updated to orange
					// if orange, nothing needs to be done.
					// if red, it needs to stay red
				if (core.status.equals(SecurityLevel.GREEN)) {
					core.status = SecurityLevel.ORANGE;
				}
				// reset lastPingTime for next interval //
				lastPingTime = System.currentTimeMillis();
			}
		}
	}
	
	
	/**
	 * Assumes that userIPAddresses has been initialized and attempts to ping all
	 * addresses that are on the list, with each ping on a different thread.
	 *  (userIPAddresses is initialized when the PingStatusThread is initialized)
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
				if (!result)
					p.join();
				else
					p.kill();
				if (p.result == true) {
					result = true;
				}
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		PingStatusThread ps = new PingStatusThread(new Core());
		ps.start();
	}
}
