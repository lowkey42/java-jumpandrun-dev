package de.secondsystem.game01.impl.map;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.ISerializable;

public interface ILayerObject extends IDrawable, IInsideCheck, IMoveable, IDimensioned, ISerializable, IScalable {

	boolean isInWorld(WorldId worldId);
	void setWorld(WorldId worldId, boolean exists);
	
	LayerObjectType typeUuid();
}