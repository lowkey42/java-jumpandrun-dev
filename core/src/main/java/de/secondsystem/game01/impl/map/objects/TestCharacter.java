package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.IWorldSwitchListener;
import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody.ContactListener;
import de.secondsystem.game01.model.IUpdateable;

@Deprecated
public class TestCharacter implements LayerObject, IUpdateable, ICameraController, ContactListener, IWorldSwitchListener {
	
	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(TestCharacter.class);
	
	private RectangleShape shape = null;
	
	private IPhysicsBody physicsBody;
	
	private boolean moving = false;
	private float jumpTimer = 0.f;
	
	public TestCharacter(IGameMap map, int gameWorldId, float x, float y, float width, float height, float rotation) {
		this.shape = new RectangleShape(new Vector2f(width, height));
		shape.setPosition(x, y);
		shape.setFillColor(Color.WHITE);
		shape.setOutlineColor(Color.BLACK);
		shape.setOutlineThickness(2f);
		shape.setOrigin( shape.getSize().x/2, shape.getSize().y/2);
		shape.setRotation(rotation);
		
		if( map.getPhysicalWorld()!=null ) {
			physicsBody = map.getPhysicalWorld().createBody(gameWorldId, x, y, width, height, rotation, false, CollisionHandlerType.SOLID, true);
			physicsBody.setMaxVelocityX(5);
			physicsBody.setContactListener(this);
		}
		
		map.registerWorldSwitchListener(this);	// TODO: have to be deregistered
	}
	
	@Override
	public void endContact(IPhysicsBody other) {
		shape.setFillColor(Color.RED);
	}
	
	@Override
	public void beginContact(IPhysicsBody other) {
		shape.setFillColor(Color.WHITE);
	}

	@Override
	public void update(long frameTime) {
		jumpTimer += frameTime/1000.f;
		processMovement(frameTime/1000.f);	// TODO: move somewhere else
		
		if( physicsBody!=null ) {
			shape.setPosition(physicsBody.getPosition());
			shape.setRotation(physicsBody.getRotation());
		}
	}
	
	private void processMovement(float dt) {
	    float x = 0;
	    float y = 0;
	    
	    boolean moveRight = Keyboard.isKeyPressed(Keyboard.Key.D);
	    boolean moveLeft  = Keyboard.isKeyPressed(Keyboard.Key.A);
	    boolean jump      = Keyboard.isKeyPressed(Keyboard.Key.SPACE);
	    
	    if( moveRight )
	    	x += 0.5f;
	    else
	    	if( moveLeft )
	    		x -= 0.5f;
	    
	    if( jump && physicsBody.isStable() && jumpTimer >= 100.f)
	    {
	    	y -= 1.7f;
	    	jumpTimer = 0.f;
	    }
	 
	    //if( physicsBody.getVelocity().y > 0)
	    	//y += 0.05f;
	    
	    physicsBody.move(x*5, y*30);
	    
	    if( x!=0 )
	    	moving = true;
	    else if( moving )
	    	physicsBody.resetVelocity(true, false, false);
	    
	    
	    final float r = physicsBody.getRotation();
	    if( r<340 && r>20 ) {
	    	physicsBody.forceRotation( r<180 ? 20  : 340 );
	    	physicsBody.resetVelocity(false, false, true);
	    	
	    }
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
	public float getHeight() {
		return (int) shape.getSize().y;
	}

	@Override
	public float getWidth() {
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
	public void setDimensions(float width, float height) {
		shape.setSize(new Vector2f(Math.max(width, 1), Math.max(height, 1)));
		shape.setOrigin(shape.getSize().x/2, shape.getSize().y/2);
	}

	public static TestCharacter create(IGameMap map, int worldId, Map<String, Object> attributes) {
		try {
			return new TestCharacter(
					map,
					worldId,
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("width")).floatValue(),
					((Number)attributes.get("height")).floatValue(),
					((Number)attributes.get("rotation")).floatValue() );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}
	
	@Override
	public LayerObjectType typeUuid() {
		return TYPE_UUID;
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("rotation", getRotation());
		map.put("width", getWidth());
		map.put("height", getHeight());
		
		return map;
	}

	@Override
	public Vector2f getLastStablePosition() {
		return null;	// TODO: implement for lazy-camera
	}

	@Override
	public void onWorldSwitch(int newWorldId) {
		physicsBody.setGameWorldId(newWorldId);
	}

}
