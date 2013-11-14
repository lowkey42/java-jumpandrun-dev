package de.secondsystem.game01.impl.map;

public interface IGameMapSerializer {

	void serialize( GameMap map );
	GameMap deserialize( String mapId, boolean playable, boolean editable );
	
}
