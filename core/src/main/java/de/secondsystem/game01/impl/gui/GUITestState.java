/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Mouse;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.ResourceManager;

/**
 * @author Sebastian
 *
 */
public final class GUITestState extends GameState {

	
	private final GameState playGameState, MainMenu;
	private Sprite backdrop = new Sprite();
	
	private final Text infoInputText;
	private final Text infoMemoText;
	
	// Object creations
	InputText testText = new InputText(50, 50, 200);
	MemoText testMemo = new MemoText(50, 250, 250, 90);
	
	MenuButton testButton = new MenuButton(50, 100, "TEST Button", new IOnClickListener() {
		
		@Override
		public void onClick() {
			System.out.println("Test Button works!");
		}
	});
	

	MenuButton backButton = new MenuButton(1000, 655, "BACK", new IOnClickListener(){
		@Override
		public void onClick() {
			setNextState(MainMenu);
		}
	});
		
	
	public GUITestState(GameState MainMenu, GameState playGameState,
			Sprite backdrop) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
		this.MainMenu = MainMenu;
		this.backdrop = backdrop;
		try {
			// Loading standard Font
			ConstFont myFont = ResourceManager.font.get("VeraMono.ttf");
			infoInputText = new Text("Input Text", myFont, 20);
			infoMemoText = new Text("Memo Editor", myFont, 20);
			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	}
	
	
	@Override
	protected void onStart(GameContext ctx) {

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
		
		infoInputText.setPosition(testText.getPos().x, testText.getPos().y - 30);
		infoMemoText.setPosition(testMemo.getPos().x, testMemo.getPos().y - 30);
	}

	
	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		
		ctx.window.clear();
		
		ctx.window.draw(backdrop);
		
		for (Event event : ctx.window.pollEvents()) {
			switch (event.type) {
			case CLOSED:
				ctx.window.close();
				break;
			case MOUSE_WHEEL_MOVED:
				testMemo.scrollText(event);
				break;
			case MOUSE_BUTTON_RELEASED:
				if( event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT ) {
					testButton.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y);
					backButton.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y);
					if(ctx.getMousePosition().x >= testText.getPos().x && ctx.getMousePosition().x <= testText.getPos().x + testText.width  && 
							ctx.getMousePosition().y >= testText.getPos().y && ctx.getMousePosition().y <= testText.getPos().y + testText.height){
								testText.setActive();
					} else { testText.setInactive();}
					if(ctx.getMousePosition().x >= testMemo.getPos().x && ctx.getMousePosition().x <= testMemo.getPos().x + testMemo.width  && 
							ctx.getMousePosition().y >= testMemo.getPos().y && ctx.getMousePosition().y <= testMemo.getPos().y + testMemo.height){
								testMemo.setActive();
					} else { testMemo.setInactive();}
				}
				break;
			case TEXT_ENTERED:
				if(event.asTextEvent().unicode <= 127 && event.asTextEvent().unicode >= 32){
					//System.out.println("TEXT ENTERED UNICODE: " + event.asTextEvent().unicode);
					testText.newKey(event);
					testMemo.newKey(event);
				// Backspace pushed
				} else if (event.asTextEvent().unicode == 8){
					testText.removeKey();
					testMemo.removeKey();
				// Return pushed
				} else if (event.asTextEvent().unicode == 13){
					System.out.println("Sent Input Text: " + testText.finalizeInput());
					System.out.println("Sent Memo Text: " + testMemo.finalizeInput());
				}
				break;
			case KEY_RELEASED:
				if (event.asKeyEvent().key == Key.ESCAPE)
					testText.setInactive();
				if ( playGameState!=null && event.asKeyEvent().key == Key.ESCAPE)
					setNextState(playGameState);
			}
		}

		ctx.window.draw(infoInputText);
		ctx.window.draw(infoMemoText);
		
		testText.draw(ctx.window);
		testButton.draw(ctx.window);
		testMemo.draw(ctx.window);
		backButton.draw(ctx.window);
		
	}	
	
}