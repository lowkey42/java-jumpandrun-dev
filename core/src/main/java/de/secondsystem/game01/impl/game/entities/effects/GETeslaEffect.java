package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.LightningManager;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.collections.IndexedMoveable;

public class GETeslaEffect extends IndexedMoveable implements IGameEntityEffect, LightningManager.ILightningSource {
	
	private int worldMask;
	
	private final float maxDistance;
	
	private Vector2f position;
	
	public GETeslaEffect(IGameMap map, int worldMask, Vector2f position, float maxDistance) {
		this.position = position;
		this.worldMask = worldMask;
		this.maxDistance = maxDistance;
		
		map.getLightningManager().addSource(this, worldMask);
	}

	@Override
	public void onDestroy(IGameMap map) {
		map.getLightningManager().removeSource(this);
	}
	
	@Override
	public void draw(RenderTarget rt, Vector2f position, float rotation, int worldMask) {
		if( !position.equals(this.position) || this.worldMask!=worldMask ) {
			this.worldMask = worldMask;
			setPosition(position);
		}
	}
	
	@Override
	public void update(long frameTimeMs) {
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EffectUtils.FACTORY, EffectUtils.normalizeHandlerFactory(TeslaFactory.class.getName())),
				new Attribute("maxDistance", maxDistance)
		);
	}

	@Override
	public boolean inside(Vector2f point) {
		return false;
	}
	
	@Override
	public void setRotation(float degree) {
	}
	
	@Override
	public float getRotation() {
		return 0;
	}
	
	@Override
	public Vector2f getPosition() {
		return position;
	}
	
	@Override
	public float getMaxDist() {
		return maxDistance;
	}
	
	@Override
	public int getWorldMask() {
		return this.worldMask;
	}

	@Override
	protected void doSetPosition(Vector2f pos) {
		this.position = pos;
	}
}

class TeslaFactory implements IGameEntityEffectFactory {

	@Override
	public IGameEntityEffect create(IGameMap map, Attributes attributes, int worldMask, Vector2f position, float rotation, IDrawable representation) {
		return new GETeslaEffect(map, worldMask, position, attributes.getFloat("maxDistance") );
	}
	
}