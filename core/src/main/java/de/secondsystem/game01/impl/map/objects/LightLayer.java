package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;

public final class LightLayer extends SimpleLayer {

	private final LightMap lightMap;
	
	public LightLayer(LayerType type, LightMap lightMap) {
		super(type);
		this.lightMap = lightMap;
	}

	@Override
	public void draw(RenderTarget rt) {
		if( lightMap!=null ) {
			lightMap.setView(rt.getView());

			lightMap.clear(0.8f);
			
			for( ILayerObject lo : objects ) {
				if( lo instanceof LightLayerObject )
					((LightLayerObject) lo).drawLight(lightMap);
			}

			lightMap.draw(rt);
		}
	}

}
