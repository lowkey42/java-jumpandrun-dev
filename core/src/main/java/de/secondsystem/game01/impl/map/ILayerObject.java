package de.secondsystem.game01.impl.map;

import java.util.Map;

import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;

public interface ILayerObject extends IDrawable, IInsideCheck, IMoveable, IDimensioned {
			
	void setDimensions(float width, float height);
	
	LayerObjectType typeUuid();
	Map<String, Object> getAttributes();
}