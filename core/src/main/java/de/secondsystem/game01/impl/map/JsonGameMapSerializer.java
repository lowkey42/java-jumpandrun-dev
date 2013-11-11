package de.secondsystem.game01.impl.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.map.GameMap.World;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;


/**
 * 
 * Format
 * {world0:{tileset:bla, backgroundColor:#ffffff, layers:[]}, world1:{}}
 * 
 * @author lowkey
 *
 */
public class JsonGameMapSerializer implements IGameMapSerializer {

	private JSONParser parser = new JSONParser();
	
	@SuppressWarnings("unchecked")
	@Override
	public void serialize( Path out, GameMap map) {
		JSONObject obj = new JSONObject();
		obj.put("world", Arrays.asList(serializeWorld(map.world[0]), serializeWorld(map.world[1])));
		
		try ( Writer writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized GameMap deserialize(Path in) {

		try ( Reader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8) ){
			JSONObject obj = (JSONObject) parser.parse(reader);
			
			@SuppressWarnings("unchecked")
			List<JSONObject> worlds = (List<JSONObject>) obj.get("world");
			
			return new GameMap(deserializeWorld( worlds.get(0) ), deserializeWorld( worlds.get(1) ));
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private JSONObject serializeWorld(World world) {
		JSONObject obj = new JSONObject();
		obj.put("backgroundColor", encodeColor(world.backgroundColor) );
		obj.put("tileset", world.tileset.name );
		obj.put("layer", serializeLayers(world.graphicLayer) );
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeLayers(Layer[] layers) {
		JSONArray array = new JSONArray();
		
		for( Layer l : layers ) {
			JSONObject layer = new JSONObject();
			
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
		
		obj.put("$type", objects.typeUuid());
		obj.putAll(objects.getAttributes());
		
		return obj;
	}

	private World deserializeWorld(JSONObject obj) {
		World w = new World(decodeColor((String)obj.get("backgroundColor")), (String)obj.get("tileset") );
		deserializeLayers(w, (JSONArray)obj.get("layer"));
		
		return w;
	}
	private void deserializeLayers( World w, JSONArray array) {
		int i=0;
		
		for( Object l : array ) {
			for( Object obj : ((JSONArray) ((JSONObject)l).get("objects")) )
				w.graphicLayer[i].objects.add( deserializeLayerObject(w, (JSONObject) obj) );
			
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	private LayerObject deserializeLayerObject(World w, JSONObject obj) {		// TODO: use enum for TYPE_UUID & construction
		if( SpriteLayerObject.TYPE_UUID.equals(obj.get("$type")) )
			return SpriteLayerObject.create(w.tileset, obj);
		
		else if( CollisionObject.TYPE_UUID.equals(obj.get("$type")) )
			return CollisionObject.create(obj);
		
		else
			throw new Error("Aaaargggg!?!?###");
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
