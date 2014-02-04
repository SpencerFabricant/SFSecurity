package sfsecurity.old;

import java.util.ArrayList;

import sfsecurity.util.*;

public class MassPing extends Thread implements Publisher<Boolean> {
	private ArrayList<Subscriber<Boolean>> subs = new ArrayList<Subscriber<Boolean>>();
	private static ArrayList<String> hosts;
	public void run() {
		ArrayList<Ping> pingThreads = new ArrayList<Ping>(hosts.size());
		System.out.println("starting threads....");
		for (String host : hosts) {
			Ping p = new Ping(host, this);
			pingThreads.add(p);
			p.start();
		}
				
		System.out.println("Joining threads.....");
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
		publish(result);
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

	@Override
	public void publish(Boolean message) {
		for (Subscriber<Boolean> sub : subs) {
			sub.update(message);
		}
	}

	@Override
	public void addSubscriber(Subscriber<Boolean> sub) {
		subs.add(sub);
	}

	@Override
	public void removeSubscriber(Subscriber<Boolean> sub) {
		subs.remove(sub);
	}
}
