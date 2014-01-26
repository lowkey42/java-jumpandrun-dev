package de.secondsystem.game01.impl.map;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.Texture;

import de.secondsystem.game01.impl.ResourceManager;

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
	
	public ConstTexture get(int index) {
		return tiles.get( index % tiles.size() ).texture;
	}
	
	public ConstTexture getNormals(int index) {
		return tiles.get( index % tiles.size() ).normals;
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
			tTiles.add(new Tile( ResourceManager.texture_tiles.get(fn.trim()), ResourceManager.texture_tiles.getNullable(toNormalTextureName(fn.trim())) ));
		
		return tTiles;
	}
	
	private static String toNormalTextureName(String name) {
		String[] parts = name.split("\\.");
		StringBuilder str = new StringBuilder();
		for( int i=0; i<parts.length-1; ++i )
			str.append(i>0 ? "." : "").append(parts[i]);
		
		return str.toString()+"_n."+parts[parts.length-1]; 
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
	
	private static final class Tile {
		public final ConstTexture texture;
		public final ConstTexture normals;
		public Tile(ConstTexture texture, ConstTexture normals) {
			this.texture = texture;
			this.normals = normals;
		}
	}
}
