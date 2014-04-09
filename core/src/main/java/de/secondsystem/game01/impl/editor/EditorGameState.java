package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.objects.IEditorLayerObject;
import de.secondsystem.game01.impl.editor.objects.EditorEntity;
import de.secondsystem.game01.impl.editor.objects.EditorLayerObject;
import de.secondsystem.game01.impl.editor.objects.EditorLightObject;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.EntityLayerObject;
import de.secondsystem.game01.impl.map.objects.LightLayerObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

/**
 * 
 * 
 * Controlls: wasd: move L-Click: place object Wheel: change object-type Shift
 * +/-: rotate 1-9: change layer +/-: zoom shift + mousewheel: scale tile scale
 * selected objects with the mouse by clicking the marked area on the left/top
 * sides
 * 
 * @author lowkey
 * 
 */
public final class EditorGameState extends GameState {

	private static final float CAM_MOVE_SPEED = 5.f;
	private RenderWindow window;

	private final GameState playGameState;
	private GameMap map;
	private Tileset tileset;
	
	private IEditorLayerObject editorObject;
	
	private boolean moveSelectedObject = false;
	
	private float zoom = 1.f;
	
	private float cameraX = 0.f;
	private float cameraY = 0.f;
	
	private LayerType currentLayer = LayerType.FOREGROUND_0;

	private String mapToLoad=null;
	
	private EditorGUI editorInterface;
	
	private EditorGameState(GameState playGameState, GameMap map, String mapId) {
		this.playGameState = playGameState;
		this.map = map;
		mapToLoad = mapId;
	}
	public static EditorGameState createLive(GameState playGameState, GameMap map) {
		return new EditorGameState(playGameState, map, null);
	}
	public static EditorGameState create(GameState playGameState, String mapId) {
		return new EditorGameState(playGameState, null, mapId);
	}
	public static EditorGameState create(String mapId) {
		return new EditorGameState(null, null, mapId);
	}

	@Override
	protected void onStart(GameContext ctx) {
		window = ctx.window;
		
		if(map==null && mapToLoad!=null) {
			IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
			map = mapSerializer.deserialize(ctx, mapToLoad, null, true, true);
			mapToLoad = null;
		}
		
		this.tileset = map.getTileset();
		
		editorObject = new EditorLayerObject(map, tileset, false);
		
		editorInterface = new EditorGUI(0, 0, ctx.getViewWidth(), ctx.getViewHeight());
		
		editorInterface.setLayerHint(map, currentLayer);
	}

	@Override
	protected void onStop(GameContext ctx) {
		editorObject.deselect();
	}

	private View getTransformedView(GameContext ctx) {
		Vector2f ws = ctx.window.getView().getSize();
		View view = new View(Vector2f.mul(new Vector2f(cameraX + ws.x / 2, cameraY + ws.y
				/ 2), currentLayer.parallax), Vector2f.div(ws, zoom));
		return view;
	}

	private int getMouseX() {
		return Mouse.getPosition(window).x;
	}

	private int getMouseY() {
		return Mouse.getPosition(window).y;
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		map.getEntityManager().update(frameTime);
		
		final ConstView cView = ctx.window.getView();
		
		ctx.window.setView(getTransformedView(ctx));
		editorObject.update(moveSelectedObject, ctx.window, getMouseX(), getMouseY(), zoom, frameTime);
		ctx.window.setView(cView);
		
		drawMap(ctx.window);
		
		processInputKeyboard();
	}
	
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		if (event.type == Event.Type.KEY_RELEASED
				&& event.asKeyEvent().key == Key.F12) {
			setNextState(playGameState);

		} else if (event.type == Event.Type.KEY_RELEASED
				&& event.asKeyEvent().key == Key.ESCAPE) {
			setNextState(new MainMenuState(this));
		} else
			processInput(ctx, event);
	}

	private final void drawMap(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.setView(new View(new Vector2f(cameraX + cView.getSize().x / 2, cameraY + cView.getSize().y / 2), Vector2f.div(cView.getSize(), zoom)));
		map.draw(rt);

		rt.setView(new View(Vector2f.mul(rt.getView().getCenter(), currentLayer.parallax), rt.getView().getSize()));
		
		editorObject.refresh();
		editorObject.draw(rt);

		rt.setView(cView);
		
		editorInterface.draw(rt);
	}

	private final boolean processInput(GameContext ctx, Event event) {
		switch (event.type) {
		
		case KEY_PRESSED:
			return processInputKey(ctx, event.asKeyEvent());

		case MOUSE_WHEEL_MOVED:
			if (event.asMouseWheelEvent().delta == 0)
				return true;

			int offset = event.asMouseWheelEvent().delta < 0 ? -1 : 1;

			if (Keyboard.isKeyPressed(Key.LSHIFT)) {
				editorObject.zoom(offset, event.asMouseWheelEvent().delta);
			} else {
				if( editorObject.inMouseState() )
					editorObject.changeSelection(offset);
			}

			return true;

		case MOUSE_BUTTON_RELEASED:
			switch (event.asMouseButtonEvent().button) {
			case LEFT:
				moveSelectedObject = false;

				if( editorObject.inMouseState() ) 
					editorObject.addToMap(currentLayer);

				return true;

			case RIGHT:
				editorObject.repeatTexture(false);
				
				if( !editorObject.inMouseState() ) {
					editorObject.resetScalingDirection();
				
					if( editorObject.isScaling() ) {
						editorObject.setScaling(false);	
						return true;
					}
				}
				
				View view = getTransformedView(ctx);
				
				ILayerObject layerObject = map.findNode(currentLayer, ctx.window.mapPixelToCoords(new Vector2i(getMouseX(), getMouseY()), view));
				
				if( layerObject != null ) {
					if( editorObject.inMouseState() )
						editorObject.deselect();

					if( layerObject instanceof EntityLayerObject )
						editorObject = new EditorEntity(Color.BLUE, 2.f, Color.TRANSPARENT, map);
					else if( layerObject instanceof SpriteLayerObject || layerObject instanceof CollisionObject )
						editorObject = new EditorLayerObject(Color.BLUE, 2.f, Color.TRANSPARENT, map);
					else if( layerObject instanceof LightLayerObject )
 						editorObject = new EditorLightObject(Color.BLUE, 2.f, Color.TRANSPARENT, map);
					
					editorObject.setLayerObject( layerObject );
					
					editorInterface.onObjectSelection(editorObject);
				}
				else {
					deselectSprite();
				}
				
				return true;
			default:
				return false;
			}

		case MOUSE_BUTTON_PRESSED:
			View view = getTransformedView(ctx);
			Vector2f v = ctx.window.mapPixelToCoords(new Vector2i(getMouseX(), getMouseY()), view);
			switch (event.asMouseButtonEvent().button) {
			case LEFT:
				if ( editorObject.inside(v) )
					moveSelectedObject = true;

				return true;
			case RIGHT:
				if( !editorObject.inMouseState() ) {
					if( Keyboard.isKeyPressed(Key.LSHIFT) ) {
						editorObject.setRepeatedTexture(true);
						editorObject.repeatTexture(true);
					}
					editorObject.checkScaleMarkers(v);
					editorObject.setLastMappedMousePos( new Vector2f(v.x, v.y) );
				}
			default:
				return false;
			}

		default:
			return false;
		}
	}

	private void deselectSprite() {
		editorObject.deselect();
		
		switch( currentLayer ) {
		case PHYSICS:
			editorObject = new EditorLayerObject(map, tileset, true);
			break;
		case OBJECTS:
			editorObject = new EditorEntity(map);
			break;
		case LIGHTS:
			editorObject = new EditorLightObject();
			break;
		default:
			editorObject = new EditorLayerObject(map, tileset, false);
		}
		
		editorObject.create(map);
	}

	private final boolean processInputKeyboard() {
		if (Keyboard.isKeyPressed(Key.W))
			cameraY -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.S))
			cameraY += CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.A))
			cameraX -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.D))
			cameraX += CAM_MOVE_SPEED;

		if (Keyboard.isKeyPressed(Key.LEFT)) 
			if( editorObject instanceof EditorLightObject) {
				float r = ((EditorLightObject) editorObject).getRadius();
				if( r > 0 )
					((EditorLightObject) editorObject).setRadius(r - 1);
			}
			else
				if( editorObject.getWidth() > 0 )
					editorObject.setWidth(editorObject.getWidth() -1);
		
		if (Keyboard.isKeyPressed(Key.RIGHT))
			if( editorObject instanceof EditorLightObject) {
				float r = ((EditorLightObject) editorObject).getRadius();
				((EditorLightObject) editorObject).setRadius(r + 1);
			}
			else
				editorObject.setWidth(editorObject.getWidth() +1);
		
		if (Keyboard.isKeyPressed(Key.UP))
			editorObject.setHeight(editorObject.getHeight() +1);
		if (Keyboard.isKeyPressed(Key.DOWN) && editorObject.getHeight() > 0)
			editorObject.setHeight(editorObject.getHeight() -1);

		return true;
	}
	
	
	private final boolean processInputKey(GameContext ctx, KeyEvent event) {
		switch (event.key) {
		case F5: // save
			new JsonGameMapSerializer().serialize(map);
			break;

		case F9: // load
			map = new JsonGameMapSerializer().deserialize(ctx, map.getMapId(), null, false, 
					true);
			break;

		case TAB: // switch world
			map.switchWorlds();
			break;

		case DELETE: // delete selected object
			if (editorObject.getLayerObject() != null) {
				editorObject.removeFromMap(map, currentLayer);
				deselectSprite();
			}
			break;

		case PAGEUP:
		case ADD: 
			if (event.shift) // rotate object
				editorObject.rotate(11.25f);
			else if (event.control) // scale up selected object
				editorObject.zoom(2.f);
			else
				zoom *= 2;
			break;

		case PAGEDOWN:
		case SUBTRACT:
			if (event.shift) // rotate object
				editorObject.rotate(-11.25f);
			else if (event.control) // scale down selected object
				editorObject.zoom(1/2.f);
			else
				zoom /= 2;
			break;

		case NUM1:
			if (event.control) // toggle background 2 visibility
				map.flipShowLayer(LayerType.BACKGROUND_2);
			else { // select background 2
				currentLayer = LayerType.BACKGROUND_2; 
				deselectSprite();
			}
			break;
		case NUM2:
			if (event.control) // toggle background 1 visibility
				map.flipShowLayer(LayerType.BACKGROUND_1);
			else { // select background 1
				currentLayer = LayerType.BACKGROUND_1;
				deselectSprite();
			}
			break;
		case NUM3:
			if (event.control) // toggle background 0 visibility
				map.flipShowLayer(LayerType.BACKGROUND_0);
			else { // select background 0
				currentLayer = LayerType.BACKGROUND_0;
				deselectSprite();
			}
			break;

		case NUM4:
			if (event.control) // toggle foreground 0 visibility
				map.flipShowLayer(LayerType.FOREGROUND_0);
			else { // select foreground 0
				currentLayer = LayerType.FOREGROUND_0;
				deselectSprite();
			}
			break;
		case NUM5:
			if (event.control) // toggle foreground 1 visibility
				map.flipShowLayer(LayerType.FOREGROUND_1);
			else { // select foreground 1
				currentLayer = LayerType.FOREGROUND_1;
				deselectSprite();
			}
			break;

		case P:
			if (event.control) // toggle collision layer visibility
				map.flipShowLayer(LayerType.PHYSICS);
			else { // select collision layer
				currentLayer = LayerType.PHYSICS;
				deselectSprite();
			}
			break;

		case O:
			if (event.control) // toggle object layer visibility
				map.flipShowLayer(LayerType.OBJECTS);
			else { // select object layer
				currentLayer = LayerType.OBJECTS;
				deselectSprite();
			}
			break;
			
		case L:
			if(event.control) // toggle light layer visibility
				map.flipShowLayer(LayerType.LIGHTS);
			else { // select light layer
				currentLayer = LayerType.LIGHTS;
				deselectSprite();
			}

		default:
		}

		editorInterface.setLayerHint(map, currentLayer);
		
		return true;
		
	}

}
