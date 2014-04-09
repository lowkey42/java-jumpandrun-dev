package de.secondsystem.game01.impl.map;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.ScriptException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.map.GameMap.GameWorld;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.impl.scripting.IScriptApi;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
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
		obj.put("layer", serializeLayers(map.gameWorld[0], map.gameWorld[1]));
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
	public synchronized GameMap deserialize(GameContext ctx, String mapId, IScriptApi scriptApi, boolean playable, boolean editable) {
		try ( Reader reader = Files.newBufferedReader(MAP_PATH.resolve(mapId+".json"), StandardCharsets.UTF_8) ){
			Attributes obj = new Attributes( (JSONObject) parser.parse(reader) );
			
			List<Attributes> worlds = obj.getObjectList("world");
			
			Tileset tileset = new Tileset(obj.getString("tileset"));
			
			GameMap map = new GameMap(ctx, mapId, tileset, scriptApi, playable, editable);
			for( WorldId worldId : WorldId.values() )
				deserializeGameWorld(map, worldId, worlds.get(worldId.arrayIndex));
			
			deserializeLayerObjects(map, obj.getObjectList("layer"));
			
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
		obj.put("backgroundMusic", world.backgroundMusic);
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeLayers(GameWorld gameWorld1, GameWorld gameWorld2) {
		JSONArray layers = new JSONArray();
		
		for( LayerType l : LayerType.values() ) {
			Set<ILayerObject> loSet = new HashSet<>(gameWorld1.graphicLayer[l.layerIndex].listAll().size() + gameWorld1.graphicLayer[l.layerIndex].listAll().size());
			loSet.addAll(gameWorld1.graphicLayer[l.layerIndex].listAll());
			loSet.addAll(gameWorld2.graphicLayer[l.layerIndex].listAll());
			
			List<Attributes> loSerialized = new ArrayList<>(loSet.size());
			for( ILayerObject lo : loSet ) {
				loSerialized.add(lo.serialize());
			}
			
			layers.add(new Attributes( 
					new Attribute("layerType", l.toString()),
					new Attribute("objects", loSerialized)
			));
		}
		
		return layers;
	}

	private void deserializeLayerObjects(GameMap map,
			List<Attributes> layerAttributes) {
		for( Attributes layer : layerAttributes )
			for( Attributes objAttr : layer.getObjectList("objects") ) {
				ILayerObject obj = deserializeLayerObject(map, objAttr);
				
				for( WorldId worldId : WorldId.values() )
					if( obj.isInWorld(worldId) )
						map.addNode(worldId, LayerType.valueOf(layer.getString("layerType")), obj);
			}
		
	}

	private void deserializeGameWorld(GameMap map, WorldId worldId, Attributes attributes) {
		map.gameWorld[worldId.arrayIndex].backgroundColor = SerializationUtil.decodeColor(attributes.getString("backgroundColor"));
		map.gameWorld[worldId.arrayIndex].ambientLight = SerializationUtil.decodeColor(attributes.getString("ambientLight"));
		map.gameWorld[worldId.arrayIndex].backgroundMusic = attributes.getString("backgroundMusic");
	}

	private ILayerObject deserializeLayerObject(IGameMap map, Attributes attributes) {
		final LayerObjectType type = LayerObjectType.getByShortId(attributes.getString("$type"));
		
		if( type==null )
			throw new FormatErrorException("Unknown LayerObjectType: "+attributes.get("$type"));
		
		return map.createNode(type, attributes);
	}


}
