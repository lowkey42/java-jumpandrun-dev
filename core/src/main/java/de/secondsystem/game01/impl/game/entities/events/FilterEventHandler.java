package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;

public class FilterEventHandler implements IEventHandler {

	public static enum Condition {
		IS_PLAYER,
		IS_ENTITY,
		IS_NOT_NULL
	}
	
	private static final String PLAYER_GROUP = "player";
	
	private final Condition condition;
	
	private final int argIndex;
	
	private final IEventHandler sub;
	
	public FilterEventHandler(Condition cond, int argIndex, IEventHandler sub) {
		this.condition = cond;
		this.argIndex = argIndex;
		this.sub = sub;
	}
	public FilterEventHandler(IGameMap map, Attributes attributes) {
		this(Condition.valueOf(attributes.getString("if")), attributes.getInteger("arg", 0), EventUtils.createEventHandler(map, attributes.getObject("then")));
	}

	@Override
	public Object handle(Object... args) {
		Object arg = argIndex<args.length ? args[argIndex] : null;
		
		switch (condition) {
			case IS_ENTITY:
				if( !(arg instanceof IGameEntity) )
					return null;
				break;

			case IS_PLAYER:
				if( !(arg instanceof IGameEntity) || !PLAYER_GROUP.equals(((IGameEntity) arg).group()) )
					return null;
				break;

			case IS_NOT_NULL:
				if( arg==null )
					return null;
				break;
		}
		
		return sub.handle(args);
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(CondEHF.class.getName())), 
				new AttributeIfNotNull("if", 		condition.name()),
				new AttributeIfNotNull("arg", 		argIndex),
				new AttributeIfNotNull("then", 		sub.serialize())
		);
	}
	
}

final class CondEHF implements IEventHandlerFactory {
	@Override
	public FilterEventHandler create(IGameMap map, Attributes attributes) {
		return new FilterEventHandler(map, attributes);
	}
}
