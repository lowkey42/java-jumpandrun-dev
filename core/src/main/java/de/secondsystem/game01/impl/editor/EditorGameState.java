package de.secondsystem.game01.impl.editor;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
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
import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;

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
	private final Tileset tileset;

	private final Text editorHint;
	private final Text layerHint;
	private MouseEditorObject mouseTile;
	private SelectedEditorObject selectedObject;
	private float zoom = 1.f;
	
	private float cameraX = 0.f;
	private float cameraY = 0.f;
	
	private LayerType currentLayer = LayerType.FOREGROUND_0;


	private boolean moveSelectedObject = false;
	
	
	public EditorGameState(GameState playGameState, GameMap map) {
		this.playGameState = playGameState;
		this.map = map; // TODO: copy
		this.tileset = new Tileset("test01"); // TODO: get from map

		ConstFont freeSans;
		try {
			freeSans = ResourceManager.font.get("FreeSans.otf");

		} catch (IOException ex) {
			throw new Error(ex);
		}

		editorHint = new Text("EDITOR: ${mapName}", freeSans);
		editorHint.setPosition(0, 0);
		editorHint.setColor(Color.RED);

		layerHint = new Text(generateLayerHintStr(), freeSans);
		layerHint.setPosition(0, 25);
		layerHint.setColor(Color.WHITE);
		
		mouseTile      = new MouseEditorObject(tileset);
		selectedObject = new SelectedEditorObject(Color.BLUE, 2.0f, Color.TRANSPARENT);
	}

	@Override
	protected void onStart(GameContext ctx) {
		window = ctx.window;
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO Auto-generated method stub

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
		final ConstView cView = ctx.window.getView();
		
		ctx.window.setView(getTransformedView(ctx));
		EditorObject currentEditorObject = selectedObject.getLayerObject() != null ? selectedObject : mouseTile;
		currentEditorObject.update(moveSelectedObject, ctx.window, getMouseX(), getMouseY(), zoom);
		ctx.window.setView(cView);
		
		drawMap(ctx.window);

		ctx.window.draw(editorHint);
		ctx.window.draw(layerHint);

		processInputKeyboard();

		for (Event event : ctx.window.pollEvents()) {
			if (event.type == Event.Type.CLOSED) {
				ctx.window.close();

			} else if (event.type == Event.Type.KEY_RELEASED
					&& event.asKeyEvent().key == Key.F12) {
				setNextState(playGameState);

			} else if (event.type == Event.Type.KEY_RELEASED
					&& event.asKeyEvent().key == Key.ESCAPE) {
				setNextState(new MainMenuState(this));
			} else
				processInput(ctx, event);
		}
	}

	private final void drawMap(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.setView(new View(new Vector2f(cameraX + cView.getSize().x / 2, cameraY + cView.getSize().y / 2), Vector2f.div(cView.getSize(), zoom)));
		map.draw(rt);

		rt.setView(new View(Vector2f.mul(rt.getView().getCenter(), currentLayer.parallax), rt.getView().getSize()));

		EditorObject currentEditorObject = selectedObject.getLayerObject() != null ? selectedObject : mouseTile;
		
		currentEditorObject.refresh();
		currentEditorObject.draw(rt);

		rt.setView(cView);
	}

	private final boolean processInput(GameContext ctx, Event event) {
		EditorObject currentEditorObject = selectedObject.getLayerObject() != null ? selectedObject : mouseTile;
		
		switch (event.type) {
		
		case KEY_PRESSED:
			return processInputKey(event.asKeyEvent());

		case MOUSE_WHEEL_MOVED:
			if (event.asMouseWheelEvent().delta == 0)
				return true;

			int offset = event.asMouseWheelEvent().delta < 0 ? -1 : 1;

			if (Keyboard.isKeyPressed(Key.LSHIFT)) {
				currentEditorObject.zoom(offset, event.asMouseWheelEvent().delta);
			} else {
				if( mouseTile != null )
					mouseTile.changeTile(offset);
			}

			return true;

		case MOUSE_BUTTON_RELEASED:
			switch (event.asMouseButtonEvent().button) {
			case LEFT:
				moveSelectedObject = false;

				if (selectedObject.getLayerObject() == null) 
					mouseTile.addToMap(map, currentLayer);

				return true;

			case RIGHT:
				selectedObject.resetScalingPermission();
				Vector2f ws = ctx.window.getView().getSize();
				View view = new View(Vector2f.mul(new Vector2f(cameraX + ws.x / 2, cameraY + ws.y / 2), currentLayer.parallax), Vector2f.div(ws, zoom));

				selectedObject.setLayerObject( map.findNode(currentLayer, ctx.window.mapPixelToCoords(new Vector2i(getMouseX(), getMouseY()), view)) );

				if (selectedObject.getLayerObject() == null) 
					deselectSprite();

			default:
				return false;
			}

		case MOUSE_BUTTON_PRESSED:
			View view = getTransformedView(ctx);
			Vector2f v = ctx.window.mapPixelToCoords(new Vector2i(getMouseX(), getMouseY()), view);
			switch (event.asMouseButtonEvent().button) {
			case LEFT:
				if ( selectedObject.isPointInside(v) )
					moveSelectedObject = true;

				return true;
			case RIGHT:
				selectedObject.checkScaleMarkers(v);
				selectedObject.setLastMappedMousePos( new Vector2f(v.x, v.y) );
			default:
				return false;
			}

		default:
			return false;
		}
	}

	private void deselectSprite() {
		if (currentLayer == LayerType.PHYSICS)
			mouseTile.createCollisionObject(map);
		else
			mouseTile.createSpriteObject();
	}

	private final boolean processInputKeyboard() {
		EditorObject currentEditorObject = selectedObject.getLayerObject() != null ? selectedObject : mouseTile;
		
		if (Keyboard.isKeyPressed(Key.W))
			cameraY -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.S))
			cameraY += CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.A))
			cameraX -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.D))
			cameraX += CAM_MOVE_SPEED;

		if (Keyboard.isKeyPressed(Key.LEFT) && currentEditorObject.getWidth() > 0)
			currentEditorObject.setWidth(currentEditorObject.getWidth() -1);
		if (Keyboard.isKeyPressed(Key.RIGHT))
			currentEditorObject.setWidth(currentEditorObject.getWidth() +1);
		if (Keyboard.isKeyPressed(Key.UP))
			currentEditorObject.setHeight(currentEditorObject.getHeight() +1);
		if (Keyboard.isKeyPressed(Key.DOWN) && selectedObject.getHeight() > 0)
			currentEditorObject.setHeight(currentEditorObject.getHeight() -1);

		return true;
	}
	
	
	private final boolean processInputKey(KeyEvent event) {
		EditorObject currentEditorObject = selectedObject != null ? selectedObject : mouseTile;
		
		switch (event.key) {
		case F5: // save
			new JsonGameMapSerializer().serialize(map);
			break;

		case F9: // load
			map = new JsonGameMapSerializer().deserialize(map.getMapId(), true,
					true);
			break;

		case TAB: // switch world
			map.switchWorlds();
			break;

		case DELETE: // delete selected object
			if (selectedObject.getLayerObject() != null) {
				selectedObject.removeFromMap(map, currentLayer);
				deselectSprite();
			}
			break;

		case PAGEUP:
		case ADD: 
			if (event.shift) // rotate object
				currentEditorObject.rotate(11.25f);
			else if (event.control) // scale up selected object
				currentEditorObject.zoom(2.f);
			else
				zoom *= 2;
			break;

		case PAGEDOWN:
		case SUBTRACT:
			if (event.shift) // rotate object
				currentEditorObject.rotate(-11.25f);
			else if (event.control) // scale down selected object
				currentEditorObject.zoom(1/2.f);
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
			else { // selection object layer
				currentLayer = LayerType.OBJECTS;
				deselectSprite();
			}
			break;

		default:
		}

		layerHint.setString(generateLayerHintStr());

		return true;
		
	}

	private String generateLayerHintStr() {
		boolean[] s = map.getShownLayer();

		StringBuilder str = new StringBuilder();

		for (LayerType l : LayerType.values()) {
			if (currentLayer == l)
				str.append("=").append(l.name).append("=");
			else
				str.append(l.name);

			str.append(s[l.layerIndex] ? "[X]" : "[ ]");

			str.append("\t");
		}

		return str.toString();
	}

}
