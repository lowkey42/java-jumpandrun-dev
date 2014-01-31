package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.RenderTarget;

import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.LayerType;

public final class LightLayer extends SimpleLayer {

	private final LightMap lightMap;
	
	private final WorldId worldId;
	
	public LightLayer(WorldId worldId, LayerType type, LightMap lightMap) {
		super(type);
		this.worldId = worldId;
		this.lightMap = lightMap;
	}

	@Override
	public void draw(RenderTarget rt) {
		if( lightMap!=null ) {
			lightMap.drawVisibleLights(worldId.id, rt.getView().getViewport());
		}
	}
	
}
