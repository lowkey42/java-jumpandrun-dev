package de.secondsystem.game01.impl.map;

public enum LayerType {
	LIGHTS		(0, 1.f, true,  true, true, "L[L]"),
	
	BACKGROUND_2(1, .01f, true,  true, false, "B2[1]"),
	BACKGROUND_1(2, .8f, true,  true, false, "B1[2]"),
	BACKGROUND_0(3, 1.f, true,  true, false, "B0[3]"),
	
	PHYSICS		(4, 1.f, false, true, true, "P[P]"),
	OBJECTS		(5, 1.f, true,  true, true, "O[O]"),
	
	FOREGROUND_0(6, 1.f, true,  true, true, "F0[4]"),
	FOREGROUND_1(7, 1.f, true,  true, true, "F1[5]");
	
	public static final int LAYER_COUNT = 8;

	public final int layerIndex;
	
	/** parallax usually contains values between 0 and 1: lower values -> slower scrolling
	 * a lower scrolling speed has the effect that the background appears to be farther away
	 */
	public final float parallax;
	public final boolean visible;
	public final boolean updated;
	public final boolean fade;
	public final String name;
	
	private LayerType(int _layerIndex, float _parallax, boolean _visible, boolean _updated, boolean _fade, String _name) {
		layerIndex = _layerIndex;
		parallax = _parallax;
		visible = _visible;
		updated = _updated;
		fade = _fade;
		name = _name;
	}

	public LayerType next() {
		LayerType ct[] = values();
		return ct[ (ordinal()+1)%ct.length ];
	}
	public LayerType prev() {
		LayerType ct[] = values();
		return ct[ ordinal()==0 ? ct.length-1 : (ordinal()-1)%ct.length ]; 
	}
	public static LayerType first() {
		return values()[0];
	}
}