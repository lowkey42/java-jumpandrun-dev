package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsBodyShape;
import de.secondsystem.game01.util.Tools;

public class CollisionObject implements ILayerObject {

	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(CollisionObject.class);
	
	public static enum CollisionType {
		NORMAL	(CollisionHandlerType.SOLID, new Color(255, 100, 100, 150)), 
		ONE_WAY	(CollisionHandlerType.ONE_WAY, new Color(180, 180, 180, 150)), 
		NO_GRAV	(CollisionHandlerType.CLIMBABLE, new Color(100, 100, 255, 150));

		final CollisionHandlerType handlerType;
		final Color fillColor;
		
		private CollisionType(CollisionHandlerType handlerType, Color fillColor) {
			this.handlerType = handlerType;
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
	
	private final RectangleShape shape;
	
	protected final IPhysicsBody physicsBody;
	
	private CollisionType type;

	public CollisionObject(IGameMap map, WorldId worldID, CollisionType type, float x, float y, float width, float height, float rotation) {
		this.type = type;
		
		if( map.isEditable() ) {
			this.shape = new RectangleShape(new Vector2f(width, height));
			shape.setPosition(x, y);
			shape.setFillColor(type.fillColor);
			shape.setOutlineColor(Color.YELLOW);
			shape.setOutlineThickness(2);
			shape.setOrigin( shape.getSize().x/2, shape.getSize().y/2);
			shape.setRotation(rotation);
		} else 
			shape = null;
		
		if( map.getPhysicalWorld()!=null )
			physicsBody = map.getPhysicalWorld().factory()
				.inWorld(worldID)
				.position(x, y)
				.dimension(width, height)
				.rotation(rotation)
				.type(type.handlerType)
				.friction(0.5f)						// TODO: make configurable
				.restitution(0.f)					// TODO: make configurable
				.staticBody(PhysicsBodyShape.BOX)	// TODO: make configurable
				.create();
		else
			physicsBody = null;
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
		return Tools.isInside(this, point);
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
	public float getHeight() {
		return shape.getSize().y;
	}

	@Override
	public float getWidth() {
		return shape.getSize().x;
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
	public void setDimensions(float width, float height) {
		shape.setSize(new Vector2f(Math.max(width, 1), Math.max(height, 1)));
		shape.setOrigin(shape.getSize().x/2, shape.getSize().y/2);
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
		map.put("width", getWidth());
		map.put("height", getHeight());
		
		return map;
	}
	
	public static CollisionObject create(IGameMap map, WorldId worldId, Map<String, Object> attributes) {
		try {
			return new CollisionObject(
					map,
					worldId,
					CollisionType.valueOf((String)attributes.get("type")), 
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("width")).floatValue(),
					((Number)attributes.get("height")).floatValue(),
					((Number)attributes.get("rotation")).floatValue() );

		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}

}
