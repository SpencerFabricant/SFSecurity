import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import sfsecurity.util.Snapshot;

public class TestWebcam {
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
		for (int i=0;i<300;i++) {
			s2 = new Snapshot(webcam.getImage());
			if (s.detectMotion(s2)) {
				ImageIO.write(s2.image, "JPG", new File("tmpfiles/image"+i+".jpg"));
				System.out.println("Written!");
			}
			System.out.println(i);
			Thread.sleep(50);
			s = s2;
		}
//		int counter = 0;
//		while(counter < 300){
//			image = webcam.getImage();
//			ImageIO.write(image, "JPG", new File("files/test" + (counter++) + ".jpg"));
//		}
		System.out.println("DONE");
	}
}
