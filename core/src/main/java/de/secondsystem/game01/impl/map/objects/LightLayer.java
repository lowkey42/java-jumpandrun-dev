package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTarget;

import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.LayerType;

public final class LightLayer extends SimpleLayer {

	private final LightMap lightMap;
	
	private final WorldId worldId;
	
	public LightLayer(WorldId worldId, LayerType type, LightMap lightMap) {
		super(type, worldId);
		this.worldId = worldId;
		this.lightMap = lightMap;
	}

	@Override
	public void draw(RenderTarget rt) {
		if( lightMap!=null ) {
			lightMap.drawVisibleLights(worldId.id, new FloatRect(rt.getView().getCenter().x-rt.getView().getSize().x/2, rt.getView().getCenter().y-rt.getView().getSize().y/2, rt.getView().getSize().x, rt.getView().getSize().y));
		}
	}
	
}
