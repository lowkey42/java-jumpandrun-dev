package de.secondsystem.game01.impl.map;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.controller.ControllerManager;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.EventManager;
import de.secondsystem.game01.impl.game.entities.events.impl.SequenceManager;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.impl.timer.TimerManager;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameMap extends IDrawable, IUpdateable {

	public static enum WorldId {
		MAIN(1), OTHER(2);
		
		public final int id;
		final int arrayIndex;
		private WorldId(int id) {
			this.id = id;
			this.arrayIndex = id-1;
		}
		
		public static WorldId byId(int id) {
			for( WorldId w : values() )
				if( w.id==id )
					return w;
			
			return null;
		}
	}
	
	
	String getMapId();

	void setMapId(String mapId);

	void setTileset(Tileset tileset);

	Tileset getTileset();
	
	IGameEntityManager getEntityManager();
	
	TimerManager getTimerManager();
	
	void switchWorlds();
	
	void setActiveWorldId(WorldId worldId);

	WorldId getActiveWorldId();

	void addNode(WorldId worldId, LayerType layer, ILayerObject sprite);

	void addNode(LayerType layer, ILayerObject sprite);
	
	ILayerObject findNode(LayerType layer, Vector2f point);

	void remove(LayerType layer, ILayerObject s);

	boolean flipShowLayer(LayerType layer);

	boolean[] getShownLayer();

	IPhysicsWorld getPhysicalWorld();

	boolean isEditable();
	
	void registerWorldSwitchListener( IWorldSwitchListener listener );
	void deregisterWorldSwitchListener( IWorldSwitchListener listener );
	
	ScriptEnvironment getScriptEnv();
	
	public interface IWorldSwitchListener {
		void onWorldSwitch( WorldId newWorldId );
	}
	
	SequenceManager getSequenceManager();
	
	ControllerManager getControllerManager();
	
	EventManager getEventManager();

}
