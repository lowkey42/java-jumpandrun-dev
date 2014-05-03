package de.secondsystem.game01.impl.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.curser.CurserManager;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.gui.Edit;
import de.secondsystem.game01.impl.gui.Edit.IOnTextEnteredListener;
import de.secondsystem.game01.impl.gui.GUIGameState;
import de.secondsystem.game01.impl.gui.Label;
import de.secondsystem.game01.impl.gui.LayoutElementContainer;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.LayoutDirection;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.util.SerializationUtil;

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
public final class EditorGameState extends GUIGameState implements IMapProvider, IOnTextEnteredListener {

	private static final float CAM_MOVE_SPEED = 5.f;
	private GameContext ctx;

	private final GameState playGameState;
	private String mapToLoad=null;
	private GameMap map;
	
	private final CurserManager curser = new CurserManager(this);
	private ObjectPanel objectPanel;
	private LayerPanel layerPanel;
	private Edit commandPanel;
	private Label statusLabel;
	
	private float zoom = 1.f;
	private float cameraX = 0.f;
	private float cameraY = 0.f;
	
	private float timeFactor = 0;
	
	
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
		this.ctx = ctx;
		
		super.onStart(ctx);
	}
	
	@Override
	public IGameMap getMap() {
		if(map==null && mapToLoad!=null) {
			IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
			map = mapSerializer.deserialize(ctx, mapToLoad, null, false, true);
			mapToLoad = null;
		}
		
		return map;
	}

	@Override
	protected void onStop(GameContext ctx) {
	}
	
	@Override
	protected Vector2f getPosition() {
		return new Vector2f(0.f, 0.f);
	}
	
	@Override
	protected Layout getLayout() {
		return new Layout(LayoutDirection.HORIZONTAL, 0);
	}
	
	@Override
	protected void initGui(GameContext ctx, LayoutElementContainer c) {
		layerPanel = c.updateOffset(new LayerPanel(c.getXOffset(), c.getYOffset(), c.getWidth()-ObjectPanel.WIDTH, c, this));
		layerPanel.addListener(curser);
		objectPanel = c.updateOffset(new ObjectPanel(c.getXOffset(), c.getYOffset(), c.getHeight(), c, curser));
		curser.setToBrush();
		objectPanel.setActiveWorld(map.getActiveWorldId());
		
		commandPanel = c.createInputField(0, ctx.getViewHeight()-40, ctx.getViewWidth()-objectPanel.getWidth()-400, ":");
		commandPanel.setFillColor(new Color(0, 0, 0, 50));
		commandPanel.setEnteredListener(this);
		
		statusLabel = c.createPanel(ctx.getViewWidth()-objectPanel.getWidth()-400, ctx.getViewHeight()-40, 400, commandPanel.getHeight())
				.createLabel("STATUS...ASDASD");
	}
	
	private final boolean isCurserInGuiArea(GameContext ctx) {
		final Vector2f mousePos = ctx.getMousePosition();
		
		return layerPanel.inside(mousePos) || objectPanel.inside(mousePos) || commandPanel.inside(mousePos);
	}

	private final Vector2f getWorldMousePosition() {
		return ctx.window.mapPixelToCoords(Mouse.getPosition(ctx.window), getTransformedView());
	}
	
	private View getTransformedView() {
		Vector2f ws = ctx.window.getView().getSize();
		View view = new View(Vector2f.mul(new Vector2f(cameraX + ws.x / 2, cameraY + ws.y
				/ 2), layerPanel.getLayer().parallax), Vector2f.div(ws, zoom));
		return view;
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		statusLabel.setText("Pos: "+((int)getWorldMousePosition().x)+"/"+((int)getWorldMousePosition().y)+"  |   Time:"+timeFactor+
				"  |   Entity: "+(curser.getCurrentBrushIndex()+1)+"/"+(curser.getBrushCount()+1));
		
		getMap().update((long) (frameTime*timeFactor));	///< update everything, but freeze/slow down time 
		drawMap(ctx.window);
		
		if( !isCurserInGuiArea(ctx) ) {
			processInputKeyboard();
			if( curser.get()!=null )
				curser.get().onMouseMoved(getWorldMousePosition());
		}
		
		super.onFrame(ctx, frameTime);
	}

	private final void drawMap(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.setView(new View(new Vector2f(cameraX + cView.getSize().x / 2, cameraY + cView.getSize().y / 2), Vector2f.div(cView.getSize(), zoom)));
		getMap().draw(rt);

		rt.setView(new View(Vector2f.mul(rt.getView().getCenter(), layerPanel.getLayer().parallax), rt.getView().getSize()));
		curser.draw(rt);
		rt.setView(cView);
	}
	
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		if( isCurserInGuiArea(ctx) ) 
			super.processEvent(ctx, event);
		
		else if (event.type == Event.Type.KEY_RELEASED
				&& event.asKeyEvent().key == Key.F12) {
			curser.setToNull();
			setNextState(playGameState);

		} else if (event.type == Event.Type.KEY_RELEASED
				&& event.asKeyEvent().key == Key.ESCAPE) {
			setNextState(new MainMenuState(this));
			
		} else 
			processInput(ctx, event);
	}

	private final boolean processInput(GameContext ctx, Event event) {
		switch (event.type) {
			case KEY_PRESSED:
				return processInputKey(ctx, event.asKeyEvent());
				
			case MOUSE_WHEEL_MOVED:
				if (event.asMouseWheelEvent().delta == 0)
					return true;
	
				if (Keyboard.isKeyPressed(Key.LSHIFT))
					curser.get().zoom(event.asMouseWheelEvent().delta);
					
				else
					curser.scrollBrushes(event.asMouseWheelEvent().delta > 0);
	
				return true;
	
			case MOUSE_BUTTON_RELEASED:
				if( event.asMouseButtonEvent().button==Button.LEFT && curser.get().isDragged() ) {
					curser.get().onDragFinished(getWorldMousePosition());
					return true;
				
				} else if( event.asMouseButtonEvent().button==Button.RIGHT ) {
					curser.setSelectionFromCurser(getWorldMousePosition(), layerPanel.getLayer());
					return true;
				}
				
				return false;
	
			case MOUSE_BUTTON_PRESSED:
				if( event.asMouseButtonEvent().button==Button.LEFT ) {
					curser.get().onDragged(getWorldMousePosition());
				
					return true;
				}
				
				return false;
	
			default:
				return false;
		}
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
			curser.get().resize(-1, 0);
		
		if (Keyboard.isKeyPressed(Key.RIGHT))
			curser.get().resize(1, 0);
		
		if (Keyboard.isKeyPressed(Key.UP))
			curser.get().resize(0, 1);
		if (Keyboard.isKeyPressed(Key.DOWN))
			curser.get().resize(0, -1);

		return true;
	}
	
	
	private final boolean processInputKey(GameContext ctx, KeyEvent event) {
		switch (event.key) {
			case F5: // save
				curser.setToNull();
				new JsonGameMapSerializer().serialize(map);
				curser.setToBrush();
				break;
	
			case F9: // load
				map = new JsonGameMapSerializer().deserialize(ctx, map.getMapId(), null, false, 
						true);
				curser.setToBrush();
				objectPanel.setActiveWorld(map.getActiveWorldId());
				break;
	
			case TAB: // switch world
				curser.setToNull();
				map.switchWorlds();
				curser.setToBrush();
				objectPanel.setActiveWorld(map.getActiveWorldId());
				break;
	
			case DELETE: // delete selected object
				curser.deleteSelected();
				break;
	
			case PAGEUP:
			case ADD: 
				if (event.shift) // rotate object
					curser.get().rotate(11.25f);
				else if (event.control) // scale up selected object
					curser.get().zoom(2.f);
				else
					zoom *= 2;
				break;
	
			case PAGEDOWN:
			case SUBTRACT:
				if (event.shift) // rotate object
					curser.get().rotate(-11.25f);
				else if (event.control) // scale down selected object
					curser.get().zoom(1/2.f);
				else
					zoom /= 2;
				break;
	
			default:
				return layerPanel.handleKeyCommands(event);
		}
		
		return true;
		
	}

	
	private static final Pattern COMMAND_ARG_PATTERN = Pattern.compile("\"([^\"]+)\"|([^ ]+)");
	private static final Pattern COMMAND_PATTERN = Pattern.compile("(?::)?([a-zA-Z]+)((?: +[^ ]+|\"[^\"]+\")*)");
	
	@Override
	public String onTextEntered(String text) {
		if(text.startsWith(":"))
			text = text.substring(1);
		
		text = text.trim();
		
		final Matcher cm = COMMAND_PATTERN.matcher(text);
		if( !cm.matches() )
			return "> syntax error";
		
		final String command = cm.group(1);
		
		final Matcher am = COMMAND_ARG_PATTERN.matcher(cm.group(2));
		final List<String> args = new ArrayList<>();
		while( am.find() ) {
			if( am.group(1)!=null )
				args.add(am.group(1).trim());
			else if( am.group(2)!=null )
				args.add(am.group(2).trim());
		}
		
		try {
			switch(command) {
				case "quit":
				case "q":
					curser.setToNull();
					setNextState(playGameState!=null ? playGameState : new MainMenuState());
					break;
					
				case "save":
				case "w":
					curser.setToNull();
					new JsonGameMapSerializer().serialize(map);
					curser.setToBrush();
					break;
					
				case "new":
					IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
					GameMap nmap = new GameMap(ctx, args.get(0), new Tileset(args.get(1)), null);
					mapSerializer.serialize(nmap);
					
				case "l":
				case "load":
					map = new JsonGameMapSerializer().deserialize(ctx, args.isEmpty() ? map.getMapId() : args.get(0), null, false, true);
					curser.setToBrush();
					break;
					
				case "play":
					curser.setToNull();
					setNextState(new MainGameState(map.getMapId(), ctx, this));
					curser.setToBrush();
					break;
					
				case "set":
					switch(args.get(0)) {
						case "light":
							map.setAmbientLight(SerializationUtil.decodeColor(args.get(1)));
							break;
							
						case "bg":
						case "background":
							map.setBackgroundColor(SerializationUtil.decodeColor(args.get(1)));
							break;
							
						case "time":
						case "tf":
							timeFactor = Float.valueOf(args.get(1));
							break;
					}
					break;
					
				case "fl":
				case "fiatLux":
					map.toogleLightmap();
					break;
					
				case "goto":
					try {
						cameraX = Float.valueOf(args.get(0));
						cameraY = Float.valueOf(args.get(1));
						
					} catch(NumberFormatException e) {
						return "> syntax error: NaN";
					}
					break;
					
				case "echo":
					System.out.println(":echo > "+args);
					break;
			}
		} catch( Exception e ) {
			System.err.println("cmd-error: "+e.getMessage());
		}
		
		return ":";
	}

}
