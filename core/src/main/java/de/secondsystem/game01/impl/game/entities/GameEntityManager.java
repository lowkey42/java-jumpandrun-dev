package de.secondsystem.game01.impl.game.entities;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.secondsystem.game01.impl.gui.ThumbnailButton.ThumbnailData;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.GameException;

public final class GameEntityManager implements IGameEntityManager {

	private static final Path ARCHETYPE_PATH = Paths.get("assets", "entities");
	
	private final boolean overrideOptionalCreation;
	
	private final Map<UUID, IGameEntity> entities = new HashMap<>();
	@SuppressWarnings("unchecked")
	private final List<IGameEntity>[] orderedEntities = new ArrayList[256];
	private final List<Byte> orderedEntitiesKeys = new ArrayList<>(5);
	
	private final Set<UUID> entitiesToDestroy = new HashSet<>();
	private final Set<IGameEntity> entitiesToAdd = new HashSet<>();
	
	private static final LoadingCache<String, EntityArchetype> ARCHETYPE_CACHE = 
			CacheBuilder.newBuilder().concurrencyLevel(3).maximumSize(100).build(new ArchetypeLoader());
	
	final IGameMap map;
	
	public GameEntityManager(IGameMap map, boolean overrideOptionalCreation) {
		this.map = map;
		this.overrideOptionalCreation = overrideOptionalCreation;
	}
	
	@Override
	public List<String> listArchetypes() {
		return Collections.unmodifiableList( new ArrayList<String>(Arrays.asList(ARCHETYPE_PATH.toFile().list())) );
	}
	
	private List<ThumbnailData> thumbnailDatas;
	
	@Override
	public List<ThumbnailData> generateThumbnails() {
		if( thumbnailDatas!=null )
			return thumbnailDatas;
		
		List<ThumbnailData> td = new ArrayList<ThumbnailData>();
		
		UUID uuid = UUID.randomUUID();
		try {
			long t = System.currentTimeMillis();
			RenderTexture texture = new RenderTexture();
			texture.create(100, 100);
			
			for( String a : listArchetypes() ) {
				EntityArchetype at = ARCHETYPE_CACHE.get(a);
				
				if( at!=null ) {
					IGameEntity e = at.create(uuid, this, new Attributes(new Attribute("x", 0), new Attribute("y", 0)));
					float size=Math.max(e.getWidth(), e.getHeight());
					texture.setView(new View(e.getPosition(), new Vector2f(size,size)));
					texture.clear(Color.BLACK);
					e.update(2000);
					e.draw(texture);
					texture.display();

					// store and load texture to fix weird SFML bug (RenderTexture turn blank after a while)
					Path p = Paths.get("assets", "tmp", a+".bmp");
					try {
						texture.getTexture().copyToImage().saveToFile(p);
					} catch (IOException e1) {
						e1.printStackTrace();
					}					
					Texture tex = new Texture();
					tex.loadFromFile(p);
					Files.deleteIfExists(p);
					
					td.add(new ThumbnailData(a, tex, new IntRect(0, 0, 100, 100)));
					
					e.onDestroy();
				}
			}
			
			System.out.println("Time: "+(System.currentTimeMillis()-t));
			
		} catch (ExecutionException | TextureCreationException | IOException e) {
			throw new GameException(e);
		}
		
		return thumbnailDatas=td;
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
	public IGameEntity createEntity(String type, Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IGameEntity create(UUID uuid, String type, Map<String, Object> attr) {
		if( overrideOptionalCreation ) {
			final Object createCondition = attr.get("createIf");
			if( createCondition!=null )
				try {
					if( !Boolean.valueOf(map.getScriptEnv().eval(createCondition.toString()).toString()) )
						return null;
					
				} catch (ScriptException e) {
					e.printStackTrace();
				}
		}
		
		if( uuid==null )
			uuid = UUID.randomUUID();
			
		try {
			EntityArchetype at = ARCHETYPE_CACHE.get(type);
			
			if( at==null )
				throw new EntityCreationException("Unknown archetype '"+type+"' for entity: "+uuid);
			
			IGameEntity e = at.create(uuid, this, attr);
		
			entitiesToAdd.add(e);
			return e;
			
		} catch (ExecutionException e) {
			throw new Error(e.getMessage(), e);
		}
	}
	
	@Override
	public void addEntity(IGameEntity entity) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void destroyEntity(UUID eId) {
		throw new UnsupportedOperationException();
		// editor requirement
	}
	
	@Override
	public void destroy( UUID eId ) {
		entitiesToDestroy.add(eId);
	}
	
	private void destroyAndAddEntites() {
		for( IGameEntity e : entitiesToAdd ) {
			if( entities.put(e.uuid(), e)==null ) {
				int oId = e.orderId()+128;
				
				List<IGameEntity> sg = orderedEntities[oId];
				if( sg==null ) {
					sg = new ArrayList<>();
					orderedEntities[oId] = sg;
					orderedEntitiesKeys.add(e.orderId());
					Collections.sort(orderedEntitiesKeys);
				}
				
				sg.add(e);
			}
		}
		entitiesToAdd.clear();
		
		for( UUID eId : entitiesToDestroy ) {
			IGameEntity entity = entities.get(eId);
			if( entity!=null ) {
				entity.onDestroy();
				entities.remove(eId);
				List<IGameEntity> sg = orderedEntities[entity.orderId()+128];
				if( sg!=null ) {
					sg.remove(entity);
				}
			}
		}
		entitiesToDestroy.clear();
	}

	@Override
	public IGameEntity get( UUID eId ) {
		return entities.get(eId);
	}
	@Override
	public IWeakGameEntityRef getRef(final UUID eId) {
		return new IWeakGameEntityRef() { 
			private IGameEntity entity;
			@Override public UUID uuid() {
				return eId;
			}
			
			@Override public IGameEntityManager manager() {
				return GameEntityManager.this;
			}
			
			@Override public IGameEntity get() {
				if( entity!=null )	return entity;
				return entity= GameEntityManager.this.get(eId);
			}
		};
	}

	@Override
	public void draw(final WorldId worldId, final RenderTarget rt) {
		destroyAndAddEntites();
		
		for( Byte orderId : orderedEntitiesKeys )
			for( IGameEntity entity : orderedEntities[orderId+128] )
				if( entity.isInWorld(worldId) )
					entity.draw(rt);
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
				entity.setEditableState(new EditableEntityStateImpl(this, new Attributes(attr)) );
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

		private final EntityArchetype archetype;
		
		private final Attributes attributes;
		
		EditableEntityStateImpl(EntityArchetype archetype, Attributes attributes) {
			this.archetype = archetype;
			this.attributes = attributes;
		}
		
		@Override
		public String getArchetype() {
			return archetype.archetype;
		}

		@Override
		public Attributes getAttributes() {
			return new Attributes(attributes, archetype.attributes);
		}
	}
	
	@Override
	public Attributes serialize() {	
		final List<Attributes> entityAttributes = new ArrayList<>(entities.size());
		for(IGameEntity entity : entities.values())
			entityAttributes.add( filterEntityAttributes(entity.serialize(), entity.getEditableState().getArchetype()) );
		
		return new Attributes(
				new Attribute("entities", entityAttributes)
		); 
	}
	
	private static final Attributes filterEntityAttributes( Attributes attributes, String archetype ) {
		try {
			EntityArchetype at = ARCHETYPE_CACHE.get(archetype);
			
			if( at!=null ) {
				for( Entry<String, Object> e : at.attributes.entrySet() ) {
					Object o1 = e.getValue();
					Object o2 = attributes.get(e.getKey());
					
					if( compObject(o1, o2) )
						attributes.remove(e.getKey());
				}
			}
			
		} catch (ExecutionException e) {
			throw new Error(e.getMessage(), e);
		}
		
		return attributes;
	}
	
	private static boolean compObject(Object o1, Object o2) {
		return o1.equals(o2) 
				|| (o1 instanceof Number && o2 instanceof Number && !o1.getClass().equals(o2.getClass()) && compNumber(o1,o2))
				|| (o1 instanceof Collection && o2 instanceof Collection && compCollection(o1,o2)) 
				|| (o1 instanceof Map && o2 instanceof Map && compMap(o1,o2));
	}
	
	private static boolean compNumber(Object o1, Object o2) {
		if( o1 instanceof Double || o1 instanceof Float || o2 instanceof Double || o2 instanceof Float )
			return ((Number) o1).doubleValue()==((Number)o2).doubleValue();
		
		else
			return ((Number) o1).longValue()==((Number)o2).longValue();
	}

	@SuppressWarnings("unchecked")
	private static boolean compMap(Object o1, Object o2) {
		Map<Object, Object> m1 = (Map<Object, Object>) o1;
		Map<Object, Object> m2 = (Map<Object, Object>) o2;
		
		if( m1.size()!=m2.size() )
			return false;

		for( Entry<Object, Object> e : m1.entrySet() ) {
			Object so1 = e.getValue();
			Object so2 = m2.get(e.getKey());
			
			if( !compObject(so1, so2) )
				return false;
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	private static boolean compCollection(Object o1, Object o2) {
		Collection<Object> c1 = (Collection<Object>) o1;
		Collection<Object> c2 = (Collection<Object>) o2;
		
		outer: for( Object so1 : c1 ) {
			for( Object so2 : c2 )
				if( compObject(so1, so2) )
					continue outer;
			
			return false;
		}
		
		return true;
	}

	@Override
	public void deserialize(Attributes attributes) {
		if( attributes==null )
			return;
		
		final List<Attributes> entityAttributes = attributes.getObjectList("entities");
		
		if( entityAttributes==null )
			return;
		
		for(Attributes entityAttr : entityAttributes) {
			final UUID uuid = UUID.fromString( entityAttr.getString("uuid") );
			final String archetype = entityAttr.getString("archetype");
			
			create(uuid, archetype, entityAttr);
		}
		
		destroyAndAddEntites();
	}

	@Override
	public ArrayList<String> getArchetypes() {
		ArrayList<String> list = new ArrayList<>();
		final File entityFolder = ARCHETYPE_PATH.toFile();
		
	    for (final File fileEntry : entityFolder.listFiles())
	    	if (fileEntry.isDirectory()) 
	           list.addAll(listFilesForFolder(fileEntry));
	        else 
	           list.add(fileEntry.getName()); 
	        
		return list;
	}
	
	private ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> list = new ArrayList<>();
		
	    for (final File fileEntry : folder.listFiles()) 
	    	list.add(fileEntry.getName());
	    
	    return list;
	}

	@Override
	public IGameEntity findEntity(Vector2f pos) {
		for(IGameEntity entity : entities.values()) {
			if( entity.inside(pos) )
				return entity;
		}
		
		return null;
	}

}
