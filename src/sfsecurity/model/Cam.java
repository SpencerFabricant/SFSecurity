package sfsecurity.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import sfsecurity.util.Snapshot;


public class Cam {
	Webcam webcam;
	public static final String ROOT_DIRECTORY = "data/cam/";
	public Cam() {
		webcam = Webcam.getDefault();
		webcam.open();
	}
	public String savePicture() throws IOException {
		return savePicture(ROOT_DIRECTORY);
	}
	public String savePicture(String directory) throws IOException {
		if (directory == null) directory = ROOT_DIRECTORY;
		String name = directory + ("pic_" +System.currentTimeMillis()) + ".jpg";
		savePicture(webcam.getImage(), name);
		return name;
	}
	public void savePicture(BufferedImage im, String name) throws IOException {
		ImageIO.write(im, "JPG", new File(name));
	}
	
	/**
	 * This function works like recordVideo(), but it only saves images when
	 * there is motion
	 * Smart video capture that only saves images when there is motion
	 * @param seconds -- max duration
	 * @return the absolute path of the directory that it is saved to
	 */
	public String recordMotion(int seconds) {
		String directory = ROOT_DIRECTORY + System.currentTimeMillis() + "/";
		try {
			(new File(directory)).mkdirs();
			Snapshot s1 = new Snapshot(webcam.getImage());
			Snapshot s2;
			savePicture(s1.image, directory+"pic_"+System.currentTimeMillis()+".jpg");
			long endTime = System.currentTimeMillis() + (seconds * 1000);
			while(System.currentTimeMillis() < endTime) {
				s2 = new Snapshot(webcam.getImage());
				if (s1.isMotion(s2)) {
					savePicture(s2.image, directory+"pic_"+System.currentTimeMillis()+".jpg");
					s1 = s2;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (new File(directory)).getAbsolutePath();
	}
	
	/** Returns the name of the video's frames directory */
	public String recordVideo(int seconds) {
		String directory = ROOT_DIRECTORY + System.currentTimeMillis() + "/";
		(new File(directory)).mkdirs();
		long endTime = System.currentTimeMillis() + (seconds * 1000);
		while(System.currentTimeMillis() < endTime) {
			try {
				savePicture(directory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return directory;
	}
	
	public Snapshot getSnapshot() {
		return new Snapshot(webcam.getImage());
	}
	public boolean isMotion(int interval) {
		Snapshot s1 = getSnapshot();
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Snapshot s2 = getSnapshot();
		return s1.isMotion(s2);
		
	}
		public static void main(String[] args) throws IOException, InterruptedException {
			System.out.println("Setting up webcam...");
			Webcam webcam = Webcam.getDefault();
			System.out.println("Initializing webcam...");
			webcam.open();
			System.out.println("somethingsomething...");
			BufferedImage image = webcam.getImage();
			Snapshot s = new Snapshot(image);
			Snapshot s2;
			System.out.println("...");
			int i=0;
			while(i<100) {
				s2 = new Snapshot(webcam.getImage());
				if (s.isMotion(s2)) {
					ImageIO.write(s2.image, "JPG", new File("tmpfiles/image"+(i++)+".jpg"));
					System.out.println("Written!");
				}
				System.out.println(i);
				Thread.sleep(50);
				s = s2;
			}
//			int counter = 0;
//			while(counter < 300){
//				image = webcam.getImage();
//				ImageIO.write(image, "JPG", new File("files/test" + (counter++) + ".jpg"));
//			}
			System.out.println("DONE");
		}
}
