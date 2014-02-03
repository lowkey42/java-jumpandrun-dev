package de.secondsystem.game01.impl.sound;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public final class MonologueTextBox implements IUpdateable, IDrawable {

	private Monologue monologue;
	private int sentence = -1;
	private long timePassed;
	
	private final Text text = new Text();
	
	public MonologueTextBox(ConstFont font, int size, Vector2f pos) {
		text.setCharacterSize(size);
		text.setFont(font);
		text.setPosition(pos);
	}

	public void play(String name) {
		if( (monologue = Monologue.load(name))!=null ) {
			monologue.updateText(sentence=0, text);
			timePassed=0;
		}
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		if( sentence!=-1 )
			renderTarget.draw(text);
	}

	@Override
	public void update(long frameTimeMs) {
		if( sentence==-1 )
			return;
			
		timePassed+=frameTimeMs;
		
		int ns = monologue.getNextSentence(sentence, timePassed);
		if( ns>sentence ) {
			timePassed = 0;
			sentence = ns;
			monologue.updateText(sentence, text);
			
		} else if( ns<sentence ) {
			timePassed = 0;
			sentence = ns;
			monologue = null;
		}
	}

}
