package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.jsfml.graphics.RenderTarget;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public final class GameEntityManager implements IGameEntityManager {

	private final Map<UUID, IGameEntity> entities = new HashMap<>();
	
	private static final LoadingCache<String, Map<String, Object>> ARCHETYPE_CACHE = 
			CacheBuilder.newBuilder().concurrencyLevel(3).maximumSize(100).build(new ArchetypeLoader());
	
	final IGameMap map;
	
	public GameEntityManager(IGameMap map) {
		this.map = map;
	}
	
	@Override
	public IControllableGameEntity createControllable( String type, Map<String, Object> args ) {
		try {
			Attributes attributes = new Attributes( ARCHETYPE_CACHE.get(type), args );
			
			ControllableGameEntity e = new ControllableGameEntity(UUID.randomUUID(), this, map, GameEntityHelper.createEventHandler(this, attributes), attributes );
			entities.put(e.uuid(), e);
			
			return e;
			
		} catch (ExecutionException e) {
			throw new Error(e.getMessage(), e);
		}
	}
	
	@Override
	public void destroy( UUID eId ) {
		entities.remove(eId);
	}

	@Override
	public IGameEntity get( UUID eId ) {
		return entities.get(eId);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		for( IGameEntity entity : entities.values() )
			entity.draw(renderTarget);
	}

	@Override
	public void update(long frameTimeMs) {
		for( IGameEntity entity : entities.values() )
			entity.update(frameTimeMs);
	}
	
	
	private static final class ArchetypeLoader extends CacheLoader<String, Map<String, Object>> {

		private final Path BASE_PATH = Paths.get("assets", "entities");
		
		private JSONParser parser = new JSONParser();
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized Map<String, Object> load(String key) throws Exception {
			try ( Reader reader = Files.newBufferedReader(BASE_PATH.resolve(key), StandardCharsets.UTF_8) ){
				JSONObject obj = (JSONObject) parser.parse(reader);
					
				return Collections.unmodifiableMap(obj);
				
			} catch (IOException | ParseException e) {
				System.err.println("Unable to load entity archetype: "+e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		
	}
}
