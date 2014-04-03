/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public final class GUITestState extends GUIGameStateSimpleLayout {

	

	@Override
	protected int getElementSpacing() {
		return 50;
	}

	@Override
	protected int getXPosition() {
		return 50;
	}

	@Override
	protected int getYPosition() {
		return 200;
	}

	@Override
	protected void initGui(GameContext ctx) {
		createLabel("Input Text").setFor(createInputField(200, ""));
		createLabel("Memo Editor").setFor(createInputField(200, "")); // TODO: memo
		
		createButton("TEST Button", new IOnClickListener() {
			@Override public void onClick() {
				System.out.println("Test Button works!");
			}
		});
		
		createButton(1000, 655, "BACK", new IOnClickListener(){
			@Override public void onClick() {
				setNextState(MainMenu);
			}
		});
	}
	
	
	private final GameState MainMenu;
	private Sprite backdrop = new Sprite();
	
	
	public GUITestState(GameState MainMenu, GameState playGameState,
			Sprite backdrop) {
		// Transfering last State into playGameState
		this.MainMenu = MainMenu;
		this.backdrop = backdrop;
	}
	
	
	@Override
	protected void onStart(GameContext ctx) {
		super.onStart(ctx);

		if (backdrop.getTexture() == null) {
			Texture backdropBuffer = new Texture();
			// Creating Backdrop Texture via monitor screenshot of the stage
			// before, rendered on every frame
			try {
				backdropBuffer.create(ctx.settings.width, ctx.settings.height);
			} catch (TextureCreationException e) {
				e.printStackTrace();
			}
			backdropBuffer.update(ctx.window);
			backdrop.setTexture(backdropBuffer);
		}
	}

	
	@Override
	protected void onStop(GameContext ctx) {
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		ctx.window.clear();
		ctx.window.draw(backdrop);

		super.onFrame(ctx, frameTime);
		
	}
	
}