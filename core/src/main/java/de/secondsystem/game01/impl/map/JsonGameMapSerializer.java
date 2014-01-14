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

import org.jsfml.graphics.Color;
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
		obj.put("entities", map.getEntityManager().serialize());
		obj.put("events", map.getSequenceManager().serialize());
		
		try ( Writer writer = Files.newBufferedWriter( MAP_PATH.resolve(map.getMapId()), StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '"+MAP_PATH.resolve(map.getMapId())+"': "+e.getMessage(), e);
		}
	}

	@Override
	public synchronized GameMap deserialize(GameContext ctx, String mapId, boolean playable, boolean editable) {
		try ( Reader reader = Files.newBufferedReader(MAP_PATH.resolve(mapId), StandardCharsets.UTF_8) ){
			JSONObject obj = (JSONObject) parser.parse(reader);
			
			@SuppressWarnings("unchecked")
			List<JSONObject> worlds = (List<JSONObject>) obj.get("world");
			
			Tileset tileset = new Tileset((String)obj.get("tileset"));
			
			Color ambientLight = SerializationUtil.decodeColor((String)obj.get("ambientLight"));
			
			GameMap map = new GameMap(ctx, mapId, tileset, ambientLight!=null?ambientLight:Color.WHITE, playable, editable);
			for( WorldId worldId : WorldId.values() )
				deserializeGameWorld(map, worldId, worlds.get(worldId.arrayIndex));
			
			Object scriptsObj = obj.get("scripts");
			if( scriptsObj instanceof JSONArray ) {
				for( Object scriptName : ((JSONArray) scriptsObj) ) {
					map.getScriptEnv().load(scriptName.toString());
				}
			}
			
			map.getEntityManager().deserialize( (JSONArray) obj.get("entities"));
			map.getSequenceManager().deserialize(map, (JSONObject) obj.get("events"));
			
			return map;
			
		} catch (IOException | ParseException e) {
			throw new FormatErrorException("Unable to parse map-file '"+MAP_PATH.resolve(mapId)+"': "+e.getMessage(), e);
		} catch (ScriptException e) {
			throw new FormatErrorException("Unable to parse map-file (Script-Error) '"+MAP_PATH.resolve(mapId)+"': "+e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject serializeGameWorld(GameWorld world) {
		JSONObject obj = new JSONObject();
		obj.put("backgroundColor", SerializationUtil.encodeColor(world.backgroundColor) );
		obj.put("layer", serializeLayers(world.graphicLayer) );
		
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

	private void deserializeGameWorld(GameMap map, WorldId worldId, JSONObject obj) {
		map.gameWorld[worldId.arrayIndex].backgroundColor = SerializationUtil.decodeColor((String)obj.get("backgroundColor"));
		
		deserializeLayers(map, worldId, (JSONArray)obj.get("layer"));
	}
	@SuppressWarnings("unchecked")
	private void deserializeLayers( IGameMap map, WorldId worldId, JSONArray layerArray) {
		for( JSONObject l : (Iterable<JSONObject>) layerArray )
			for( Object obj : ((JSONArray) l.get("objects")) )
				map.addNode(worldId, LayerType.valueOf((String)l.get("layerType")), deserializeLayerObject(map, worldId, (JSONObject) obj));
	}

	@SuppressWarnings("unchecked")
	private ILayerObject deserializeLayerObject(IGameMap map, WorldId worldId, JSONObject obj) {
		final LayerObjectType type = LayerObjectType.getByShortId((String) obj.get("$type"));
		
		if( type==null )
			throw new FormatErrorException("Unknown LayerObjectType: "+obj.get("$type"));
		
		return type.create(map, worldId, obj);
	}


}
