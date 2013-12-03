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
	
	// Object creation
	InputText testText = new InputText(50, 50, 200, "Test Text");
	MemoText testMemo = new MemoText(50, 250, 250, 90, "This is a Test Memo");
	
	MenuButton testButton = new MenuButton("TEST BUTTON", 50, 100, new MenuButton.IOnClickListener(){
		@Override
		public void onClick() {
			System.out.println("Test Button works!");
		}
	});

	MenuButton backButton = new MenuButton("BACK", 1000, 655, new MenuButton.IOnClickListener(){
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
		
		infoInputText.setPosition(testText.pos_x, testText.pos_y - 25);
		infoMemoText.setPosition(testMemo.pos_x, testMemo.pos_y - 25);
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		
		ctx.window.draw(backdrop);
		
		for (Event event : ctx.window.pollEvents()) {
			switch (event.type) {
			case CLOSED:
				ctx.window.close();
				break;
			case MOUSE_BUTTON_RELEASED:
				if( event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT ) {
					testButton.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
					backButton.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
					if(Mouse.getPosition(ctx.window).x >= testText.pos_x && Mouse.getPosition(ctx.window).x <= testText.pos_x + testText.width  && 
							   Mouse.getPosition(ctx.window).y >= testText.pos_y && Mouse.getPosition(ctx.window).y <= testText.pos_y + testText.height){
								testText.setActive();
					} else { testText.setInactive();}
					if(Mouse.getPosition(ctx.window).x >= testMemo.pos_x && Mouse.getPosition(ctx.window).x <= testMemo.pos_x + testMemo.width  && 
							   Mouse.getPosition(ctx.window).y >= testMemo.pos_y && Mouse.getPosition(ctx.window).y <= testMemo.pos_y + testMemo.height){
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
					System.out.println("Sent Text: " + testText.finalizeInput());
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
