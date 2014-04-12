/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.util.Arrays;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.LayoutDirection;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;;

/**
 * @author Sebastian
 *
 */
public final class GUITestState extends GUIGameState {

	@Override
	protected Layout getLayout() {
		return new Layout(LayoutDirection.VERTICAL, 50);
	}

	@Override
	protected Vector2f getPosition() {
		return new Vector2f(50, 50);
	}

	@Override
	protected void initGui(GameContext ctx, LayoutElementContainer c) {
		c.createLabel("Input Text").setFor(c.createInputField(200, ""));
		
		new DropDownField<>(400, 0, 300, EventType.class, new SimpleRwValueRef<EventType>(null), c);
		
		c.createButton("TEST Button", new IOnClickListener() {
			@Override public void onClick() {
				System.out.println("Test Button works!");
			}
		});
		
		c.createButton(1000, 655, "BACK", new IOnClickListener(){
			@Override public void onClick() {
				setNextState(MainMenu);
			}
		});
		
		final Attributes ichBinFastEinEntity = new Attributes(
				new Attribute("interactive", true),
				new Attribute("x", 555),
				new Attribute("y", 42.23),
				new Attribute("seqTest", Arrays.asList("qwertz", "asd", "test")),
				new Attribute("objTest", new Attributes(new Attribute("asd", "fgh"), new Attribute("vim", "hjkl")))
		);
		
		c.createAttributesDataTable( ctx.getViewWidth()-600, 0, 500, new AttributesSource() {
			
			@Override public Attributes getAttributes() {
				return ichBinFastEinEntity;
			}

			@Override
			public void applyAttributes(Attributes newAttributes) {
				ichBinFastEinEntity.clear();
				ichBinFastEinEntity.putAll(newAttributes);
			}
		});
		
		// Datatable-Test
		//  später folgt noch eine Spezialisierung "AttributesDataTable" für den Editor 
//		c.createDataTable(1000, 100, 400,
//				Arrays.asList("test-2", "asd-42", "zzz-23", "hjkl-vim"),
//				Arrays.asList(
//					new DataTable.ColumnDef<String>() {
//						@Override public String getName() {				return "first Column"; }
//
//						@Override public float getWidthPercentage() {	return 0.5f; }
//
//						@Override public Element createValueElement(float width,
//								String data, LayoutElementContainer row) {
//							return row.createLabel(data.split("-")[0], width, 1);
//						}
//					},
//					new DataTable.ColumnDef<String>() {
//						@Override public String getName() {				return "another Column"; }
//
//						@Override public float getWidthPercentage() {	return 0.5f; }
//
//						@Override public Element createValueElement(float width,
//								String data, LayoutElementContainer row) {
//							if( "42".equals(data.split("-")[1]) ) {
//								return row.createInputField(width, new RwValueRef<String>() {
//									
//									private String prefix = "test";
//									
//									@Override
//									public void setValue(String value) {
//										prefix = value.split("-")[0];
//									}
//									
//									@Override
//									public String getValue() {
//										return prefix + "-" + (System.currentTimeMillis()/1000/10);
//									}
//								});
//							}
//							return row.createInputField(width, data.split("-")[1]);
//						}
//					}
//				));

		c.createStringGrid(300, 200, 5, 2, 200, 44);
		
		new CheckBox(300, 100, baseContainer);
	}
	
	
	private final GameState MainMenu;
	private Sprite backdrop = new Sprite();

	
	public GUITestState(GameState MainMenu, GameState playGameState, Sprite backdrop) {
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
