package sfsecurity.network;

import java.io.IOException;

public class Ping extends Thread {
	String ipAddress;
	boolean result = false;
	public Ping(String ip) {
		super();
		ipAddress = ip;
	}
	public void run() {
		result = ping();
	}
	private boolean ping() {
		// TODO: set a smaller timeout
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
