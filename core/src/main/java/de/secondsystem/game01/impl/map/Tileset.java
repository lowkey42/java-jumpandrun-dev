package de.secondsystem.game01.impl.map;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Texture;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.SpriteTexture;

public class Tileset {

	public final String name;
	
	private final List<Tile> tiles;
	
	public Tileset( String name ) {
		this.name = name;
		
		try {
			/** load file to String and split it on commas (whitespace are ignored); e.g. "tile1.png, tile2.jpeg" is parsed as ["tile1.png","tile2.jpeg"] */
			String[] tileFiles = readFile("assets/tilesets/"+name+".txt").split("\\s*,\\s*");
			
			// save textures in a unmodifiable list (not the list nor its content can be modified)
			tiles = Collections.unmodifiableList(loadTextures(tileFiles));
			
		} catch( IOException e ) {
			throw new Error("Unable to load tileset: "+name, e);
		}
	}
	
	public int size() {
		return tiles.size();
	}
	
	public SpriteTexture get(int index) {
		return tiles.get( index % tiles.size() );
	}
	
	public IntRect getClip(int index) {
		return tiles.get( index % tiles.size() ).clip;
	}
	
	public String getName(int index) {
		return tiles.get( index % tiles.size() ).name;
	}

	/** 
	 * Load each tile as a new {@link Texture}
	 * @param tileFiles tile-names as Strings
	 * @return the loaded textures
	 * @throws IOException at least one file couldn't be loaded
	 */
	private static final List<Tile> loadTextures( String... tileFiles ) throws IOException {
		List<Tile> tTiles = new ArrayList<Tile>(tileFiles.length);
		for( String fn : tileFiles )
			tTiles.add(loadTexture(fn));
		
		return tTiles;
	}
	
	private static final Pattern CLIPPING_PATTERN = Pattern.compile("(.*)\\s*\\{\\s*([0-9]+)\\s*;\\s*([0-9]+)\\s*;\\s*([0-9]+)\\s*;\\s*([0-9]+)\\s*\\}");
	
	private static Tile loadTexture(String fn) throws IOException {
		final IntRect clip;
		
		final Matcher m = CLIPPING_PATTERN.matcher(fn);
		if( m.matches() ) {
			fn = m.group(1);
			clip = new IntRect( Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5)) );
		} else
			clip = null;
		
		return new Tile(ResourceManager.texture_tiles.get(fn.trim()), fn, clip );
	}
	
	/**
	 * Read the file to a {@link String}
	 * @param path The path to the file
	 * @return The content of the file
	 * @throws IOException The file couldn't be opened/read
	 */
	static String readFile(String path ) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	private static final class Tile extends SpriteTexture {
		public final String name;
		public final IntRect clip;
		
		public Tile(SpriteTexture tex, String name, IntRect clip) {
			super(tex.texture, tex.normals, tex.altTexture, tex.altNormals);
			this.name = name;
			this.clip = clip;
			
			if( clip==null && (name.contains("tileable") || name.contains("_ta_")) ) {
				((Texture) texture).setRepeated(true);
				if( normals!=null )
					((Texture) normals).setRepeated(true);
			}
		}
	}
}
