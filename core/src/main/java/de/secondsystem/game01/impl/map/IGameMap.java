package de.secondsystem.game01.impl.map;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameMap extends IDrawable, IUpdateable {

	String getMapId();

	void setMapId(String mapId);

	void setTileset(Tileset tileset);

	Tileset getTileset();
	
	IGameEntityManager getEntityManager();

	void switchWorlds();
	
	void setActiveWorldId(int worldId);

	int getActiveWorldId();

	void addNode(int worldId, LayerType layer, LayerObject sprite);

	void addNode(LayerType layer, LayerObject sprite);
	
	LayerObject findNode(LayerType layer, Vector2f point);

	void remove(LayerType layer, LayerObject s);

	boolean flipShowLayer(LayerType layer);

	boolean[] getShownLayer();

	IPhysicsWorld getPhysicalWorld();

	boolean isEditable();
	
	void registerWorldSwitchListener( IWorldSwitchListener listener );
	void deregisterWorldSwitchListener( IWorldSwitchListener listener );
	
	ScriptEnvironment getScriptEnv();
	
	public interface IWorldSwitchListener {
		void onWorldSwitch( int newWorldId );
	}

}