package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.physics.PhysicsBody;

public class TestCharacter extends PhysicsBody implements LayerObject{
	
	public static final LayerObjectType TYPE_UUID = LayerObjectType.getByType(TestCharacter.class);
	
	private RectangleShape shape = null;
	
	private float resetTimer = 0.f;
	public TestCharacter(int gameWorldId, float x, float y, float width, float height, float rotation) {
		super(gameWorldId);
				
		this.shape = new RectangleShape(new Vector2f(width, height));
		shape.setPosition(x, y);
		shape.setFillColor(Color.WHITE);
		shape.setOutlineColor(Color.BLACK);
		shape.setOutlineThickness(2f);
		shape.setOrigin( shape.getSize().x/2, shape.getSize().y/2);
		shape.setRotation(rotation);
		
		createBody(x, y, width, height, false);
		//body.setFixedRotation(true);
	}
	
	public void update(float dt, GameContext ctx)
	{
		processMovement(dt);
		// convert box2d position(0.01pixel is 1 unit) to the position in our coordinate system(1 pixel is 1 unit)
		float sf = GameMap.BOX2D_SCALE_FACTOR;
		shape.setPosition(new Vector2f(body.getPosition().x/sf, body.getPosition().y/sf));
		shape.setRotation((float) Math.toDegrees(body.getAngle()));
		
		// set view
		ConstView cView = ctx.window.getView();
		Vector2f s = cView.getSize();
		ctx.window.setView(new View(new Vector2f(getPosition().x, cView.getCenter().y), new Vector2f(s.x, s.y)));
	}
	
	private void processMovement(float dt)
	{
		resetTimer += dt;
		if( resetTimer >= GameMap.FIXED_STEP )
		{
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
		    
		    if( nearEqual(body.getLinearVelocity().y ,0.f) && jump )
		    	y -= 1.7f;
		    
		    if( body.getLinearVelocity().y > 0)
		    	y += 0.05f;
		    
		    move(x, y);
		    resetTimer = 0.f;
		}
	}
	
	private boolean nearEqual(float a, float b)
	{
		return Math.abs(a - b) < 0.0001f; 
	}
	
	public void move(float pX, float pY)
	{
		body.setLinearVelocity(new Vec2(0, body.getLinearVelocity().y));
		body.applyLinearImpulse(new Vec2(pX, pY), body.getWorldCenter());
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

	@Override
	public LayerObject copy() {
		return new TestCharacter(gameWorldId, getPosition().x, getPosition().y, getWidth(), getHeight(), getRotation());
	}

	@Override
	protected void beginContact(PhysicsBody with) {
		shape.setFillColor(Color.RED);
		
	}

	@Override
	protected void endContact(PhysicsBody with) {
		shape.setFillColor(Color.WHITE);
	}

	public static TestCharacter create(GameMap map, int worldId, Map<String, Object> attributes) {
		try {
			return new TestCharacter(
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
	public void onGameWorldSwitch(int gameWorldId) {
		this.gameWorldId = gameWorldId;
	}

}
