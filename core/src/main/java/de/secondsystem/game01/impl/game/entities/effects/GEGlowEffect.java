package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.Glow;
import de.secondsystem.game01.impl.graphic.SpriteWrappper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.IWorldDrawable;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.util.SerializationUtil;

public class GEGlowEffect implements IGameEntityEffect {

	private final Glow glow;
	
	public GEGlowEffect(IGameMap map, IWorldDrawable representation, Color color, Color outerColor, float size, float waveSize, float sizeDecayPerSec) {
		this.glow = new Glow((SpriteWrappper) representation, color, outerColor, size, waveSize, sizeDecayPerSec);
	}

	@Override
	public void onDestroy(IGameMap map) {
	}
	
	@Override
	public void draw(RenderTarget rt, WorldId worldId, Vector2f position, float rotation, int worldMask) {
		glow.draw(rt);
	}
	
	@Override
	public void update(long frameTimeMs) {
		glow.update(frameTimeMs);
	}

	@Override
	public Attributes serialize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean inside(Vector2f point) {
		return false;// pe.inside(point);
	}

}

class GlowFactory implements IGameEntityEffectFactory {

	@Override
	public IGameEntityEffect create(IGameMap map, Attributes attributes, int worldMask, Vector2f position, float rotation, IWorldDrawable representation) {
		return new GEGlowEffect(map, representation,
				SerializationUtil.decodeColor(attributes.getString("color")),
				SerializationUtil.decodeColor(attributes.getString("outerColor")),
				attributes.getFloat("size"),
				attributes.getFloat("waveSize"),
				attributes.getFloat("sizeDecayPerSec", 0)
		);
	}
	
}