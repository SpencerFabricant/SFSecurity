package sfsecurity.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping extends Thread {
	
	/**
	 * Attempts to ping the address.  Only called when the thread is started
	 * @return true if ipAddress can be reached, false otherwise.
	 */
	public static boolean ping(String ipAddress) {
		// TODO: set a smaller timeout if you can figure out how.
		Process myProcess = null;
		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				cmd = "ping -n 1 " + ipAddress;
			} else { // system is unix-based
				cmd = "ping -c 1 " + ipAddress;
			}
			myProcess = Runtime.getRuntime().exec(cmd);
			BufferedReader processInput = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
			String s;
			while((s = processInput.readLine()) != null) {
				// System.out.println(s);
				if (s.contains("unreachable")) {
					myProcess.destroy();
					return false;
				}
			}
			if (myProcess.exitValue() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// this **shouldn't** happen.  If it does, we're in trouble
			e.printStackTrace();
		}
		return false;
	}
}
