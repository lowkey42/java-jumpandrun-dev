package de.secondsystem.game01.impl.map;

public enum LayerType {
	BACKGROUND_2(0, .1f, true,  false, "B2[1]"),
	BACKGROUND_1(1, .8f, true,  false, "B1[2]"),
	BACKGROUND_0(2, 1.f, true,  false, "B0[3]"),
	
	PHYSICS		(3, 1.f, false, true, "P[P]"),
	OBJECTS		(4, 1.f, true,  true, "O[O]"),
	LIGHTS		(5, 1.f, true,  true, "L[L]"),
	
	FOREGROUND_0(6, 1.f, true,  false, "F0[4]"),
	FOREGROUND_1(7, 1.f, true,  false, "F1[5]");
	
	public static final int LAYER_COUNT = 8;

	public final int layerIndex;
	
	/** parallax usually contains values between 0 and 1: lower values -> slower scrolling
	 * a lower scrolling speed has the effect that the background appears to be farther away
	 */
	public final float parallax;
	public final boolean visible;
	public final boolean updated;
	public final String name;
	
	private LayerType(int _layerIndex, float _parallax, boolean _visible, boolean _updated, String _name) {
		layerIndex = _layerIndex;
		parallax = _parallax;
		visible = _visible;
		updated = _updated;
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