package de.secondsystem.game01.impl.editor;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RectangleShape;
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
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.CollisionObject.CollisionType;
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
	private static final float TILE_SCALE_FACTOR = 1.1f;
	private RenderWindow window;

	private final GameState playGameState;
	private GameMap map;
	private final Tileset tileset;

	private final Text editorHint;
	private final Text layerHint;
	private LayerObject mouseTile;

	private float x, y, zoom = 1.f;
	private int currentTile = 0;
	private float currentTileRotation = 0;
	private float currentTileZoom = 1.f;
	private float currentTileHeight = 1;
	private float currentTileWidth = 1;
	private LayerType currentLayer = LayerType.FOREGROUND_0;
	private LayerObject selectedObject;
	private RectangleShape selectedObjectMarker;
	private RectangleShape tileScaleWidthMarker;
	private RectangleShape tileScaleHeightMarker;

	private boolean tileScaleWidth;
	private boolean tileScaleHeight;

	private boolean moveSelectedObject = false;
	private Vector2f lastMousePos;

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

		createMouseSprite();

		selectedObjectMarker = createMarker(new Vector2f(1, 1), Color.BLUE,
				4.f, Color.TRANSPARENT);

		tileScaleHeightMarker = createMarker(new Vector2f(1, 1),
				Color.TRANSPARENT, 0, new Color(255, 100, 100, 150));

		tileScaleWidthMarker = createMarker(new Vector2f(1, 1),
				Color.TRANSPARENT, 0, new Color(255, 100, 100, 150));
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
		View view = new View(Vector2f.mul(new Vector2f(x + ws.x / 2, y + ws.y
				/ 2), currentLayer.parallax), Vector2f.div(ws, zoom));
		return view;
	}

	private RectangleShape createMarker(Vector2f size, Color outlineColor,
			float outlineThickness, Color fillColor) {
		RectangleShape marker = new RectangleShape(size);
		marker.setOutlineColor(outlineColor);
		marker.setOutlineThickness(outlineThickness);
		marker.setFillColor(fillColor);

		return marker;
	}

	private void setupMarker(RectangleShape marker, Vector2f size,
			Vector2f origin, Vector2f pos, float rotation) {
		marker.setSize(size);
		marker.setOrigin(origin);
		marker.setPosition(pos);
		marker.setRotation(rotation);
	}

	private void createMouseSprite() {
		if (!(mouseTile instanceof SpriteLayerObject))
			mouseTile = new SpriteLayerObject(tileset, currentTile, 0, 0, 0);
		onSpriteTileChanged();
	}

	private void createMouseCollisionObj() {
		if (!(mouseTile instanceof CollisionObject))
			mouseTile = new CollisionObject(map, map.getActiveGameWorldId(),
					CollisionType.NORMAL, 0, 0, 50, 50, 0);
		onSpriteTileChanged();
	}

	private void onSpriteTileChanged() {
		currentTileRotation = 0.f;
		currentTileZoom = 1.f;
		currentTileHeight = mouseTile.getHeight();
		currentTileWidth = mouseTile.getWidth();
	}

	private int getMouseX() {
		return Mouse.getPosition(window).x;
	}

	private int getMouseY() {
		return Mouse.getPosition(window).y;
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		drawMap(ctx.window);

		ctx.window.draw(editorHint);
		ctx.window.draw(layerHint);

		processInputKeyboard();

		for (Event event : ctx.window.pollEvents()) {
			if (event.type == Event.Type.CLOSED) {
				// The user pressed the close button
				ctx.window.close();

			} else if (event.type == Event.Type.KEY_RELEASED
					&& (event.asKeyEvent().key == Key.F12 || event.asKeyEvent().key == Key.ESCAPE)) {
				setNextState(playGameState);

			} else
				processInput(ctx, event);
		}
	}

	private final void drawMap(RenderTarget rt) {
		final ConstView cView = rt.getView();

		rt.setView(new View(new Vector2f(x + cView.getSize().x / 2, y
				+ cView.getSize().y / 2), Vector2f.div(cView.getSize(), zoom)));
		map.draw(rt);

		rt.setView(new View(Vector2f.mul(rt.getView().getCenter(),
				currentLayer.parallax), rt.getView().getSize()));
		Vector2f v = rt.mapPixelToCoords(
				new Vector2i(getMouseX(), getMouseY()), rt.getView());

		Vector2i newPos = new Vector2i(getMouseX(), getMouseY());
		LayerObject currentLayerObject = selectedObject != null ? selectedObject
				: mouseTile;
		currentLayerObject.setRotation(currentTileRotation);
		currentLayerObject.setDimensions(currentTileWidth * currentTileZoom,
				currentTileHeight * currentTileZoom);

		if (selectedObject != null) {
			if (moveSelectedObject) {
				selectedObject.setPosition(rt.mapPixelToCoords(newPos));
			}
			Vector2f newSize = new Vector2f(selectedObject.getWidth(),
					selectedObject.getHeight());
			setupMarker(selectedObjectMarker, newSize, new Vector2f(
					newSize.x / 2f, newSize.y / 2f),
					selectedObject.getPosition(), selectedObject.getRotation());
			rt.draw(selectedObjectMarker);

			// draw tile scale markers if the mouse is over
			setupMarker(tileScaleHeightMarker, new Vector2f(newSize.x, 8.f),
					new Vector2f(newSize.x / 2f, newSize.y / 2f),
					selectedObject.getPosition(), selectedObject.getRotation());

			setupMarker(tileScaleWidthMarker, new Vector2f(8.f, newSize.y),
					new Vector2f(newSize.x / 2f, newSize.y / 2f),
					selectedObject.getPosition(), selectedObject.getRotation());

			if (tileScaleHeightMarker.getGlobalBounds().contains(
					new Vector2f(v.x, v.y)))
				rt.draw(tileScaleHeightMarker);

			if (tileScaleWidthMarker.getGlobalBounds().contains(
					new Vector2f(v.x, v.y)))
				rt.draw(tileScaleWidthMarker);

			if (tileScaleWidth)
				currentTileWidth += 0.45f * (lastMousePos.x - getMouseX());

			if (tileScaleHeight)
				currentTileHeight += 0.45f * (lastMousePos.y - getMouseY());

		} else {
			mouseTile.setPosition(rt.mapPixelToCoords(newPos));
			mouseTile.draw(rt);
		}

		lastMousePos = new Vector2f(getMouseX(), getMouseY());
		rt.setView(cView);
	}

	private final boolean processInput(GameContext ctx, Event event) {
		switch (event.type) {
		case KEY_PRESSED:
			return processInputKey(event.asKeyEvent());

		case MOUSE_WHEEL_MOVED:
			if (event.asMouseWheelEvent().delta == 0)
				return true;

			int offset = event.asMouseWheelEvent().delta < 0 ? -1 : 1;

			if (Keyboard.isKeyPressed(Key.LSHIFT)) {
				if (offset == 1)
					currentTileZoom *= event.asMouseWheelEvent().delta
							* TILE_SCALE_FACTOR;
				else
					currentTileZoom /= event.asMouseWheelEvent().delta
							* TILE_SCALE_FACTOR * -1;
			} else {
				if (mouseTile instanceof SpriteLayerObject) {
					// compute currentTile; example: currentTile = 9; offset =
					// 1; tiles.size() = 10; 9+1=10 % 10 = 0 = currentTile
					int ts = tileset.tiles.size();
					currentTile += offset;
					currentTile = currentTile < 0 ? ts - 1 : currentTile % ts;

					// change the tile
					((SpriteLayerObject) mouseTile).setTile(tileset,
							currentTile);
				} else if (mouseTile instanceof CollisionObject) {
					CollisionObject co = (CollisionObject) mouseTile;
					CollisionType type = offset > 0 ? co.getType().next() : co
							.getType().prev();
					co.setType(type);
					;
				}
			}

			return true;

		case MOUSE_BUTTON_RELEASED:
			switch (event.asMouseButtonEvent().button) {
			case LEFT:
				moveSelectedObject = false;

				if (selectedObject == null) {
					map.addNode(
							currentLayer,
							mouseTile.typeUuid().create(map,
									map.getActiveGameWorldId(),
									mouseTile.getAttributes()));
				}
				return true;

			case RIGHT:
				tileScaleWidth = false;
				tileScaleHeight = false;
				Vector2f ws = ctx.window.getView().getSize();
				View view = new View(Vector2f.mul(new Vector2f(x + ws.x / 2, y
						+ ws.y / 2), currentLayer.parallax), Vector2f.div(ws,
						zoom));

				selectedObject = map.findNode(currentLayer, ctx.window
						.mapPixelToCoords(
								new Vector2i(getMouseX(), getMouseY()), view));

				if (selectedObject != null) {
					currentTileRotation = selectedObject.getRotation();
					currentTileZoom = 1.0f;
					currentTileHeight = selectedObject.getHeight();
					currentTileWidth = selectedObject.getWidth();

				} else {
					deselectSprite();
				}

			default:
				return false;
			}

		case MOUSE_BUTTON_PRESSED:
			View view = getTransformedView(ctx);
			Vector2f v = ctx.window.mapPixelToCoords(new Vector2i(getMouseX(),
					getMouseY()), view);
			switch (event.asMouseButtonEvent().button) {

			case LEFT:
				if (selectedObject != null && selectedObject.inside(v))
					moveSelectedObject = true;

				return true;
			case RIGHT:
				if (tileScaleWidthMarker.getGlobalBounds().contains(
						new Vector2f(v.x, v.y)))
					tileScaleWidth = true;

				if (tileScaleHeightMarker.getGlobalBounds().contains(
						new Vector2f(v.x, v.y)))
					tileScaleHeight = true;

				lastMousePos = new Vector2f(getMouseX(), getMouseY());
			default:
				return false;
			}

		default:
			return false;
		}
	}

	private void deselectSprite() {
		selectedObject = null;

		if (currentLayer == LayerType.PHYSICS)
			createMouseCollisionObj();
		else
			createMouseSprite();
	}

	private final boolean processInputKeyboard() {
		if (Keyboard.isKeyPressed(Key.W))
			y -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.S))
			y += CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.A))
			x -= CAM_MOVE_SPEED;
		if (Keyboard.isKeyPressed(Key.D))
			x += CAM_MOVE_SPEED;

		if (Keyboard.isKeyPressed(Key.LEFT) && currentTileWidth > 0)
			currentTileWidth--;
		if (Keyboard.isKeyPressed(Key.RIGHT))
			currentTileWidth++;
		if (Keyboard.isKeyPressed(Key.UP))
			currentTileHeight++;
		if (Keyboard.isKeyPressed(Key.DOWN) && currentTileHeight > 0)
			currentTileHeight--;

		return true;
	}

	private final boolean processInputKey(KeyEvent event) {
		switch (event.key) {
		case F5:
			new JsonGameMapSerializer().serialize(map);
			break;

		case F9:
			map = new JsonGameMapSerializer().deserialize(map.getMapId(), true,
					true);
			break;

		case TAB:
			map.switchWorlds();
			break;

		case DELETE:
			if (selectedObject != null) {
				map.remove(currentLayer, selectedObject);
				deselectSprite();
			}
			break;

		case PAGEUP:
		case ADD:
			if (event.shift)
				currentTileRotation += 11.25f;
			else if (event.control)
				currentTileZoom *= 2;
			else
				zoom *= 2;
			break;

		case PAGEDOWN:
		case SUBTRACT:
			if (event.shift)
				currentTileRotation -= 11.25f;
			else if (event.control)
				currentTileZoom /= 2;
			else
				zoom /= 2;
			break;

		case NUM1:
			if (event.control)
				map.flipShowLayer(LayerType.BACKGROUND_2);
			else {
				currentLayer = LayerType.BACKGROUND_2;
				deselectSprite();
			}
			break;
		case NUM2:
			if (event.control)
				map.flipShowLayer(LayerType.BACKGROUND_1);
			else {
				currentLayer = LayerType.BACKGROUND_1;
				deselectSprite();
			}
			break;
		case NUM3:
			if (event.control)
				map.flipShowLayer(LayerType.BACKGROUND_0);
			else {
				currentLayer = LayerType.BACKGROUND_0;
				deselectSprite();
			}
			break;

		case NUM4:
			if (event.control)
				map.flipShowLayer(LayerType.FOREGROUND_0);
			else {
				currentLayer = LayerType.FOREGROUND_0;
				deselectSprite();
			}
			break;
		case NUM5:
			if (event.control)
				map.flipShowLayer(LayerType.FOREGROUND_1);
			else {
				currentLayer = LayerType.FOREGROUND_1;
				deselectSprite();
			}
			break;

		case P:
			if (event.control)
				map.flipShowLayer(LayerType.PHYSICS);
			else {
				currentLayer = LayerType.PHYSICS;
				deselectSprite();
			}
			break;

		case O:
			if (event.control)
				map.flipShowLayer(LayerType.OBJECTS);
			else {
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
