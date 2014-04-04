package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.gui.Element.KeyType;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;

public abstract class GUIGameState extends GameState {

	private LayoutElementContainer baseContainer;
	
	private boolean guiInitialized = false;
	
	protected abstract void initGui(GameContext ctx, LayoutElementContainer container);
	
	protected abstract Vector2f getPosition();
	
	protected abstract Layout getLayout();
	
	@Override
	protected void onStart(GameContext ctx) {
		if( !guiInitialized ) {
			baseContainer = new LayoutElementContainer(getPosition().x, getPosition().y, 
					ctx.getViewWidth()-getPosition().x, ctx.getViewHeight()-getPosition().y, 
					Style.createDefaultStyle(), getLayout());
			initGui(ctx, baseContainer);
			guiInitialized = true;
		}
	}

	protected final void updateGui(GameContext ctx, long frameTime) {
		if( Mouse.isButtonPressed(Mouse.Button.LEFT) ) {
			baseContainer.onFocus(ctx.getMousePosition());
			baseContainer.onKeyPressed(KeyType.ENTER);
			baseContainer.onMouseOver(ctx.getMousePosition());
		}
		
		baseContainer.update(frameTime);
	}

	protected final void drawGui(RenderTarget rt) {
		baseContainer.draw(rt);
	}
	
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		updateGui(ctx, frameTime);
		drawGui(ctx.window);
	}

	@Override
	protected void processEvent(GameContext ctx, Event event) { // TODO: add gamepad-support
		switch (event.type) {
			case TEXT_ENTERED:
				baseContainer.onTextInput(event.asTextEvent().unicode);
				break;
				
			case MOUSE_BUTTON_PRESSED:
				baseContainer.onFocus(ctx.getMousePosition());
				baseContainer.onKeyPressed(KeyType.ENTER);
				
			case MOUSE_MOVED:
				baseContainer.onMouseOver(ctx.getMousePosition());
				break;
				
			case MOUSE_BUTTON_RELEASED:
				baseContainer.onKeyReleased(KeyType.ENTER);
				break;
				
			case KEY_PRESSED: {
				final KeyType kt = determineKeyType(event.asKeyEvent());
				if( kt!=null )
					baseContainer.onKeyPressed(kt);
				break;
			}
				
			case KEY_RELEASED: {
				final KeyType kt = determineKeyType(event.asKeyEvent());
				if( kt!=null )
					baseContainer.onKeyReleased(kt);
				break; }
	
			default:
				break;
		}
	}
	
	private KeyType determineKeyType(KeyEvent keyEvent) {
		switch (keyEvent.key) {
			case LEFT:			return KeyType.LEFT;
			case RIGHT:			return KeyType.RIGHT;
			case UP:			return KeyType.UP;
			case DOWN:			return KeyType.DOWN;
			case RETURN:		return KeyType.ENTER;
			case ESCAPE:		return KeyType.EXIT;
			case BACKSPACE:		return KeyType.BACKSPACE;
			case TAB:			return KeyType.TAB;
			case DELETE:		return KeyType.DEL;
	
			default:
				return null;
		}
	}
	
}
