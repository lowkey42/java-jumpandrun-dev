package de.secondsystem.game01.impl.editor;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

/**
 * 
 * 
 * Controlls:
 * wasd:		move
 * L-Click:		place object
 * Wheel:		change object-type
 * Shift +/-:	rotate
 * 1-9:			change layer
 * +/-:			zoom
 * 
 * @author lowkey
 *
 */
public final class EditorGameState extends GameState {

	private static final float CAM_MOVE_SPEED = 5.f;
	
	private final GameState playGameState;
	private final GameMap map;
	private final Tileset tileset;
	
	private final Text editorHint;
	private final Text layerHint;
	private final Sprite mouseTile;
	
	private float x,y, zoom=1.f;
	private int currentTile = 0;
	private float currentTileRotation=0;
	private float currentTileZoom=1.f;
	private LayerType currentLayer = LayerType.FOREGROUND_0;
	private LayerObject selectedObject;
	private RectangleShape selectedObjectMarker;
	
	public EditorGameState(GameState playGameState, GameMap map) {
		this.playGameState = playGameState;
		this.map = map; // TODO: copy
		this.tileset = new Tileset("test01"); // TODO: get from map
		
		Font freeSans = new Font();
		try {
		    freeSans.loadFromFile(Paths.get("assets/FreeSans.otf"));
		    
		} catch(IOException ex) {
		    throw new Error(ex);
		}
		
		editorHint = new Text("EDITOR: ${mapName}", freeSans);
		editorHint.setPosition(0, 0);
		editorHint.setColor(Color.RED);
		
		layerHint = new Text(generateLayerHintStr(), freeSans);
		layerHint.setPosition(0, 25);
		layerHint.setColor(Color.WHITE);
		
		mouseTile = new Sprite();
		mouseTile.setColor(new Color(255, 255, 255, 128));
		mouseTile.setTexture(tileset.tiles.get(currentTile));
		mouseTile.setOrigin(mouseTile.getTexture().getSize().x/2, mouseTile.getTexture().getSize().y/2);
		
		selectedObjectMarker = new RectangleShape(new Vector2f(1,1));
		selectedObjectMarker.setOutlineColor(Color.BLUE);
		selectedObjectMarker.setOutlineThickness(4);
		selectedObjectMarker.setFillColor(Color.TRANSPARENT);
	}

	@Override
	protected void onStart(GameContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFrame(GameContext ctx) {
		drawMap(ctx.window);
		
		ctx.window.draw(editorHint);
		ctx.window.draw(layerHint);
		
		processInputKeyboard();
		
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED) {
	            //The user pressed the close button
	            ctx.window.close();
	            
	        } else if( event.type==Event.Type.KEY_RELEASED && event.asKeyEvent().key==Key.F12 ) {
	        	setNextState(playGameState);
	        	
	        } else
	        	processInput(ctx, event);
	    }
	}
	
	private final void drawMap( RenderTarget rt ) {
		final ConstView cView = rt.getView();
		
		rt.setView(new View(new Vector2f(x+cView.getSize().x/2, y+cView.getSize().y/2), Vector2f.div(cView.getSize(), zoom) ));
		map.draw(rt);

		rt.setView(new View(Vector2f.mul(rt.getView().getCenter(), currentLayer.parallax), rt.getView().getSize()));
		
		
		if( selectedObject!=null ) {
			if( Mouse.isButtonPressed(Button.LEFT) ) {
				selectedObject.setPosition(rt.mapPixelToCoords(new Vector2i(Mouse.getPosition().x, Mouse.getPosition().y)));
			}
			
			selectedObject.setRotation(currentTileRotation);
			selectedObject.setScale(currentTileZoom);
			
			selectedObjectMarker.setSize( new Vector2f(selectedObject.getHeight(), selectedObject.getWidth()) );
			selectedObjectMarker.setScale(selectedObject.getScale(), selectedObject.getScale());
			selectedObjectMarker.setOrigin(selectedObject.getOrigin());
			selectedObjectMarker.setRotation(selectedObject.getRotation());
			selectedObjectMarker.setPosition(selectedObject.getPosition());
			rt.draw(selectedObjectMarker);
			
		} else {
			mouseTile.setOrigin(mouseTile.getTexture().getSize().x/2, mouseTile.getTexture().getSize().y/2);
			mouseTile.setPosition( rt.mapPixelToCoords(new Vector2i(Mouse.getPosition().x, Mouse.getPosition().y)) );
			mouseTile.setRotation(currentTileRotation);
			mouseTile.setScale(currentTileZoom, currentTileZoom);
			rt.draw(mouseTile);
		}

		rt.setView(cView);
	}
	
	private final boolean processInput(GameContext ctx, Event event) {
		switch( event.type ) {
			case KEY_PRESSED:
				return processInputKey(event.asKeyEvent());

			case MOUSE_WHEEL_MOVED:
				if( event.asMouseWheelEvent().delta==0 )
					return true;
				
				currentTile = Math.abs( (currentTile+ (event.asMouseWheelEvent().delta<0 ? -1 : 1)) % tileset.tiles.size() );
				mouseTile.setTexture(tileset.tiles.get(currentTile));
				mouseTile.setOrigin(mouseTile.getTexture().getSize().x/2, mouseTile.getTexture().getSize().y/2);
				return true;
				
			case MOUSE_BUTTON_RELEASED:
				switch( event.asMouseButtonEvent().button ){
					case LEFT:
						if( selectedObject==null ) {
							map.addNode(currentLayer, new SpriteLayerObject(tileset, currentTile, mouseTile.getPosition().x, mouseTile.getPosition().y, currentTileRotation, currentTileZoom));
						}
						return true;
					
					case RIGHT:
						View view = new View(
								Vector2f.mul(
										new Vector2f(x+ctx.window.getView().getSize().x/2, y+ctx.window.getView().getSize().y/2),
										currentLayer.parallax), 
								Vector2f.div(ctx.window.getView().getSize(), zoom) );
						
						selectedObject = map.findNode(currentLayer, ctx.window.mapPixelToCoords(new Vector2i(Mouse.getPosition().x, Mouse.getPosition().y), view));
						
						if( selectedObject!=null ) {
							currentTileRotation = selectedObject.getRotation();
							currentTileZoom = selectedObject.getScale();
							
						} else {
							deselectSprite();
						}
						
					default:
						return false;
				}
				
			default:
				return false;
		}
	}

	private void deselectSprite() {
		selectedObject = null;
		currentTileRotation = 0.f;
		currentTileZoom = 1.f;
	}
	
	private final boolean processInputKeyboard() {
		if( Keyboard.isKeyPressed(Key.W) )
			y-=CAM_MOVE_SPEED;
		if( Keyboard.isKeyPressed(Key.S) )
			y+=CAM_MOVE_SPEED;
		if( Keyboard.isKeyPressed(Key.A) )
			x-=CAM_MOVE_SPEED;
		if( Keyboard.isKeyPressed(Key.D) )
			x+=CAM_MOVE_SPEED;
		
		return true;
	}
	
	private final boolean processInputKey(KeyEvent event) {
		switch( event.key ) {
			case DELETE:
				if( selectedObject!=null ) {
					map.remove(currentLayer, selectedObject);
					deselectSprite();
				}
				break; 
		
			case ADD:
				if( event.shift )
					currentTileRotation+=11.25f;
				else if( event.control )
					currentTileZoom*=2;
				else 
					zoom*=2;
				break;
			case SUBTRACT:
				if( event.shift )
					currentTileRotation-=11.25f;
				else if( event.control )
					currentTileZoom/=2;
				else 
					zoom/=2;
				break;
				
			case NUM1:
				if( event.control )
					map.flipShowLayer(LayerType.BACKGROUND_2);
				else {
					currentLayer = LayerType.BACKGROUND_2;
					deselectSprite();
				}
				break;
			case NUM2:
				if( event.control )
					map.flipShowLayer(LayerType.BACKGROUND_1);
				else {
					currentLayer = LayerType.BACKGROUND_1;
					deselectSprite();
				}
				break;
			case NUM3:
				if( event.control )
					map.flipShowLayer(LayerType.BACKGROUND_0);
				else {
					currentLayer = LayerType.BACKGROUND_0;
					deselectSprite();
				}
				break;
				
			case NUM4:
				if( event.control )
					map.flipShowLayer(LayerType.PHYSICS);
				else {
					currentLayer = LayerType.PHYSICS;
					deselectSprite();
				}
				break;
				
			case NUM5:
				if( event.control )
					map.flipShowLayer(LayerType.FOREGROUND_0);
				else {
					currentLayer = LayerType.FOREGROUND_0;
					deselectSprite();
				}
				break;
			case NUM6:
				if( event.control )
					map.flipShowLayer(LayerType.FOREGROUND_1);
				else {
					currentLayer = LayerType.FOREGROUND_1;
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
		
		for( LayerType l : LayerType.values() ) {
			if( currentLayer==l )
				str.append("=").append(l.name).append("=   ");
			else
				str.append(l.name).append("   ");
		}
		
		return str.toString();
	}
	
}
