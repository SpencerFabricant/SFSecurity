package sfsecurity.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import sfsecurity.util.Snapshot;

public class CamThread extends Thread {
	Webcam webcam;
	private boolean motionMode;
	private long motionStart;
	public CamThread() {
		motionMode = false;
		motionStart = 0;
		Webcam webcam = Webcam.getDefault();
		webcam.open();
	}
	public void run() {
		BufferedImage image = webcam.getImage();
		Snapshot s = new Snapshot(image);
		Snapshot s2;
		System.out.println("...");
		for (int i=0;i<300;i++) {
			s2 = new Snapshot(webcam.getImage());
			if (s.isMotion(s2)) {
				try {
					ImageIO.write(s2.image, "JPG", new File("tmpfiles/image"+i+".jpg"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Written!");
			}
			System.out.println(i);
			s = s2;
		}
	}
	/* When motion is detected:
	 * 
	*/
	/* When motion is not detected:
	 * 	It may still be saved because ...
	 */
}
