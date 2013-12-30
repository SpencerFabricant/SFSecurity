package sfsecurity.network;

import java.io.IOException;

public class Ping extends Thread {
	String ipAddress;
	public volatile boolean result = false;
	
	/**
	 * Creates a thread that, when run, will attempt to ping pIPAddress
	 * The result is stored in this thread's <<result>> field after the thread
	 * finishes running.
	 * @param pIPAddress
	 */
	public Ping(String pIPAddress) {
		super();
		ipAddress = pIPAddress;
	}
	public void run() {
		result = ping();
	}
	/**
	 * Attempts to ping the address.  Only called when the thread is started
	 * @return true if ipAddress can be reached, false otherwise.
	 */
	private boolean ping() {
		// TODO: set a smaller timeout if you can figure out how.
		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				cmd = "ping -n 1 " + ipAddress;
			} else { // system is unix-based
				cmd = "ping -c 1 " + ipAddress;
			}
			Process myProcess = Runtime.getRuntime().exec(cmd);
			myProcess.waitFor();
			if (myProcess.exitValue() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			
		} catch (InterruptedException e) {
			
		}
		return false;
	}
}
