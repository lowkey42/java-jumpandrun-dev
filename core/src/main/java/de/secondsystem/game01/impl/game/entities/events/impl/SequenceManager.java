package de.secondsystem.game01.impl.game.entities.events.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.FormatErrorException;

public final class SequenceManager {
	private static final Path SEQUENCE_PATH = Paths.get("assets", "events", "sequences", "test01");
	
	private final Map<UUID, ISequencedObject> sequencedObjects = new HashMap<>();
	private final Map<UUID, SequencedEntity> sequencedEntities = new HashMap<>();
	
	public SequenceManager() {
		
	}
	
	public ISequencedObject getSequencedObject(UUID uuid) {
		return sequencedObjects.get(uuid);
	}
	
	public SequencedEntity getSequencedEntity(UUID uuid) {
		return sequencedEntities.get(uuid);
	}
	
	public ISequencedObject createSequencedObject() {
		SequencedObject seqObj = new SequencedObject(UUID.randomUUID());
		sequencedObjects.put(seqObj.uuid, seqObj);
		
		return seqObj;
	}
	
	public Toggle createToggle() {
		Toggle toggle = new Toggle(UUID.randomUUID());
		sequencedObjects.put(toggle.uuid(), toggle);
		
		return toggle;
	}
	
	public Playback createPlayback() {
		Playback playback = new Playback(UUID.randomUUID());
		sequencedObjects.put(playback.uuid(), playback);
		
		return playback;
	}
	
	public Condition createCondition() {
		Condition condition = new Condition(UUID.randomUUID());
		sequencedObjects.put(condition.uuid(), condition);
		
		return condition;
	}
	
	public AnimatedSequencedEntity createAnimatedSequencedEntity(IGameEntity owner) {
		AnimatedSequencedEntity e = new AnimatedSequencedEntity(UUID.randomUUID(), owner);
		sequencedEntities.put(e.uuid(), e);
		
		return e;
	}
	
	public ControllableSequencedEntity createControllableSequencedEntity(IGameEntity owner, PatrollingController controller) {
		ControllableSequencedEntity e = new ControllableSequencedEntity(UUID.randomUUID(), owner, controller);
		sequencedEntities.put(e.uuid(), e);
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public void serialize() {
		JSONObject obj = new JSONObject();
		
		JSONArray seqObjects = new JSONArray();
		for(ISequencedObject seqObj : sequencedObjects.values()) 
			seqObjects.add(seqObj.serialize());
		
		JSONArray seqEntities = new JSONArray();
		for(SequencedEntity seqObj : sequencedEntities.values()) 
			seqEntities.add(seqObj.serialize());
		
			obj.put("sequencedObjects", seqObjects);
			obj.put("sequencedEntities", seqEntities);
		
		try ( Writer writer = Files.newBufferedWriter(SEQUENCE_PATH, StandardCharsets.UTF_8) ){
			obj.writeJSONString(writer);
			
		} catch (IOException e) {
			throw new FormatErrorException("Unable to write map-file '" + SEQUENCE_PATH + "': " + e.getMessage(), e);
		}
	}
	
	public void deserialize(IGameEntityManager entityManager) {
		JSONParser parser = new JSONParser();
		
		try ( Reader reader = Files.newBufferedReader(SEQUENCE_PATH, StandardCharsets.UTF_8) ) {
			JSONObject obj = (JSONObject) parser.parse(reader);
			JSONArray seqObjects = (JSONArray) obj.get("sequencedObjects");
			for(Object o : seqObjects) {
				ISequencedObject seqObj = new SequencedObject();
				seqObj.deserialize((JSONObject) o, entityManager, this);
				sequencedObjects.put(seqObj.uuid(), seqObj);
			}
			
			JSONArray seqEntities = (JSONArray) obj.get("sequencedEntities");
			for(Object o : seqEntities) {
				SequencedEntity seqEntity = new SequencedEntity();
				seqEntity.deserialize((JSONObject) o, entityManager, this);
				sequencedEntities.put(seqEntity.uuid(), seqEntity);
			}
			
		} catch (IOException | ParseException e) {
			throw new FormatErrorException("Unable to parse map-file '" + SEQUENCE_PATH + "': " + e.getMessage(), e);
		}
	}
}
