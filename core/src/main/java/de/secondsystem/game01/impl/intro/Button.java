package de.secondsystem.game01.impl.intro;

import java.nio.file.Path;
import java.io.IOException;

import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Sprite;

/**
 * Diese Klasse stellt einen Button mit diversen Funktionen bereit
 * @author Sebastian
 *
 */
public final class Button {

	Path file;
	String name;
	int pos_x, pos_y;
	
	// Konstruktoren
	Button(String name, Path file, int pos_x, int pos_y){
		this.file = file;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.name = name;
	}
	
	// Methoden	
	void create(){
		
		// TODO --> Automatische Übername des Strings "name" des ausführenden Objekts
		Texture newbutton = new Texture();
		
			try {
				// Versuche Texturdatei zu laden
				newbutton.loadFromFile(file);
			
				System.out.println("DATEI ERFOLGREICH EINGEBUNDEN!");
			} catch(IOException ex) {
				//Ouch! something went wrong
				System.out.println("DATEI KONNTE NICHT GELADEN WERDEN!");
				ex.printStackTrace();
			}
		
		Sprite newsprite = new Sprite(newbutton);
		newsprite.setPosition(pos_x, pos_y);
			
	}
	
	void draw(){
		
		// TODO --> Überprüfung ob zuvor erfolgreich eine Textur geladen wurde
		
	}
	
}
