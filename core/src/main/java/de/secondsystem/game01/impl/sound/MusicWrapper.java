package de.secondsystem.game01.impl.sound;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsfml.audio.Music;

import de.secondsystem.game01.model.IUpdateable;

public final class MusicWrapper implements IUpdateable {

	private static final Path BASE_PATH = Paths.get("assets", "music");
	
	private String nowPlaying;
	
	private Music music;
	
	private Music fadeTo;
	
	private long fadeTime;
	
	private long fadeTimePassed;

	public MusicWrapper() {
	}
	public MusicWrapper(String track) {
		music = load(track);
	}

	private static Music load(String name) {
		try {
			Music music = new Music();
			music.openFromFile(BASE_PATH.resolve(name));
			music.setAttenuation(0);
			music.setLoop(true);
			
			return music;
			
		} catch (IOException e) {
			System.out.println("unable to load music-file: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public void play() {
		if( music!=null )
			music.play();
	}
	public void pause() {
		if( music!=null )
			music.pause();
	}
	public void fade( String next, long fadeTimeMs ) {
		if( next!=null && !next.equalsIgnoreCase(nowPlaying) && (fadeTo = load(next))!=null ) {
			fadeTo.setVolume(0);
			fadeTo.play();
			fadeTime = fadeTimeMs;
			fadeTimePassed = 0;
			nowPlaying = next;
		}
	}

	@Override
	public void update(long frameTimeMs) {
		if( fadeTo!=null ) {
			fadeTimePassed+=frameTimeMs;
			
			if( fadeTimePassed<fadeTime && music!=null ) {
				fadeTo.setVolume(100* (fadeTimePassed/fadeTime));
				music.setVolume(100- fadeTo.getVolume());
				
			} else {
				if( music!=null )
					music.stop();
				
				fadeTo.setVolume(100);
				music = fadeTo;
				fadeTo = null;
			}
		}
	}
	
}
