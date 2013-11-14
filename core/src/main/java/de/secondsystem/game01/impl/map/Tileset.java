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

public class Tileset {

	public final String name;
	
	public final List<ConstTexture> tiles;
	
	public Tileset( String name ) {
		this.name = name;
		
		try {
			// "\s*" ignores any number of whitespace characters
			// split("\\s*,\\s*") ignores "   ,   " in example: "file1   ,   file2" and splits file1
			// and file2 into two strings
			String[] tileFiles = readFile("assets/tilesets/"+name+".txt").split("\\s*,\\s*");
			
			List<ConstTexture> tTiles = new ArrayList<ConstTexture>(tileFiles.length);
			// go through the string array (tileFiles), create for each(:) file a texture and add it to tTiles
			for( String fn : tileFiles ) {
				Texture texture = new Texture();
				texture.loadFromFile(Paths.get("assets/tiles/"+fn));
				
				tTiles.add(texture);
			}
			
			// save tTiles in a constant/unmodifiable list (no write access)
			tiles = Collections.unmodifiableList(tTiles);
			
		} catch( IOException e ) {
			throw new Error("Unable to load tileset: "+name, e);
		}
	}
	
	// some magic
	static String readFile(String path ) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
