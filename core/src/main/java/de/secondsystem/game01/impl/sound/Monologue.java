package de.secondsystem.game01.impl.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;

import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.util.SerializationUtil;

public final class Monologue implements IDrawable, IUpdateable {

	private static final class Sentence {
		final String text;
		final Color color;
		final String soundBite;
		final int skipWaitMs;
		
		Sentence(String text, Color color, String soundBite, int skipWaitMs) {
			this.text = text;
			this.color = color;
			this.soundBite = soundBite;
			this.skipWaitMs = skipWaitMs;
		}
		Sentence(Attributes attr) {
			this(
				attr.getString("text"), 
				SerializationUtil.decodeColor(attr.getString("color")), 
				attr.getString("soundBite"), 
				attr.getInteger("lenght", 5));
		}
	}
	
	private final List<Sentence> sentences;
	
	public Monologue(Attributes attr) {
	//	attr.get
		
		List<Sentence> s = new ArrayList<>();
		
		
		sentences = Collections.unmodifiableList(s);
	}

	@Override
	public void update(long frameTimeMs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		// TODO Auto-generated method stub
		
	}

}
