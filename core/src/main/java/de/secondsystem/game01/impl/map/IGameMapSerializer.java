package de.secondsystem.game01.impl.map;

import java.nio.file.Path;

public interface IGameMapSerializer {

	void serialize( Path out, GameMap map );
	GameMap deserialize( Path in );
	
}
