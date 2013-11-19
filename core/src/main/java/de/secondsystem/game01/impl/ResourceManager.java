package de.secondsystem.game01.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import org.jsfml.audio.ConstSoundBuffer;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public abstract class ResourceManager<T> {

	public static final ResourceManager<ConstTexture> texture_gui = new ResourceManager<ConstTexture>(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "gui");
		}
		@Override protected ConstTexture load(Path path) {
			Texture texture = new Texture();
			try {
				texture.loadFromFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return texture;
		}
	};

	public static final ResourceManager<ConstTexture> texture_tiles = new ResourceManager<ConstTexture>(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "tiles");
		}
		@Override protected ConstTexture load(Path path) {
			Texture texture = new Texture();
			try {
				texture.loadFromFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return texture;
		}
	};

	public static final ResourceManager<ConstTexture> texture = new ResourceManager<ConstTexture>(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "textures");
		}
		@Override protected ConstTexture load(Path path) {
			Texture texture = new Texture();
			try {
				texture.loadFromFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return texture;
		}
	};

	public static final ResourceManager<ConstFont> font = new ResourceManager<ConstFont>(5) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "fonts");
		}
		@Override protected ConstFont load(Path path) {
			Font font = new Font();
			try {
			    font.loadFromFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return font;
		}
	};
	
	public static final ResourceManager<ConstSoundBuffer> sound = new ResourceManager<ConstSoundBuffer>(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "sounds");
		}
		@Override protected ConstSoundBuffer load(Path path) {
			SoundBuffer sound = new SoundBuffer();
			try {
			    sound.loadFromFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return sound;
		}
	};
	
	
	private static final int CONCURRENCY_LEVEL = 5;
	
	private final LoadingCache<String, T> cache;
	
	private ResourceManager( int maxSize ) {
		cache = CacheBuilder.newBuilder().concurrencyLevel(CONCURRENCY_LEVEL).maximumSize(maxSize).build(new MyLoader());
	}
	
	protected abstract T load( Path path );
	
	protected abstract Path getBasePath();
	
	public final T get( String name ) throws IOException {
		try {
			return cache.get(name);
		} catch (ExecutionException e) {
			throw new IOException("error loading '"+name+"': "+e.getMessage(),e);
		}
	}
	
	private final class MyLoader extends CacheLoader<String, T> {

		@Override
		public T load(String key) throws Exception {
			return ResourceManager.this.load(getBasePath().resolve(key));
		}
		
	}
}
