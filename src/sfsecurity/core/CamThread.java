package sfsecurity.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;

import sfsecurity.model.Cam;
import sfsecurity.util.Snapshot;

/**
 * This is the thread that controls the camera and eventually feeds back information
 * to the core.
 */
public class CamThread extends Thread {
	
	private Cam cam;
	private Core core;
	public CamThread(Core core) {
		cam = new Cam();
		this.core = core;
	}
	public void run() {
		System.out.println("Running CamThread...");
		Snapshot s1 = cam.getSnapshot();
		Snapshot s2;
		boolean isMotion;
		while(true) {
			s2 = s1;
			s1 = cam.getSnapshot();
			isMotion = s1.isMotion(s2);
			try {
				sleep(core.handleMotion(isMotion, cam));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/* When motion is detected:
	 * 
	*/
	/* When motion is not detected:
	 * 	It may still be saved because ...
	 */
}
