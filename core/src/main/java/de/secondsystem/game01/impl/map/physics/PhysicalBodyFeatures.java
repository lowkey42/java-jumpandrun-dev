package de.secondsystem.game01.impl.map.physics;

public final class PhysicalBodyFeatures {
	
	public static final int STABLE_CHECK		= (int) Math.pow(2,0);
	
	public static final int SIDE_CONTACT_CHECK 	= (int) Math.pow(2,1);
	
	public static final int WORLD_SWITCH_CHECK 	= (int) Math.pow(2,2);
	
	
	public static boolean has(int featureSet, int feature) {
		return (featureSet & feature) != 0;
	}
	
	private PhysicalBodyFeatures() {
	}
}
