package de.secondsystem.game01.impl.map;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.map.GameMap.GameWorld;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.util.SerializationUtil;


/**
 * 
 * Format
 * {world0:{tileset:bla, backgroundColor:#ffffff, layers:[]}, world1:{}}
 * 
 * @author lowkey
 *
 */
public class JsonGameMapSerializer implements IGameMapSerializer {

	private static final Path MAP_PATH = Paths.get("assets", "maps");
	
	private JSONParser parser = new JSONParser();
	
	@SuppressWarnings("unchecked")
	@Override
	public void serialize( GameMap map) {
		JSONObject obj = new JSONObject();
		obj.put("world", Arrays.asList(serializeGameWorld(map.gameWorld[0]), serializeGameWorld(map.gameWorld[1])));
		obj.put("tileset", map.getTileset().name );
		obj.put("scripts", map.scripts.list());
		obj.put("entityManager", map.getEntityManager().serialize());
		
		try ( Writer writer = Files.newBufferedWriter( MAP_PATH.resolve(map.getMapId()+".json"), StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '"+MAP_PATH.resolve(map.getMapId()+".json")+"': "+e.getMessage(), e);
		}
	}

	@Override
	public synchronized GameMap deserialize(GameContext ctx, String mapId, boolean playable, boolean editable) {
		try ( Reader reader = Files.newBufferedReader(MAP_PATH.resolve(mapId+".json"), StandardCharsets.UTF_8) ){
			Attributes obj = new Attributes( (JSONObject) parser.parse(reader) );
			
			List<Attributes> worlds = obj.getObjectList("world");
			
			Tileset tileset = new Tileset(obj.getString("tileset"));
			
			GameMap map = new GameMap(ctx, mapId, tileset, playable, editable);
			for( WorldId worldId : WorldId.values() )
				deserializeGameWorld(map, worldId, worlds.get(worldId.arrayIndex));
			
			List<String> scripts = obj.getList("scripts");
			for( String scriptName : scripts ) {
				map.getScriptEnv().queueLoad(scriptName);
			}
			
			map.getEntityManager().deserialize( obj.getObject("entityManager"));
			
			return map;
			
		} catch (IOException | ParseException e) {
			throw new FormatErrorException("Unable to parse map-file '"+MAP_PATH.resolve(mapId+".json")+"': "+e.getMessage(), e);
		} catch (ScriptException e) {
			throw new FormatErrorException("Unable to parse map-file (Script-Error) '"+MAP_PATH.resolve(mapId)+"': "+e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject serializeGameWorld(GameWorld world) {
		JSONObject obj = new JSONObject();
		obj.put("backgroundColor", SerializationUtil.encodeColor(world.backgroundColor) );
		obj.put("ambientLight", SerializationUtil.encodeColor(world.ambientLight) );
		obj.put("layer", serializeLayers(world.graphicLayer) );
		obj.put("backgroundMusic", world.backgroundMusic);
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeLayers(ILayer[] layers) {
		JSONArray array = new JSONArray();
		
		for( ILayer l : layers ) {
			Attributes attr = l.serialize();
			
			if( attr!=null )
				array.add(attr);
		}

		return array;
	}

	private void deserializeGameWorld(GameMap map, WorldId worldId, Attributes attributes) {
		map.gameWorld[worldId.arrayIndex].backgroundColor = SerializationUtil.decodeColor(attributes.getString("backgroundColor"));
		map.gameWorld[worldId.arrayIndex].ambientLight = SerializationUtil.decodeColor(attributes.getString("ambientLight"));
		map.gameWorld[worldId.arrayIndex].backgroundMusic = attributes.getString("backgroundMusic");
		
		deserializeLayers(map, worldId, attributes.getObjectList("layer"));
	}
	
	private void deserializeLayers( IGameMap map, WorldId worldId, List<Attributes> layerAttributes) {
		for( Attributes layer : layerAttributes )
			for( Attributes obj : layer.getObjectList("objects") )
				map.addNode(worldId, LayerType.valueOf(layer.getString("layerType")), deserializeLayerObject(map, worldId, obj));
	}

	private ILayerObject deserializeLayerObject(IGameMap map, WorldId worldId, Attributes attributes) {
		final LayerObjectType type = LayerObjectType.getByShortId(attributes.getString("$type"));
		
		if( type==null )
			throw new FormatErrorException("Unknown LayerObjectType: "+attributes.get("$type"));
		
		return type.create(map, worldId, attributes);
	}


}
