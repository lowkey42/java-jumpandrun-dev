package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.physics.IPhysicalWorld;

public interface IGameMap {

	String getMapId();

	void setMapId(String mapId);

	void setTileset(Tileset tileset);

	Tileset getTileset();

	void switchWorlds();

	void draw(RenderTarget rt);

	void update(long frameTimeMs);

	int getActiveGameWorldId();

	void addNode(int worldId, LayerType layer, LayerObject sprite);

	void addNode(LayerType layer, LayerObject sprite);

	LayerObject findNode(LayerType layer, Vector2f point);

	void remove(LayerType layer, LayerObject s);

	boolean flipShowLayer(LayerType layer);

	boolean[] getShownLayer();

	IPhysicalWorld getPhysicalWorld();

	boolean isEditable();
	
	void registerWorldSwitchListener( IWorldSwitchListener listener );
	void deregisterWorldSwitchListener( IWorldSwitchListener listener );
	
	public interface IWorldSwitchListener {
		void onWorldSwitch( int newWorldId );
	}

}