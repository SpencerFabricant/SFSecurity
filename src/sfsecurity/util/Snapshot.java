package sfsecurity.util;

import java.awt.image.BufferedImage;


public class Snapshot {
	private final int threshold;
	public final BufferedImage image;
	public final BufferedImage smallImage;
	public Snapshot(BufferedImage image) {
		threshold = 20;
		this.image = image;
		this.smallImage = image; // TODO: resize
	}
	public boolean detectMotion(Snapshot snapshot) {
		// TODO
		if (compare(snapshot) >= threshold) return true;
		return false;
	}
	private int compare(Snapshot snapshot) {
		return 0; // TODO
	}
}
