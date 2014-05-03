package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.ConstTexture;

public class SpriteTexture {
	public final ConstTexture texture;
	public final ConstTexture normals;

	public final ConstTexture altTexture;
	public final ConstTexture altNormals;
	
	public SpriteTexture(ConstTexture texture, ConstTexture normals, ConstTexture altTexture, ConstTexture altNormals) {
		this.texture = texture;
		this.normals = normals;
		this.altTexture = altTexture!=null ? altTexture : texture;
		this.altNormals = altTexture!=null ? altNormals : normals;
	}

}
