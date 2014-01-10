package de.secondsystem.game01.impl.map;

import de.secondsystem.game01.impl.GameContext;

public interface IGameMapSerializer {

	void serialize( GameMap map );
	GameMap deserialize( GameContext ctx, String mapId, boolean playable, boolean editable );
	
}
