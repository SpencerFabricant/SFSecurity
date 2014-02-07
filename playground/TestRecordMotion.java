import sfsecurity.model.Cam;


public class TestRecordMotion {
	public static void main(String[] args) {
		Cam cam = new Cam();
		System.out.println("capturing motion...");
		String s = cam.recordMotion(20);
		System.out.println("Video saved to " + s);
		System.out.println("Exiting...");
	}
}
