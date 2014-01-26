package sfsecurity.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;


/**
 * Snapshot:  Contain
 * @author Spencer Fabricant
 */
public class Snapshot {
	private static final int threshold = 50;
	private static final int SMALL_SIZE = 16;
	public final String filename;
	public final Date timestamp;
	public final BufferedImage image;
	public final BufferedImage smallImage;
	public Snapshot(BufferedImage image) {
		filename = System.currentTimeMillis() + ".jpg";
		timestamp = new Date();
		this.image = image;
		smallImage = new BufferedImage(SMALL_SIZE, SMALL_SIZE, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = smallImage.createGraphics();
		g.drawImage(image, 0, 0, SMALL_SIZE, SMALL_SIZE, null);
		g.dispose();
	}
	public boolean isMotion(Snapshot snapshot) {
		if (compare(snapshot) >= threshold) return true;
		return false;
	}
	/**
	 * returns average sum sqared difference per pixel of
	 * reduced-sized greyscale image
	 */
	public double compare(Snapshot snapshot) {
		double total = 0;
		for (int i=0; i<SMALL_SIZE; i++) {
			for (int j=0; j<SMALL_SIZE; j++) {
				int diff = (this.smallImage.getRGB(i,j)&0xFF)
						- (snapshot.smallImage.getRGB(i, j)&0xFF);
				total += (diff*diff);
			}
		}
		return (total/(SMALL_SIZE * SMALL_SIZE));
	}
}
