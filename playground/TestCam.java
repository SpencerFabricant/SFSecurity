import sfsecurity.model.Cam;


public class TestCam {
	public static void main(String[] args) {
		System.out.println("HI");
		Cam cam = new Cam();
		System.out.println("Recording video...");
		cam.recordVideo(5);
		System.out.println("Exiting...");
	}
}
