package de.secondsystem.game01.impl.intro;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Sprite;

/**
 * Diese Klasse stellt einen Button mit diversen Funktionen bereit
 * @author Sebastian
 *
 */
public final class Button {

	Path file;
	String title, text;
	int pos_x, pos_y;
	
	Sprite newsprite;
	
	// Konstruktoren
	Button(String text, Path file, int pos_x, int pos_y){
		this.file = file;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.text = text;
		
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
		
		newsprite = new Sprite(newbutton);
		newsprite.setPosition(pos_x, pos_y);
	}
	
	Button(String text, int pos_x, int pos_y){
		this(text, Paths.get("assets", "gui", "buttons", "ButtonNormal.png"), pos_x, pos_y);
	}
	
	// Methoden	
	
	// Sprite zeichnen
	void draw(RenderTarget rt){
		
		// TODO --> Überprüfung ob zuvor erfolgreich eine Textur geladen wurde
		rt.draw(newsprite);
	}
	
}
