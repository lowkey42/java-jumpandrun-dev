package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.LayerObject;

public class CollisionObject implements LayerObject {

	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(CollisionObject.class);
	
	public static enum CollisionType {
		NORMAL(new Color(255, 100, 100, 255)), ONE_WAY(new Color(180, 180, 180, 255)), NO_GRAV(new Color(100, 100, 255, 255));
		
		final Color fillColor;
		private CollisionType(Color fillColor) {
			this.fillColor = fillColor;
		}
		public CollisionType next() {
			CollisionType ct[] = values();
			return ct[ (ordinal()+1)%ct.length ];
		}
		public CollisionType prev() {
			CollisionType ct[] = values();
			return ct[ ordinal()==0 ? ct.length-1 : (ordinal()-1)%ct.length ]; 
		}
		public static CollisionType first() {
			return values()[0];
		}
	}
	
	private RectangleShape shape;
	
	private CollisionType type;

	public CollisionObject(CollisionType type, float x, float y, float height, float width, float rotation) {
		this.type = type;
		this.shape = new RectangleShape(new Vector2f(width, height));
		shape.setPosition(x, y);
		shape.setFillColor(type.fillColor);
		shape.setOutlineColor(Color.YELLOW);
		shape.setOutlineThickness(2);
		shape.setOrigin( shape.getSize().x/2, shape.getSize().y/2);
		shape.setRotation(rotation);
	}
	
	public void setType(CollisionType type) {
		this.type = type;
		shape.setFillColor(type.fillColor);
	}
	public CollisionType getType() {
		return type;
	}
	
	@Override
	public void draw(RenderTarget rt) {
		rt.draw(shape);
	}

	@Override
	public boolean inside(Vector2f point) {
		return shape.getGlobalBounds().contains(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		shape.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		shape.setRotation(degree);
	}

	@Override
	public int getHeight() {
		return (int) shape.getSize().y;
	}

	@Override
	public int getWidth() {
		return (int) shape.getSize().x;
	}

	@Override
	public Vector2f getOrigin() {
		return shape.getOrigin();
	}

	@Override
	public float getRotation() {
		return shape.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return shape.getPosition();
	}

	@Override
	public void setDimensions(float height, float width) {
		shape.setSize(new Vector2f(Math.max(width, 1), Math.max(height, 1)));
		shape.setOrigin(shape.getSize().x/2, shape.getSize().y/2);
	}

	@Override
	public LayerObject copy() {
		return new CollisionObject(type, getPosition().x, getPosition().y, getHeight(), getWidth(), getRotation());
	}

	@Override
	public LayerObjectType typeUuid() {
		return TYPE_UUID;
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type.name());
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("rotation", getRotation());
		map.put("height", getHeight());
		map.put("width", getWidth());
		
		return map;
	}
	public static CollisionObject create(GameMap map, int worldId, Map<String, Object> attributes) {
		try {
			return new CollisionObject(
					CollisionType.valueOf((String)attributes.get("type")), 
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("height")).intValue(),
					((Number)attributes.get("width")).intValue(),
					((Number)attributes.get("rotation")).floatValue() );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}

}
