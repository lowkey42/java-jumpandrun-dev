package de.secondsystem.game01.impl.editor;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.CollisionObject.CollisionType;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

public class MouseEditorLayerObject extends EditorLayerObject {
	private final Tileset tileset;
	private int currentTile = 0;
	
	public MouseEditorLayerObject(Tileset tileset) {
		this.tileset = tileset;
		
		createSpriteObject();
	}
	
	public void createSpriteObject() {
		if (!(layerObject instanceof SpriteLayerObject))
			layerObject = new SpriteLayerObject(tileset, currentTile, 0, 0, 0);
		
		onTileChanged();
	}

	public void createCollisionObject(GameMap map) {
		if (!(layerObject instanceof CollisionObject))
			layerObject = new CollisionObject(map, map.getActiveWorldId(), CollisionType.NORMAL, 0, 0, 50, 50, 0);
		
		onTileChanged();
	}
	
	protected void onTileChanged() {
		rotation = 0.f;
		zoom = 1.f;
		height = layerObject.getHeight();
		width = layerObject.getWidth();
	}
	
	public void changeTile(int offset) {		
		if ( layerObject instanceof SpriteLayerObject ) {
			int tileSize = tileset.tiles.size();
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
	
	public void addToMap(GameMap map, LayerType currentLayer) {
		map.addNode(currentLayer, layerObject.typeUuid().create(map, map.getActiveWorldId(), layerObject.getAttributes()));
	}	

}
