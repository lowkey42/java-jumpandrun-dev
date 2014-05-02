package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsBodyShape;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class CollisionObject implements ILayerObject {

	public static final LayerObjectType TYPE_UUID = LayerObjectType.COLLISION;
	
	public static enum CollisionType {
		NORMAL	(CollisionHandlerType.SOLID, new Color(255, 10, 10, 100)), 
		ONE_WAY	(CollisionHandlerType.ONE_WAY, new Color(10, 10, 255, 100)), 
		NO_GRAV	(CollisionHandlerType.CLIMBABLE, new Color(10, 255, 10, 100));

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
	
	private int worldMask;

	public CollisionObject(IGameMap map, int worldMask, CollisionType type, float x, float y, float width, float height, float rotation) {
		this.type = type;
		this.worldMask = worldMask;
		
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
				.worldMask(worldMask)
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
	
	@Override
	public LayerType getLayerType() {
		return LayerType.PHYSICS;
	}
	@Override
	public void setLayerType(LayerType layerType) {
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
		return shape.getGlobalBounds().contains(point); // checks the AABB not the OBB
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
	public boolean isInWorld(WorldId worldId) {
		return (worldMask & worldId.id)!=0;
	}

	@Override
	public void setWorld(WorldId worldId, boolean exists) {
		if( physicsBody!=null )
			physicsBody.setWorld(worldId, exists);
		
		if( exists )
			worldMask|=worldId.id;
		else
			worldMask&=~worldId.id;
	}

	@Override
	public LayerObjectType typeUuid() {
		return TYPE_UUID;
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute("$type", typeUuid().shortId),
				new Attribute("world", worldMask),
				new Attribute("type", type.name()),
				new Attribute("x", getPosition().x),
				new Attribute("y", getPosition().y),
				new Attribute("rotation", getRotation()),
				new Attribute("width", getWidth()),
				new Attribute("height", getHeight())
		);
	}
	
	static final class Factory implements ILayerObjectFactory {
		@Override
		public CollisionObject create(IGameMap map, Attributes attributes) {
			try {
				return new CollisionObject(
						map,
						attributes.getInteger("world"),
						CollisionType.valueOf(attributes.getString("type")), 
						attributes.getFloat("x"),
						attributes.getFloat("y"),
						attributes.getFloat("width"),
						attributes.getFloat("height"),
						attributes.getFloat("rotation") );
			
			} catch( ClassCastException | NullPointerException e ) {
				throw new Error( "Invalid attributes: "+attributes, e );
			}
		}
	}

}
