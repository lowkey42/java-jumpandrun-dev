package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIf;

public class SpawnEventHandler implements IEventHandler {

	private final IGameEntityManager em;
	
	private final String type;
	
	private final Float relativeX, relativeY;
	
	private final Attributes attributes;
	
	public SpawnEventHandler(IGameMap map, Attributes attributes) {
		this.em = map.getEntityManager();
		this.type = attributes.getString("type");
		this.attributes = attributes.getObject("attributes"); 
		this.relativeX = attributes.getFloat("relX");
		this.relativeY = attributes.getFloat("relY");
	}
	
	@Override
	public Object handle(Object... args) {
		IGameEntity self = (IGameEntity) args[0];
		
		float sr = (float) Math.sin(Math.toRadians(self.getRotation()));
		float cr = (float) Math.cos(Math.toRadians(self.getRotation()));
		
		float rx = (relativeX!=null ? relativeX : 0) * cr - (relativeY!=null ? relativeY : 0) * sr;
		float ry = (relativeX!=null ? relativeX : 0) * sr - (relativeY!=null ? relativeY : 0) * cr;

		return em.create(type, new Attributes(attributes, new Attributes(
				new AttributeIf(relativeX!=null, "x", rx+self.getPosition().x),
				new AttributeIf(relativeY!=null, "y", ry+self.getPosition().y),
				new Attribute("rotation", self.getRotation()),
				new Attribute("worldId", self.getWorldMask())
		)) );
	}

	@Override
	public Attributes serialize() {
		return new Attributes(new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(SpawnEHF.class.getName())));
	}
	
}

final class SpawnEHF implements IEventHandlerFactory {
	@Override
	public SpawnEventHandler create(IGameMap map, Attributes attributes) {
		return new SpawnEventHandler(map, attributes);
	}
}
