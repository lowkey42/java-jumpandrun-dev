package de.secondsystem.game01.impl.map;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.scripting.IScriptApi;

public interface IGameMapSerializer {

	void serialize( GameMap map );
	GameMap deserialize( GameContext ctx, String mapId, IScriptApi scriptApi, boolean playable, boolean editable );
	
}
