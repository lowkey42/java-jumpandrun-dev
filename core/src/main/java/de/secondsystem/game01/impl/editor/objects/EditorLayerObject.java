package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.CollisionObject.CollisionType;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;
import de.secondsystem.game01.util.Tools;

public class EditorLayerObject extends AbstractEditorLayerObject {
	protected EditorMarker marker;
	protected EditorMarker [] scaleMarkers  = new EditorMarker[4];
	protected Vector2f mappedMousePos;
	protected Vector2f lastMappedMousePos = new Vector2f(0.f, 0.f);
	protected float lastWidth, lastHeight;
	protected Vector2f lastPos = new Vector2f(0.f, 0.f);
	protected int scalingX = 0;
	protected int scalingY = 0;	
	protected boolean scaling = false;
	protected IGameMap map;
	
	private Tileset tileset;
	private int currentTile = 0;
	private boolean isPhysicsObject;
	
	protected boolean mouseState;
	
	protected class Vector3 {	
		public float x;
		public float y;
		public float z;
		
		public Vector3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public EditorLayerObject() {
		
	}
	
	public EditorLayerObject(Color outlineColor, float outlineThickness, Color fillColor, IGameMap map) {
		mouseState = false;
		this.map = map;	
		marker = new EditorMarker(outlineColor, outlineThickness, fillColor);
		
		for(int i=0; i<4; i++)
			scaleMarkers[i] = new EditorMarker(Color.TRANSPARENT, 0.0f, new Color(255, 100, 100, 150));		
	}
	
	public EditorLayerObject(GameMap map, Tileset tileset, boolean isPhysicsObject) {
		mouseState = true;
		this.tileset = tileset;
		this.isPhysicsObject = isPhysicsObject;
		
		create(map);
	}
	
	@Override
	public void setLayerObject(ILayerObject layerObject) {
		this.layerObject = layerObject;
		
		if( layerObject != null ) {
			rotation = layerObject.getRotation();
			zoom = 1.0f;
			height = layerObject.getHeight();
			width = layerObject.getWidth();
			setPosition(layerObject.getPosition());
			lastPos = pos;
			lastWidth = width;
			lastHeight = height;
		}
	}
	
	@Override
	public ILayerObject getLayerObject() {
		return layerObject;
	}
	
	@Override
	public void removeFromMap(GameMap map, LayerType currentLayer) {
		map.remove(currentLayer, layerObject);
		layerObject = null;
	}
	
	@Override
	public void draw(RenderTarget rt) {	
		super.draw(rt);
		
		if( !mouseState ) {
			marker.draw(rt);
			
			if( mappedMousePos != null )
				for(int i=0; i<4; i++) {
					if( scaleMarkers[i].isMouseOver(mappedMousePos) )
						scaleMarkers[i].draw(rt);
			}
		}
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		
		if( mouseState ) {
			setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));					
		}
		else {		
			mappedMousePos = rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY));
			
			if (movedObj) 
				setPosition(mappedMousePos);
			
			marker.update(this);	
			
			EditorMarker em = scaleMarkers[2];
			em.setRelativePos(width - em.getWidth(), height - em.getHeight());
			em = scaleMarkers[3];
			em.setRelativePos(width - em.getWidth(), height - em.getHeight());
			
			for(int i=0; i<4; i++) {
				scaleMarkers[i].update(this);
				scaleMarkers[i].setSize( new Vector2f(i%2 == 1 ? scaleMarkers[i].getWidth() : 8.f, i%2 == 0 ? scaleMarkers[i].getHeight() : 8.f));
			}
			
			mouseScaling();
		}
	}
	
	@Override
	public void resetScalingDirection() {
		scalingX = 0;
		scalingY = 0;
	}
	
	@Override
	public void checkScaleMarkers(Vector2f p) {
		float r = Tools.clampedRotation(scaleMarkers[0].getShape().getRotation());
		if( r >= 90 && r <= 270 ) {
			scalingX = scaleMarkers[0].isInside(p) ? 1 : scaleMarkers[2].isInside(p) ? -1 : 0;		
			scalingY = scaleMarkers[1].isInside(p) ? 1 : scaleMarkers[3].isInside(p) ? -1 : 0;
		}
		else
		{
			scalingX = scaleMarkers[0].isInside(p) ? -1 : scaleMarkers[2].isInside(p) ? 1 : 0;		
			scalingY = scaleMarkers[1].isInside(p) ? -1 : scaleMarkers[3].isInside(p) ? 1 : 0;
		}
	}
	
	@Override
	public void setScaling(boolean scaling) {
		this.scaling = scaling;
	}
	
	@Override
	public boolean isScaling() {
		return scaling;
	}
	
	@Override
	public void setLastMappedMousePos(Vector2f pos) {
		lastMappedMousePos = pos;
	}
	
	protected Vector3 mouseScaling(EditorMarker marker, int vertex1, int vertex2, float scalingDir, Vector2f lastPos, float lastSize, boolean xDir) {
		Vector2f dir = new Vector2f(0.f, 0.f);

		dir = Vector2f.sub( Tools.distanceVector(marker.getShape(), vertex1, vertex2, mappedMousePos), 
					Tools.distanceVector(marker.getShape(), vertex1, vertex2, lastMappedMousePos) );
		
		float t = xDir  ? dir.x : dir.y;
		float d = Tools.vectorLength(dir);
		float size = lastSize + ( d * (scalingDir == -1 ? 1 : -1) ) * ( t < 0 ? -1 : 1 );
		Vector2f v = Vector2f.sub( lastPos, Vector2f.div(dir, 2.f) );
		
		return new Vector3(v.x, v.y, size);
	}
	
	protected void mouseScaling() {
		// TODO: fix bug: simultaneous scaling of width and height
		
		if( scalingX != 0 ) {
			Vector3 v;
			if( scalingX == -1 )
				v = mouseScaling(scaleMarkers[0], 3, 0, scalingX, lastPos, lastWidth, true); 
			else
				v = mouseScaling(scaleMarkers[2], 1, 2, scalingX, lastPos, lastWidth, true); 
			
			setWidth( v.z );
			setPosition( new Vector2f(v.x, v.y) );
		}
		else 
			lastWidth  = width;
		
		if( scalingY != 0 ) {	
			Vector3 v;
			if( scalingY == -1 ) 
				v = mouseScaling(scaleMarkers[1], 0, 1, scalingY, lastPos, lastHeight, false); 
			else 
				v = mouseScaling(scaleMarkers[3], 2, 3, scalingY, lastPos, lastHeight, false); 
			
			setHeight( v.z );
			setPosition( new Vector2f(v.x, v.y) );
		}
		else 
			lastHeight = height;
		
		if( scalingX == 0 && scalingY == 0 )
			lastPos = pos;
		else
			scaling = true;
		
		if( width < 10.f )
			setWidth( 10.f );
		
		if( height < 10.f )
			setHeight( 10.f );
	}

	@Override
	public void create(IGameMap map) {
		this.map = map;
		
		if( isPhysicsObject )
			layerObject = new CollisionObject(map, map.getActiveWorldId().id, CollisionType.NORMAL, 0, 0, 50, 50, 0);		
		else
			layerObject = new SpriteLayerObject(tileset, map.getActiveWorldId().id, currentTile, 0, 0, 0);

		rotation = 0.f;
		zoom = 1.f;
		height = layerObject.getHeight();
		width = layerObject.getWidth();
	}

	@Override
	public void changeSelection(int offset) {
		if ( layerObject instanceof SpriteLayerObject ) {
			int tileSize = tileset.size();
			currentTile += offset;
			currentTile = currentTile < 0 ? tileSize - 1 : currentTile % tileSize;

			((SpriteLayerObject) layerObject).setTile(tileset, currentTile);
		} 
		else 
			if ( layerObject instanceof CollisionObject ) {
				CollisionObject co = (CollisionObject) layerObject;
				CollisionType type = offset > 0 ? co.getType().next() : co.getType().prev();
				co.setType(type);
			}
	}

	@Override
	public void addToMap(LayerType currentLayer) {
		map.addNode(currentLayer, layerObject.typeUuid().create(map, layerObject.serialize()));
	}

	@Override
	public void setMouseState(boolean mouseState) {
		this.mouseState = mouseState;
	}

	@Override
	public boolean inMouseState() {
		return mouseState;
	} 
}
