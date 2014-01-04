package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class SequencedEntityEventHandler implements IEventHandler {
	
	private ISequencedObject sequencedObject;
	
	public SequencedEntityEventHandler(EventType eventType, ISequencedObject sequencedObject) {
		this.sequencedObject = sequencedObject;
	}
	
	public SequencedEntityEventHandler(IGameMap map, Attributes attributes) {

		Attributes jSeqObject = attributes.getObject("sequencedObject");
		ISequencedObject seqObj = map.getSequenceManager().createSequencedObject( jSeqObject.getString("class"));	
		ISequencedObject so = seqObj.deserialize(jSeqObject, map);
		this.sequencedObject = so != null ? so : seqObj;
	}
	
	@Override
	public Object handle(Object... args) {
		return sequencedObject.handle(args[0] instanceof IGameEntity ? (IGameEntity) args[0] : null, args);
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(SequencedEHF.class.getName())), 
				new Attribute("sequencedObject",sequencedObject.serialize()));
	}
	
}

final class SequencedEHF implements IEventHandlerFactory {
	@Override
	public SequencedEntityEventHandler create(IGameMap map, Attributes attributes) {
		return new SequencedEntityEventHandler(map, attributes);
	}
}
