package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstTexture;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.AnimationTexture;
import de.secondsystem.game01.model.GameException;

public final class Style {

	public final ConstFont labelFont;
	
	public final int labelFontSize;

	public final ConstFont buttonFont;
	
	public final int buttonFontSize;

	public final ConstFont textFont;
	
	public final int textFontSize;
	
	public final AnimationTexture buttonTexture;
	
	public final ConstTexture sliderTexture;
	
	public final AnimationTexture checkBoxTexture;
	
	public final int dropDownSuggestionsFontSize;
	
	public final boolean autoFocus;

	public Style(ConstFont labelFont, int labelFontSize, ConstFont textFont, int textFontSize, 
			ConstFont buttonFont, int buttonFontSize, AnimationTexture buttonTexture, 
			ConstTexture sliderTexture, AnimationTexture checkBoxTexture, int dropDownSuggestionsFontSize,
			boolean autoFocus) {
		super();
		this.labelFont = labelFont;
		this.labelFontSize = labelFontSize;
		this.buttonFont = buttonFont;
		this.buttonFontSize = buttonFontSize;
		this.textFont = textFont;
		this.textFontSize = textFontSize;
		this.buttonTexture = buttonTexture;
		this.sliderTexture = sliderTexture;
		this.autoFocus = autoFocus;
		this.checkBoxTexture = checkBoxTexture;
		this.dropDownSuggestionsFontSize = dropDownSuggestionsFontSize;
	}
	
	public Style setButtonTexture( AnimationTexture tex ) {
		return new Style(labelFont, labelFontSize, textFont, textFontSize, buttonFont, buttonFontSize, tex, sliderTexture, checkBoxTexture, 
				dropDownSuggestionsFontSize, autoFocus);
	}
	
	public static Style createDefaultStyle() {
		try {
			return new Style(
					ResourceManager.font.get("FreeSans.otf"), 20, // label
					ResourceManager.font.get("FreeSans.otf"), 20, // text
					ResourceManager.font.get("FreeSans.otf"), 26, ResourceManager.animation.get("coolButton.anim"), // button 
					ResourceManager.texture_gui.get("VolumeButton.png"), // slider
					ResourceManager.animation.get("coolCheckBox.anim"), // checkBox
					14, // dropdown
					false);
			
		} catch (IOException e) {
			throw new GameException("Unable to load files for default gui-style: "+e.getMessage(), e);
		}
	}
}
