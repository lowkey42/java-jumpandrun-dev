package de.secondsystem.game01.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsfml.audio.ConstSoundBuffer;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.ShaderSourceException;
import org.jsfml.graphics.Texture;

import de.secondsystem.game01.impl.graphic.AnimationTexture;
import de.secondsystem.game01.impl.graphic.SpriteTexture;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.collections.LruCache;


public abstract class ResourceManager<T> {

	public static final ResourceManager<ConstShader> shader_frag = new ResourceManager<ConstShader>(10) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "shader");
		}
		@Override protected ConstShader load(Path path) {
			Shader shader = new Shader();
			try {
				shader.loadFromFile(path, Shader.Type.FRAGMENT);
			} catch (IOException e) {
				throw new GameException(e.getMessage(), e);
			} catch (ShaderSourceException e) {
				throw new GameException(e.getMessage(), e);
			}
			return shader;
		}
	};
	
	public static final ResourceManager<ConstShader> shader = new ResourceManager<ConstShader>(10) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "shader");
		}
		@Override protected ConstShader load(Path path) {
			Shader shader = new Shader();
			try {
				shader.loadFromFile(Paths.get(path.toString()+".vert"), Paths.get(path.toString()+".frag"));
			} catch (IOException e) {
				throw new GameException(e.getMessage(), e);
			} catch (ShaderSourceException e) {
				throw new GameException(e.getMessage(), e);
			}
			return shader;
		}
	};

	public static final ResourceManager<ConstShader> shader_vert = new ResourceManager<ConstShader>(10) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "shader");
		}
		@Override protected ConstShader load(Path path) {
			Shader shader = new Shader();
			try {
				shader.loadFromFile(path, Shader.Type.VERTEX);
			} catch (IOException e) {
				throw new GameException(e.getMessage(), e);
			} catch (ShaderSourceException e) {
				throw new GameException(e.getMessage(), e);
			}
			return shader;
		}
	};

	public static final ResourceManager<SpriteTexture> texture_gui = new TextureResourceManager(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "gui");
		}
	};

	public static final ResourceManager<SpriteTexture> texture_tiles = new TextureResourceManager(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "tiles");
		}
	};

	public static final ResourceManager<SpriteTexture> texture = new TextureResourceManager(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "textures");
		}
	};
	
	public static final ResourceManager<AnimationTexture> animation = new ResourceManager<AnimationTexture>(100) {
		@Override protected Path getBasePath() {
			return Paths.get("assets", "animations");
		}
		@Override protected AnimationTexture load(Path path) {
			try {
				return new AnimationTexture(path);
			} catch (IOException e) {
				throw new GameException(e.getMessage(), e);
			}
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
				throw new GameException(e.getMessage(), e);
			}
			return sound;
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
				throw new GameException(e.getMessage(), e);
			}
			return font;
		}
	};
	
	private static abstract class TextureResourceManager extends ResourceManager<SpriteTexture> {
		private TextureResourceManager( int maxSize ) {
			super(maxSize);
		}
		@Override protected SpriteTexture load(Path path) {
			
			try {
				Texture normals = null;
				Texture altTexture = null;
				Texture altNormals = null;
				
				Texture texture = new Texture();
				texture.loadFromFile(path);

				Path normalsPath = addSuffix(path, "_n");
				if( Files.isRegularFile(normalsPath) ) {
					try {
						normals = new Texture();
						normals.loadFromFile(normalsPath);
					} catch (IOException e) {
						System.err.println("Unable to load normal-texture from: "+normalsPath);
						normals = null;
					}
				}

				Path altTexturePath = addSuffix(path, "_o");
				if( Files.isRegularFile(altTexturePath) ) {
					try {
						altTexture = new Texture();
						altTexture.loadFromFile(altTexturePath);
					} catch (IOException e) {
						System.err.println("Unable to load alt-texture from: "+altTexturePath);
						altTexture = null;
					}
				}

				Path altNormalsPath = addSuffix(path, "_on");
				if( Files.isRegularFile(altNormalsPath) ) {
					try {
						altNormals = new Texture();
						altNormals.loadFromFile(altNormalsPath);
					} catch (IOException e) {
						System.err.println("Unable to load normal-texture from: "+altNormalsPath);
						altNormals = null;
					}
				}
				
				return new SpriteTexture(texture, normals, altTexture, altNormals);
				
			} catch (IOException e) {
				throw new GameException(e.getMessage(), e);
			}
		}
	}
	
	
	private final LruCache<String, T> cache;
	
	private ResourceManager( int maxSize ) {
		cache = new LruCache<>(maxSize, new MyLoader());
	}
	
	protected abstract T load( Path path );
	
	protected abstract Path getBasePath();

	public final T getNullable( String name ) {
		try {
			return cache.get(name);
		} catch (GameException e) {
			return null;
		}
	}
	public final T get( String name ) throws GameException {
		return cache.get(name);
	}
	
	private static Path addSuffix(Path path, String suffix) {
		String[] parts = path.getFileName().toString().split("\\.");
		StringBuilder str = new StringBuilder();
		for( int i=0; i<parts.length-1; ++i )
			str.append(i>0 ? "." : "").append(parts[i]);
		
		return path.subpath(0, path.getNameCount()-1).resolve(str.toString()+suffix+"."+parts[parts.length-1]); 
	}
	
	private final class MyLoader implements LruCache.Loader<String, T> {

		@Override
		public T load(String key) {
			return ResourceManager.this.load(getBasePath().resolve(key));
		}
		
	}
}
