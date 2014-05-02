package de.secondsystem.game01.impl.map.objects;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.ILayerObject.ILayerObjectFactory;
import de.secondsystem.game01.model.Attributes;

public enum LayerObjectType {

	SPRITE			("sp", new SpriteLayerObject.Factory()),
	COLLISION		("cl", new CollisionObject.Factory()),
	ENTITY			("et", new EntityLayerObject.Factory()),
	LIGHT			("li", new LightLayerObject.Factory()),
	PARTICLE_EMITTER("pe", new ParticleEmitterLayerObject.Factory());
	
	
	/** Shortened id of this type (must be unique) */
	public final String shortId;
	
	private final ILayerObjectFactory factory;
	
	private LayerObjectType(String shortId, ILayerObjectFactory factory) {
		this.shortId = shortId;
		this.factory = factory;
	}
	
	public static LayerObjectType getByShortId(String shortId) {
		for( LayerObjectType lot : values() )
			if( lot.shortId.equals(shortId) )
				return lot;
		
		return null;
	}
	
	public ILayerObject create( IGameMap map, Attributes attributes ) {
		return factory.create(map, attributes);
	}
	
}
