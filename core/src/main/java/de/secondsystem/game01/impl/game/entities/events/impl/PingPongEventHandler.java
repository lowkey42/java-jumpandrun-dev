package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.SingleEntityEventHandler;
import de.secondsystem.game01.impl.map.IGameMap;

public final class PingPongEventHandler extends SingleEntityEventHandler {

	private EntityEventType out;
	
	public PingPongEventHandler(UUID uuid, EntityEventType in, EntityEventType out ) {
		super(uuid, in);
		this.out = out;
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
		newArgs.set(0, owner);
		System.out.println("ping");
		return ((IGameEntity)args[0]).getEventHandler().handle(out, ((IGameEntity)args[0]), newArgs.toArray());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject data = super.serialize();
		data.put("out", out.toString());
		
		return data;
	}
	
	@Override
	public IEntityEventHandler deserialize(JSONObject obj, IGameMap map) {
		out = EntityEventType.valueOf((String) obj.get("out"));
		return super.deserialize(obj, map);
	}

}
