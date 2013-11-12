package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.Tileset;

/**
 * TODO: tinting
 * @author lowkey
 *
 */
public class SpriteLayerObject implements LayerObject {

	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(SpriteLayerObject.class);
	
	private Sprite sprite;
	
	private Tileset tileset;
	private int tileId;
	
	public SpriteLayerObject(Sprite sprite) {
		this.sprite = sprite;
	}
	public SpriteLayerObject(Tileset tileset, int tileId, float x, float y, float rotation) {
		this(tileset, tileId, x, y, rotation, 0, 0);
	}
	public SpriteLayerObject(Tileset tileset, int tileId, float x, float y, float rotation, float height, float width) {
		this.tileset = tileset;
		this.tileId = tileId;
		sprite = new Sprite();
		sprite.setTexture(tileset.tiles.get(tileId));
		sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
		sprite.setPosition(x, y);
		sprite.setRotation(rotation);
		setDimensions(height>0?height:sprite.getTexture().getSize().y, width>0?width:sprite.getTexture().getSize().x);
	}
	
	public void setTile(Tileset tileset, int tileId) {
		this.tileset = tileset;
		this.tileId = tileId;
		sprite.setTexture(tileset.tiles.get(tileId));
		sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		rt.draw(sprite);
	}
	
	@Override
	public void setDimensions(float height, float width) {
		sprite.setScale(width/sprite.getTexture().getSize().x, height/sprite.getTexture().getSize().y);
	}

	@Override
	public boolean inside(Vector2f point) {
		return sprite.getGlobalBounds().contains(point);
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
	public int getHeight() {
		return (int) (sprite.getTexture().getSize().y * sprite.getScale().y);
	}

	@Override
	public int getWidth() {
		return (int) (sprite.getTexture().getSize().x * sprite.getScale().x);
	}

	@Override
	public Vector2f getOrigin() {
		return sprite.getOrigin();
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
	public LayerObject copy() {
		return new SpriteLayerObject(tileset, tileId, sprite.getPosition().x, sprite.getPosition().y, sprite.getRotation(), getHeight(), getWidth());
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
		map.put("height", getHeight());
		map.put("width", getWidth());
		
		return map;
	}
	public static SpriteLayerObject create(GameMap map, int worldId, Map<String, Object> attributes) {
		try {
			return new SpriteLayerObject(
					map.getTileset(),
					((Number)attributes.get("tile")).intValue(), 
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("rotation")).floatValue(),
					((Number)attributes.get("height")).intValue(),
					((Number)attributes.get("width")).intValue() );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}
}
