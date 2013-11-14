package de.secondsystem.game01.impl.map;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.physics.PhysicsContactFilter;
import de.secondsystem.game01.impl.map.physics.PhysicsContactListener;

public class GameMap {
	
	// ADDED physics world // TODO: REMOVE COMMENT
	// RENAMED World into GameWorld ! reason: World is a class of box2d // TODO: REMOVE COMMENT
	public static final class GameWorld {
		final Layer[] graphicLayer;
		Color backgroundColor;
		
		GameWorld() {
			graphicLayer = new Layer[LayerType.LAYER_COUNT];
			for( LayerType l : LayerType.values() )
				graphicLayer[l.layerIndex] = new Layer(l);
			
			backgroundColor = Color.BLACK;
		}
		void addNode( LayerType layer, LayerObject sprite ) {
			addNode(layer.layerIndex, sprite);
		}
		void addNode( int layerIndex, LayerObject sprite ) {
			graphicLayer[layerIndex].addNode(sprite);
		}
	}
	
	private String mapId;
	
	final GameWorld gameWorld[] = new GameWorld[2];
	
	private Tileset tileset;
	
	// RENAMED // TODO: REMOVE COMMENT
	private int activeGameWorldId;
	
	final boolean editable;
	
	final boolean playable;
	
	// time variables
	public static final float FIXED_STEP = 1/60f;
	private final int maxSteps = 5;
	private float fixedTimestepAccumulator = 0;
	
	// physics variables
	public static World physicsWorld = null;
	private final int velocityIterations = 8;
	private final int positionIterations = 3;
	public static final float BOX2D_SCALE_FACTOR = 0.01f;
	
	float resetTimer = 0.f;
	
	private void createPhysicsWorld()
	{
		Vec2 gravity = new Vec2(0.0f, 10.0f);
		physicsWorld = new World(gravity);
		physicsWorld.setSleepingAllowed(true);
		physicsWorld.setContactListener(new PhysicsContactListener());
		physicsWorld.setContactFilter(new PhysicsContactFilter());
	}
	
	public GameMap(String mapId, Tileset tileset) {	
		createPhysicsWorld();
		
		this.mapId = mapId;
		gameWorld[0] = new GameWorld();
		gameWorld[1] = new GameWorld();

		this.tileset = tileset;
		this.editable = true;
		this.playable = true;
	}
	
	GameMap(String mapId, Tileset tileset, boolean playable, boolean editable) {
		createPhysicsWorld();
		
		this.mapId = mapId;
		gameWorld[0] = new GameWorld();
		gameWorld[1] = new GameWorld();
		
		this.tileset = tileset;
		this.editable = editable;
		this.playable = playable;
		this.activeGameWorldId = 0;
	}
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}
	public Tileset getTileset() {
		return tileset;
	}
	
	// objectLayer
	// collisionLayer
	// triggerLayer
	// particleLayer
	
	
	public void switchWorlds() {
		activeGameWorldId = activeGameWorldId==0 ? 1 : 0;
		
		// ADDED // TODO: REMOVE COMMENT
		for( LayerType l : LayerType.values() ) 
			gameWorld[activeGameWorldId].graphicLayer[l.layerIndex].onGameWorldSwitch(activeGameWorldId);
	}
	
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.clear(gameWorld[activeGameWorldId].backgroundColor);
		
		for( LayerType l : LayerType.values() ) {
			Layer layer = gameWorld[activeGameWorldId].graphicLayer[l.layerIndex];
			
			// parallax explanation can be found in the class "LayerType"
			if( layer.show ) {
				if( l.parallax!=1.f )
					rt.setView( new View(Vector2f .mul(cView.getCenter(), l.parallax), cView.getSize()) );
				else
					rt.setView(cView);
				
				layer.draw(rt);
			}
		}
		
		rt.setView(cView);
	}
	
	public void processPhysics(float dt)
	{
		fixedTimestepAccumulator += dt;
	    int steps = (int) Math.floor(fixedTimestepAccumulator / FIXED_STEP);
	    if(steps > 0)
	    {
	        fixedTimestepAccumulator -= steps * FIXED_STEP;
	    }
	    
	    int stepsClamped = Math.min(steps, maxSteps);
	 
	    for (int i = 0; i < stepsClamped; ++i)
	    	physicsWorld.step(FIXED_STEP, velocityIterations, positionIterations);
	    physicsWorld.clearForces();
	}
	
	// ADDED // TODO: REMOVE COMMENT
	public int getActiveGameWorldId()
	{
		return activeGameWorldId;
	}

	public void addNode( int worldId, LayerType layer, LayerObject sprite ) {
		gameWorld[worldId].graphicLayer[layer.layerIndex].addNode(sprite);
	}
	public void addNode( LayerType layer, LayerObject sprite ) {
		addNode(activeGameWorldId, layer, sprite);
	}
	public LayerObject findNode( LayerType layer, Vector2f point ) {
		return gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].findNode(point);
	}
	public void remove( LayerType layer, LayerObject s ) {
		gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].remove(s);
	}
		
	public boolean flipShowLayer( LayerType layer ) {
		return gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].show = !gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].show;
	}
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = gameWorld[activeGameWorldId].graphicLayer[i].show;
		
		return s;
	}
	
}
