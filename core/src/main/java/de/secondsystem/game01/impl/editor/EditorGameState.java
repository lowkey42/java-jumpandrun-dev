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
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.GraphicLayer.GraphicLayerType;

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
	private GraphicLayerType currentLayer = GraphicLayerType.FOREGROUND_0;
	private Sprite selectedSprite;
	private RectangleShape selectedSpriteMarker;
	
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
		
		selectedSpriteMarker = new RectangleShape(new Vector2f(1,1));
		selectedSpriteMarker.setOutlineColor(Color.BLUE);
		selectedSpriteMarker.setOutlineThickness(4);
		selectedSpriteMarker.setFillColor(Color.TRANSPARENT);
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
		
		
		if( selectedSprite!=null ) {
			selectedSprite.setOrigin(mouseTile.getTexture().getSize().x/2, mouseTile.getTexture().getSize().y/2);
			
			if( Mouse.isButtonPressed(Button.LEFT) ) {
				selectedSprite.setPosition(rt.mapPixelToCoords(new Vector2i(Mouse.getPosition().x, Mouse.getPosition().y)));
			}
			
			selectedSprite.setRotation(currentTileRotation);
			selectedSprite.setScale(currentTileZoom, currentTileZoom);
			
			Vector2i texBounds = selectedSprite.getTexture().getSize();
			selectedSpriteMarker.setSize( new Vector2f(texBounds.x, texBounds.y) );
			selectedSpriteMarker.setScale(selectedSprite.getScale());
			selectedSpriteMarker.setOrigin(selectedSprite.getOrigin());
			selectedSpriteMarker.setRotation(selectedSprite.getRotation());
			selectedSpriteMarker.setPosition(selectedSprite.getPosition());
			rt.draw(selectedSpriteMarker);
			
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
						if( selectedSprite==null ) {
							Sprite sprite = new Sprite();
							sprite.setTexture(tileset.tiles.get(currentTile));
							sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
							sprite.setPosition(mouseTile.getPosition());
							sprite.setRotation(currentTileRotation);
							sprite.setScale(currentTileZoom, currentTileZoom);
							
							map.addNode(currentLayer, sprite);
						}
						return true;
					
					case RIGHT:
						View view = new View(
								Vector2f.mul(
										new Vector2f(x+ctx.window.getView().getSize().x/2, y+ctx.window.getView().getSize().y/2),
										currentLayer.parallax), 
								Vector2f.div(ctx.window.getView().getSize(), zoom) );
						
						selectedSprite = map.findNode(currentLayer, ctx.window.mapPixelToCoords(new Vector2i(Mouse.getPosition().x, Mouse.getPosition().y), view));
						
						if( selectedSprite!=null ) {
							currentTileRotation = selectedSprite.getRotation();
							currentTileZoom = selectedSprite.getScale().x;
							
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
		selectedSprite = null;
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
				if( selectedSprite!=null ) {
					map.remove(currentLayer, selectedSprite);
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
					map.flipShowLayer(GraphicLayerType.BACKGROUND_2);
				else {
					currentLayer = GraphicLayerType.BACKGROUND_2;
					deselectSprite();
				}
				break;
			case NUM2:
				if( event.control )
					map.flipShowLayer(GraphicLayerType.BACKGROUND_1);
				else {
					currentLayer = GraphicLayerType.BACKGROUND_1;
					deselectSprite();
				}
				break;
			case NUM3:
				if( event.control )
					map.flipShowLayer(GraphicLayerType.BACKGROUND_0);
				else {
					currentLayer = GraphicLayerType.BACKGROUND_0;
					deselectSprite();
				}
				break;
			case NUM4:
				if( event.control )
					map.flipShowLayer(GraphicLayerType.FOREGROUND_0);
				else {
					currentLayer = GraphicLayerType.FOREGROUND_0;
					deselectSprite();
				}
				break;
			case NUM5:
				if( event.control )
					map.flipShowLayer(GraphicLayerType.FOREGROUND_1);
				else {
					currentLayer = GraphicLayerType.FOREGROUND_1;
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
		
		for( int i=0; i<s.length; ++i )
			if( currentLayer.layerIndex==i )
				str.append("=").append(i+1).append("= ");
			else
				str.append(i+1).append(" ");
		
		return str.toString();
	}
	
}
