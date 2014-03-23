package de.secondsystem.game01.impl.scripting;

import org.jsfml.audio.Sound;
import org.jsfml.graphics.Sprite;

public interface IScriptApi {

	void loadMap(String mapId);

	void playMonologue(String name);

	void store(String key, Object val);

	Object load(String key);

	boolean exists(String key);

	Sound playSound(String name);

	Sprite createSprite(String texture, float x, float y);

	void deleteSprite(Sprite sprite);

	void updateSpriteTex(Sprite sprite, String texture);

	void updateSpriteColor(Sprite sprite, int r, int g, int b, int a);

}