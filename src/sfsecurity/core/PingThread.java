package sfsecurity.core;

import java.io.IOException;
import java.util.ArrayList;

import sfsecurity.util.FileParser;
import sfsecurity.util.Ping;
import sfsecurity.util.SecurityLevel;

public class PingThread extends Thread {
	private final String ip;
	
	Core core;
	private static String IP_FILENAME = "data/user_ip.txt";
	
	public PingThread(Core core, String ip) {
		this.setDaemon(true);
		this.ip = ip;
		this.core = core;
	}
	
	public void run() {
		System.out.println("Started ping thread: " + ip);
		while(core.isRunning) {
			try {
				boolean pinged = Ping.ping(ip);
				sleep(core.handlePing(pinged));
			} catch (InterruptedException e) {
				System.err.println("This SHOULDN'T happen...");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

	}
}
