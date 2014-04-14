package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.FinalizeState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.gui.GUIGameState;
import de.secondsystem.game01.impl.gui.GUITestState;
import de.secondsystem.game01.impl.gui.LayoutElementContainer;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.LayoutDirection;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * TODO
 * 
 */
public final class MainMenuState extends GUIGameState {

	private final GameState playGameState; 

	private final Sprite backdrop = new Sprite();

	@Override
	protected Layout getLayout() {
		return new Layout(LayoutDirection.VERTICAL, 100);
	}

	@Override
	protected Vector2f getPosition() {
		return new Vector2f(800, 200);
	}

	@Override
	protected void initGui(GameContext ctx, LayoutElementContainer c) {
		c.createButton("NEW GAME", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(new MainGameState("test01", null));
			}
		});

		c.createButton("EDITOR", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(EditorGameState.create(MainMenuState.this, "test01"));
			}
		});

		c.createButton("GUI TEST SITE", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(new GUITestState(MainMenuState.this, playGameState, backdrop));
			}
		});

		c.createButton("SETTINGS", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(new SettingsMenuState(MainMenuState.this, playGameState, backdrop));
			}
		});

		c.createButton("EXIT GAME", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(new FinalizeState());
			}
		});
	}
	
	// Constructors
	public MainMenuState() {
		this.playGameState = null;
	}

	public MainMenuState(GameState playGameState) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
	}

	@Override
	protected void onStart(GameContext ctx) {
		super.onStart(ctx);

		if (backdrop.getTexture() == null) {
			Texture backdropBuffer = new Texture();
			// Creating Backdrop Texture via monitor screenshot of the stage
			// before, rendered on every frame
			try {
				backdropBuffer.create(ctx.getViewWidth()*2, ctx.getViewHeight()*2);
			} catch (TextureCreationException e) {
				e.printStackTrace();
			}
			backdropBuffer.update(ctx.window);
			backdrop.setTexture(backdropBuffer, true);
		}
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		ctx.window.clear();

		ctx.window.draw(backdrop);

		super.onFrame(ctx, frameTime);

	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		super.processEvent(ctx, event);
		
		switch (event.type) {
			case KEY_RELEASED:
				if ( playGameState!=null && event.asKeyEvent().key == Key.ESCAPE)
					setNextState(playGameState);
		}
	}
}
