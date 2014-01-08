import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;


public class TestWebcam {
	public static void main(String[] args) throws IOException {
		System.out.println("Setting up webcam...");
		Webcam webcam = Webcam.getDefault();
		System.out.println("Initializing webcam...");
		webcam.open();
		System.out.println("somethingsomething...");
		BufferedImage image = webcam.getImage();
		image = webcam.getImage();
		System.out.println("...");
		int counter = 0;
		while(counter < 300){
			image = webcam.getImage();
			ImageIO.write(image, "JPG", new File("files/test" + (counter++) + ".jpg"));
		}
		System.out.println("DONE");
	}
}
