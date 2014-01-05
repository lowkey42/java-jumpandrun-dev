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
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.Attributes;


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
		
		try ( Writer writer = Files.newBufferedWriter( MAP_PATH.resolve(map.getMapId()), StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '"+MAP_PATH.resolve(map.getMapId())+"': "+e.getMessage(), e);
		}
	}

	@Override
	public synchronized GameMap deserialize(String mapId, boolean playable, boolean editable) {
		try ( Reader reader = Files.newBufferedReader(MAP_PATH.resolve(mapId), StandardCharsets.UTF_8) ){
			Attributes obj = new Attributes( (JSONObject) parser.parse(reader) );
			
			List<Attributes> worlds = obj.getObjectList("world");
			
			Tileset tileset = new Tileset(obj.getString("tileset"));
			
			GameMap map = new GameMap(mapId, tileset, playable, editable);
			for( WorldId worldId : WorldId.values() )
				deserializeGameWorld(map, worldId, worlds.get(worldId.arrayIndex));
			
			List<String> scripts = obj.getList("scripts");
			for( String scriptName : scripts ) {
				map.getScriptEnv().load(scriptName);
			}
			
			map.getEntityManager().deserialize( obj.getObject("entityManager"));
			
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
			for( ILayerObject obj : l.objects )
				layerObjs.add(serializeLayerObject(obj));
			
			layer.put("objects", layerObjs);
			
			array.add( layer );
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONObject serializeLayerObject(ILayerObject objects) {
		JSONObject obj = new JSONObject();
		
		obj.put("$type", objects.typeUuid().shortId);
		obj.putAll(objects.getAttributes());
		
		return obj;
	}

	private void deserializeGameWorld(GameMap map, WorldId worldId, Attributes attributes) {
		map.gameWorld[worldId.arrayIndex].backgroundColor = decodeColor(attributes.getString("backgroundColor"));
		
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
