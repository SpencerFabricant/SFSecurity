package sfsecurity.network;

import java.util.ArrayList;

public class MassPing extends Thread {
	private static ArrayList<String> hosts;
	public void run() {
		ArrayList<Ping> pingThreads = new ArrayList<Ping>(hosts.size());
		System.out.println("starting threads....");
		for (String host : hosts) {
			Ping p = new Ping(host);
			pingThreads.add(p);
			p.start();
		}
				
		System.out.println("Joining threads.....");
		for (Ping p : pingThreads) {
			try {
				p.join();
				if (p.result == true) {
					System.out.println("Found a match!");
					break;
				}
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
		System.out.println("joined all threads");
	}
	
	public static void main(String[] args) {
		hosts = new ArrayList<String>();
		hosts.add("128.0.0.1");
		hosts.add("126.0.0.1");
		hosts.add("122.0.0.1");
		hosts.add("123.0.0.1");
		hosts.add("132.168.1.1");
		hosts.add("123.0.0.1");

		MassPing massPingThread = new MassPing();
		massPingThread.start();
	}
}
