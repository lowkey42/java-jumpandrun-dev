package de.secondsystem.game01.impl.map;

import java.io.IOException;

import javax.script.ScriptException;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameMap extends IDrawable, IUpdateable {

	String getMapId();

	void setMapId(String mapId);

	void setTileset(Tileset tileset);

	Tileset getTileset();
	
	IGameEntityManager getEntityManager();

	void switchWorlds();

	int getActiveGameWorldId();

	void addNode(int worldId, LayerType layer, LayerObject sprite);

	void addNode(LayerType layer, LayerObject sprite);
	
	void loadScript( String name ) throws IOException, ScriptException;

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