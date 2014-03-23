package de.secondsystem.game01.impl.scripting;

import org.jsfml.audio.Sound;
import org.jsfml.graphics.Sprite;

final class DummyScriptApi implements IScriptApi {

	@Override
	public void loadMap(String mapId) {
	}

	@Override
	public void playMonologue(String name) {
	}

	@Override
	public void store(String key, Object val) {
	}

	@Override
	public Object load(String key) {
		return null;
	}

	@Override
	public boolean exists(String key) {
		return false;
	}

	@Override
	public Sound playSound(String name) {
		return null;
	}

	@Override
	public Sprite createSprite(String texture, float x, float y) {
		return null;
	}

	@Override
	public void deleteSprite(Sprite sprite) {
	}

	@Override
	public void updateSpriteTex(Sprite sprite, String texture) {
	}

	@Override
	public void updateSpriteColor(Sprite sprite, int r, int g, int b, int a) {
	}

}
