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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.map.GameMap.World;


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
			deserializeWorld( obj.get("world0") );
			deserializeWorld( obj.get("world1") );
			// TODO: Stuff
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private JSONObject serializeWorld(World world) {
		return null;
		// TODO Auto-generated method stub
		
	}

	private World deserializeWorld(Object object) {
		return null;
		// TODO Auto-generated method stub
		
	}

}
