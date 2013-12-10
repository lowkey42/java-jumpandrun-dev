package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.jsfml.graphics.RenderTarget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.secondsystem.game01.impl.game.entities.events.CollectionEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.game.entities.events.impl.SequencedEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.SequencedObject;
import de.secondsystem.game01.impl.map.FormatErrorException;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public final class GameEntityManager implements IGameEntityManager {

	private static final Path ARCHETYPE_PATH = Paths.get("assets", "entities");
	private static final Path ENTITIES_PATH = Paths.get("assets", "entities", "saved entities");
	
	private final Map<UUID, IGameEntity> entities = new HashMap<>();
	
	private static final LoadingCache<String, EntityArchetype> ARCHETYPE_CACHE = 
			CacheBuilder.newBuilder().concurrencyLevel(3).maximumSize(100).build(new ArchetypeLoader());
	
	final IGameMap map;
	
	public GameEntityManager(IGameMap map) {
		this.map = map;
	}
	
	@Override
	public Set<String> listArchetypes() {
		return Collections.unmodifiableSet( new HashSet<String>(Arrays.asList(ARCHETYPE_PATH.toFile().list())) );
	}
	
	@Override
	public IControllableGameEntity createControllable( String type, Map<String, Object> args ) {
		return (IControllableGameEntity) create(type, args);
	}

	@Override
	public IGameEntity create(String type, Map<String, Object> attr) {
		return create(UUID.randomUUID(), type, attr);
	}
	
	@Override
	public IGameEntity create(UUID uuid, String type, Map<String, Object> attr) {
		try {
			EntityArchetype at = ARCHETYPE_CACHE.get(type);
			
			if( at==null )
				throw new EntityCreationException("Unknown archetype '"+type+"' for entity: "+uuid);
			
			IGameEntity e = at.create(uuid, this, attr);
		
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
	
	
	private static final class EntityArchetype {
		public final String archetype;
		public final Constructor<? extends IGameEntity> constructor;
		public final Map<String, Object> attributes;
		
		public EntityArchetype(String archetype, Map<String, Object> attributes) throws EntityCreationException {
			this.archetype = archetype;
			this.attributes = attributes;
			String className = (String) attributes.get("class");
			if(className==null)
				className = GameEntity.class.getCanonicalName();
			
			try {
				@SuppressWarnings("unchecked")
				Class<? extends IGameEntity> clazz = (Class<? extends IGameEntity>) getClass().getClassLoader().loadClass(className);
				constructor = clazz.getConstructor( UUID.class, GameEntityManager.class, IGameMap.class, Attributes.class );
				
			} catch (ClassNotFoundException | NoSuchMethodException e) {
				throw new EntityCreationException("Unable to load GameEntity-Class with required constructor: "+e.getMessage(), e);
			} catch (SecurityException e) {
				throw new Error(e.getMessage(), e);
			}
		}
		
		public IGameEntity create(UUID uuid, GameEntityManager em, Map<String, Object> attr) {
			try {
				IGameEntity entity = constructor.newInstance(uuid, em, em.map, new Attributes( attributes, attr) );
				entity.setEditableState(new EditableEntityStateImpl(archetype, new Attributes(attr)) );
				return entity;
				
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new EntityCreationException("Unable to create instance of "+constructor.getDeclaringClass()+": "+e.getMessage(), e);
			}
		}
	}
	
	private static final class ArchetypeLoader extends CacheLoader<String, EntityArchetype> {
		
		private JSONParser parser = new JSONParser();
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized EntityArchetype load(String key) throws Exception {
			try ( Reader reader = Files.newBufferedReader(ARCHETYPE_PATH.resolve(key), StandardCharsets.UTF_8) ){
				JSONObject obj = (JSONObject) parser.parse(reader);
				
				return new EntityArchetype(key, Collections.unmodifiableMap(obj));
				
			} catch (IOException | ParseException e) {
				System.err.println("Unable to load entity archetype: "+e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		
	}

	private static final class EditableEntityStateImpl implements IEditableEntityState {

		private final String archetype;
		
		private final Attributes attributes;
		
		EditableEntityStateImpl(String archetype, Attributes attributes) {
			this.archetype = archetype;
			this.attributes = attributes;
		}
		
		@Override
		public String getArchetype() {
			return archetype;
		}

		@Override
		public Attributes getAttributes() {
			return attributes;
		}
		
	}
	
//	@Override
//	public void deserialize(Iterator<SerializedEntity> iter) {
//		while( iter.hasNext() ) {
//			SerializedEntity se = iter.next();
//			create(se.uuid(), se.archetype(), se.attributes());
//		}
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void serialize() {
		JSONObject obj = new JSONObject();
		
		JSONArray jArray = new JSONArray();
		for(IGameEntity entity : entities.values()) {
			JSONObject se = new JSONObject();
			se.put("uuid", entity.uuid().toString());		
			se.put("archetype", entity.getEditableState().getArchetype());	
			se.put("attributes", entity.getEditableState().getAttributes());
			se.put("eventHandler", entity.getEventHandler().serialize());		// TODO: geht das auch anders (Darstellung im Editor evtl. problematisch)
		}
		
		obj.put("entities", jArray);
		
		try ( Writer writer = Files.newBufferedWriter(ENTITIES_PATH, StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '" + ENTITIES_PATH + "': " + e.getMessage(), e);
		}
	}
	
	@Override
	public void deserialize() {
		JSONParser parser = new JSONParser();
		
		try ( Reader reader = Files.newBufferedReader(ENTITIES_PATH, StandardCharsets.UTF_8) ) {
			JSONObject obj = (JSONObject) parser.parse(reader);
			JSONArray jArray = (JSONArray) obj.get("entities");
			for(Object o : jArray) {
				// deserialize game entity
				JSONObject jObj = (JSONObject) o; 
				final UUID uuid = UUID.fromString( (String) jObj.get("uuid") );
				final String archetype = (String) jObj.get("archetype");
				@SuppressWarnings("unchecked")
				HashMap<String, Object> attributes = (HashMap<String, Object>) jObj.get("attributes");
				CollectionEntityEventHandler eventHandler = new CollectionEntityEventHandler();
				eventHandler.deserialize((JSONObject) jObj.get("eventHandler"), map);			
				
				IGameEntity entity = create(uuid, archetype, attributes);
				entity.setEventHandler(eventHandler);
				
				entities.put(entity.uuid(), entity);
			}
			
		} catch (IOException | ParseException e) {
			throw new FormatErrorException("Unable to parse map-file '" + ENTITIES_PATH + "': " + e.getMessage(), e);
		}
	}


//	@Override
//	public Iterable<SerializedEntity> serialize() {
//		return new SEIterable();
//	}

//	private static final class SerializedEntityImpl implements SerializedEntity {
//
//		private final UUID uuid;
//		private final String archetype;
//		private final Map<String, Object> attributes;
//		
//		public SerializedEntityImpl(UUID uuid, String archetype, Map<String, Object> attributes) {
//			this.uuid = uuid;
//			this.archetype = archetype;
//			this.attributes = attributes;
//		}
//		
//		@Override
//		public UUID uuid() {
//			return uuid;
//		}
//
//		@Override
//		public String archetype() {
//			return archetype;
//		}
//
//		@Override
//		public Map<String, Object> attributes() {
//			return attributes;
//		}
//		
//	}
	
//	private final class SEIterable implements Iterable<SerializedEntity> {
//		@Override public Iterator<SerializedEntity> iterator() {
//			return new SEIterator(entities);
//		}
//	}
//	private final class SEIterator implements Iterator<SerializedEntity> {
//
//		private final Iterator<Entry<UUID, IGameEntity>> iter;
//		
//		public SEIterator(Map<UUID, IGameEntity> entities) {
//			iter = Collections.unmodifiableMap(entities).entrySet().iterator();
//		}
//		
//		@Override
//		public boolean hasNext() {
//			return iter.hasNext();
//		}
//
//		@Override
//		public SerializedEntity next() {
//			Entry<UUID, IGameEntity> entity = iter.next();
//
//			Map<String, Object> attributes = entity.getValue().serialize().clone();
//			
//			try {
//				EntityArchetype at = ARCHETYPE_CACHE.get(entity.getValue().getArchetype());
//				
//				if( at!=null ) {
//					for( Entry<String, Object> e : at.attributes.entrySet() )
//						if( e.getValue().equals(attributes.get(e.getKey())) )
//								attributes.remove(e.getKey());
//				}
//				
//			} catch (ExecutionException e) {
//				throw new Error(e.getMessage(), e);
//			}
//			
//			return new SerializedEntityImpl(entity.getKey(), entity.getValue().getArchetype(), attributes);
//		}
//
//		@Override
//		public void remove() {
//			throw new UnsupportedOperationException("remove is not allowed");
//		}
//		
//	}
}
