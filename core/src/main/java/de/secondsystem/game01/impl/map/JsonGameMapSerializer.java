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

import de.secondsystem.game01.impl.map.GameMap.GameWorld;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;


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
		
		try ( Writer writer = Files.newBufferedWriter( MAP_PATH.resolve(map.getMapId()), StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '"+MAP_PATH.resolve(map.getMapId())+"': "+e.getMessage(), e);
		}
	}

	@Override
	public synchronized GameMap deserialize(String mapId, boolean playable, boolean editable) {
		try ( Reader reader = Files.newBufferedReader(MAP_PATH.resolve(mapId), StandardCharsets.UTF_8) ){
			JSONObject obj = (JSONObject) parser.parse(reader);
			
			@SuppressWarnings("unchecked")
			List<JSONObject> worlds = (List<JSONObject>) obj.get("world");
			
			Tileset tileset = new Tileset((String)obj.get("tileset"));
			
			GameMap map = new GameMap(mapId, tileset, playable, editable);
			for( int i=0; i<=1; ++i )
				deserializeGameWorld(map, i, worlds.get(i));
			
			Object scriptsObj = obj.get("scripts");
			if( scriptsObj instanceof JSONArray ) {
				for( Object scriptName : ((JSONArray) scriptsObj) ) {
					map.loadScript(scriptName.toString());
				}
			}
			
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
		obj.put("backgroundColor", encodeColor(world.backgroundColor) );
		obj.put("layer", serializeLayers(world.graphicLayer) );
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeLayers(Layer[] layers) {
		JSONArray array = new JSONArray();
		
		for( Layer l : layers ) {
			JSONObject layer = new JSONObject();
			layer.put("layerType", l.type.name());
			
			JSONArray layerObjs = new JSONArray();
			for( LayerObject obj : l.objects )
				layerObjs.add(serializeLayerObject(obj));
			
			layer.put("objects", layerObjs);
			
			array.add( layer );
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONObject serializeLayerObject(LayerObject objects) {
		JSONObject obj = new JSONObject();
		
		obj.put("$type", objects.typeUuid().shortId);
		obj.putAll(objects.getAttributes());
		
		return obj;
	}

	private void deserializeGameWorld(GameMap map, int worldId, JSONObject obj) {
		map.gameWorld[worldId].backgroundColor = decodeColor((String)obj.get("backgroundColor"));
		
		deserializeLayers(map, worldId, (JSONArray)obj.get("layer"));
	}
	@SuppressWarnings("unchecked")
	private void deserializeLayers( IGameMap map, int worldId, JSONArray layerArray) {
		for( JSONObject l : (Iterable<JSONObject>) layerArray )
			for( Object obj : ((JSONArray) l.get("objects")) )
				map.addNode(worldId, LayerType.valueOf((String)l.get("layerType")), deserializeLayerObject(map, worldId, (JSONObject) obj));
	}

	@SuppressWarnings("unchecked")
	private LayerObject deserializeLayerObject(IGameMap map, int worldId, JSONObject obj) {
		final LayerObjectType type = LayerObjectType.getByShortId((String) obj.get("$type"));
		
		if( type==null )
			throw new FormatErrorException("Unknown LayerObjectType: "+obj.get("$type"));
		
		return type.create(map, worldId, obj);
	}


	private String encodeColor(Color color) {
		return "#"+encodeColorComponent(color.r)+encodeColorComponent(color.g)+encodeColorComponent(color.b)+encodeColorComponent(color.a);
	}
	private String encodeColorComponent(int c) {
		String s = Integer.toHexString(c);
		assert( s.length()==2 || s.length()==1 );
		
		return s.length()==2 ? s : "0"+s;
	}

	private Color decodeColor(String str) {
		assert( str.startsWith("#") );
		return new Color(
				decodeColorComponent(str.substring(1, 3)),
				decodeColorComponent(str.substring(3, 5)),
				decodeColorComponent(str.substring(5, 7)),
				decodeColorComponent(str.substring(7, 9)) );
	}
	private int decodeColorComponent(String str) {
		return Integer.parseInt(str, 16);
	}
}
