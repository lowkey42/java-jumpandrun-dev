package de.secondsystem.game01.impl.map;

public enum LayerType {
	BACKGROUND_2(0, .8f , true, "B2[1]"),
	BACKGROUND_1(1, .95f, true, "B1[2]"),
	BACKGROUND_0(2, 1.f , true, "B0[3]"),
	
	PHYSICS		(3, 1.f , false, "P[P]"),
	OBJECTS		(4, 1.f , true, "O[O]"),
	
	FOREGROUND_0(5, 1.f , true, "F0[4]"),
	FOREGROUND_1(6, 1.f , true, "F1[5]");
	
	public static final int LAYER_COUNT = 7;

	public final int layerIndex;
	public final float parallax;
	public final boolean visible;
	public final String name;
	private LayerType(int _layerIndex, float _parallax, boolean _visible, String _name) {
		layerIndex = _layerIndex;
		parallax = _parallax;
		visible = _visible;
		name = _name;
	}
}