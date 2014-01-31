package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.SpriteWrappper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.util.Tools;

/**
 * TODO: tinting
 * @author lowkey
 *
 */
public class SpriteLayerObject implements ILayerObject {

	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(SpriteLayerObject.class);
	
	private SpriteWrappper sprite;
	
	private int tileId;
	
	public SpriteLayerObject(Tileset tileset, int tileId, float x, float y, float rotation) {
		this(tileset, tileId, x, y, rotation, 0, 0);
	}
	public SpriteLayerObject(Tileset tileset, int tileId, float x, float y, float rotation, float width, float height) {
		this.tileId = tileId;
		sprite = new SpriteWrappper(tileset.get(tileId), tileset.getNormals(tileId));
		sprite.setPosition(new Vector2f(x, y));
		sprite.setRotation(rotation);
		sprite.setDimensions(width>0?width:sprite.getWidth(), height>0?height:sprite.getHeight());
	}
	
	public void setTile(Tileset tileset, int tileId) {
		this.tileId = tileId;
		sprite.setTexture(tileset.get(tileId), tileset.getNormals(tileId));
	}
	
	@Override
	public void draw(RenderTarget rt) {
		sprite.draw(rt);
	}
	
	@Override
	public void setDimensions(float width, float height) {
		sprite.setDimensions(width, height);
	}

	@Override
	public boolean inside(Vector2f point) {
		return Tools.isInside(this, point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		sprite.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		sprite.setRotation(degree);
	}

	@Override
	public float getHeight() {
		return sprite.getHeight();
	}

	@Override
	public float getWidth() {
		return sprite.getWidth();
	}

	@Override
	public float getRotation() {
		return sprite.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return sprite.getPosition();
	}
	@Override
	public LayerObjectType typeUuid() {
		return TYPE_UUID;
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("tile", tileId);
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("rotation", getRotation());
		map.put("width", getWidth());
		map.put("height", getHeight());
		
		return map;
	}
	
	public static SpriteLayerObject create(IGameMap map, WorldId worldId, Map<String, Object> attributes) {
		try {
			return new SpriteLayerObject(
					map.getTileset(),
					((Number)attributes.get("tile")).intValue(), 
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("rotation")).floatValue(),
					((Number)attributes.get("width")).floatValue(),
					((Number)attributes.get("height")).floatValue());
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}
}
