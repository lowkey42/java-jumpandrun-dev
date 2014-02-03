package de.secondsystem.game01.impl.sound;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.map.FormatErrorException;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.util.SerializationUtil;

public final class Monologue {

	private static final Path BASE_PATH = Paths.get("assets", "monologues");
	
	public static final class Sentence {
		public final String text;
		public final Color color;
		public final Sound soundBite;
		public final int skipWaitMs;
		
		Sentence(String text, Color color, String soundBiteName, int skipWaitMs) {
			this.text = text;
			this.color = color!=null ? color : Color.WHITE;
			this.soundBite = loadSoundBite(soundBiteName);
			this.skipWaitMs = (int) Math.max(skipWaitMs, /*TODO: Bug in SFML return wrong value: soundBite!=null ? soundBite.getBuffer().getDuration().asMilliseconds() :*/ 0);
		}
		Sentence(Attributes attr) {
			this(
				attr.getString("text"), 
				SerializationUtil.decodeColor(attr.getString("color")), 
				attr.getString("soundBite"), 
				attr.getInteger("lenght", 1000));
		}
	}
	
	private final List<Sentence> sentences;
	
	public Monologue(Attributes attr) {
		List<Attributes> sentencesAttr = attr.getObjectList("sentences");
		
		List<Sentence> s = new ArrayList<>(sentencesAttr.size());
		for( Attributes a : sentencesAttr )
			s.add(new Sentence(a));
		
		sentences = Collections.unmodifiableList(s);
	}

	public int getNextSentence(int currentSentence, long timePassed) {
		if( currentSentence==-1 )
			return -1;
		
		final Sentence cs = sentences.get(currentSentence);
		if( timePassed>=cs.skipWaitMs )
			return currentSentence+1<sentences.size() ? currentSentence+1 : -1;
		
		return currentSentence;
	}

	private static Sound loadSoundBite( String name ) {
		if( name==null || name.trim().isEmpty() )
			return null;
		
		try {
			SoundBuffer sb = new SoundBuffer();
			sb.loadFromFile(BASE_PATH.resolve(name));
			
			Sound s = new Sound(sb);
			s.setAttenuation(0);
			
			return new Sound(sb);
			
		} catch (IOException e) {
			System.err.println("Unable to load sound-bite from '"+BASE_PATH.resolve(name)+"': "+e.getMessage());
			return null;
		}
	}

	public void updateText(int sentence, Text text) {
		Sentence s = sentences.get(sentence);
		
		text.setColor(s.color);
		text.setString(s.text);
		FloatRect textRect = text.getLocalBounds();
		text.setOrigin(textRect.left + textRect.width/2.0f,
		               textRect.top  + textRect.height/2.0f);
		
		if( s.soundBite!=null )
			s.soundBite.play();
	}
	

	private static final JSONParser parser = new JSONParser();
	
	public static Monologue load(String name) {
		try ( Reader reader = Files.newBufferedReader(BASE_PATH.resolve(name+".json"), StandardCharsets.UTF_8) ){
			return new Monologue(new Attributes( (JSONObject) parser.parse(reader) ));
			
		} catch (IOException | ParseException e) {
			throw new FormatErrorException("Unable to parse monologue-file '"+BASE_PATH.resolve(name+".json")+"': "+e.getMessage(), e);
		}
	}
}
