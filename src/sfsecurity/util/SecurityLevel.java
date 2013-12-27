package sfsecurity.util;

public enum SecurityLevel {
	GREEN,  // USER is home.  Do not worry.
	ORANGE, // USER is not home.  Stay alert
	RED     // USER is not home, and movement is detected
}
