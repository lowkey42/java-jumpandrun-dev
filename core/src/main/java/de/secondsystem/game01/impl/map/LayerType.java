package de.secondsystem.game01.impl.map;

public enum LayerType {
	BACKGROUND_2(0, .8f , "B2[1]"),
	BACKGROUND_1(1, .95f, "B1[1]"),
	BACKGROUND_0(2, 1.f , "B0[2]"),
	
	PHYSICS		(3, 1.f , "P[3]"),
	
	FOREGROUND_0(4, 1.f , "F0[4]"),
	FOREGROUND_1(5, 1.f , "F1[5]");
	
	public static final int LAYER_COUNT = 6;

	public final int layerIndex;
	public final float parallax;
	public final String name;
	private LayerType(int _layerIndex, float _parallax, String _name) {
		layerIndex = _layerIndex;
		parallax = _parallax;
		name = _name;
	}
}