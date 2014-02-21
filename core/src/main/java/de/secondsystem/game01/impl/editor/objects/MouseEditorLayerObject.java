package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.CollisionObject.CollisionType;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

public class MouseEditorLayerObject extends EditorLayerObject implements IMouseEditorObject {
	private final Tileset tileset;
	private int currentTile = 0;
	private IGameMap map;
	private final boolean isPhysicsObject;
	
	public MouseEditorLayerObject(GameMap map, Tileset tileset, boolean isPhysicsObject) {
		this.tileset = tileset;
		this.isPhysicsObject = isPhysicsObject;
		
		create(map);
	}

	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
		
		super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
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
}
