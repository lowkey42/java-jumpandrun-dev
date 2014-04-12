package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public class ThumbnailButton extends Element {
	
	public static final class ThumbnailData {
		public final ConstTexture texture;
		public final IntRect clip;
		public final String name;
		public ThumbnailData(String name, ConstTexture texture, IntRect clip) {
			this.name = name;
			this.texture = texture;
			this.clip = clip!=null ? clip : new IntRect(0,0, texture.getSize().x, texture.getSize().y);
		}
		public ThumbnailData(String name, ConstTexture texture) {
			this(name, texture, null);
		}
	}
	
	private static final int CAPTION_DISTANCE = 5;
	
	protected final Sprite sprite;
	
	protected final Text caption;
	
	protected final IOnClickListener clickListener;

	public ThumbnailButton(float x, float y, float width, float height, ThumbnailData thumbnailData,
			ElementContainer owner, IOnClickListener clickListener) {
		super(x, y, width, height, owner);

		this.clickListener = clickListener;
		
		this.caption = new Text(thumbnailData.name, getStyle().textFont, getStyle().textFontSize);
		FloatRect textRect = this.caption.getLocalBounds();
		while( textRect.width>=width ) {
			caption.setString(caption.getString().substring(0, caption.getString().length()-2));
			textRect = this.caption.getLocalBounds();
		}
		
		this.caption.setOrigin(textRect.width / 2.f, textRect.height );
		
		float captSpacing = CAPTION_DISTANCE + textRect.height*2;
		
		sprite = new Sprite(thumbnailData.texture, thumbnailData.clip);
		sprite.setOrigin(thumbnailData.clip.width/2.f, thumbnailData.clip.height/2.f);
		if( thumbnailData.clip.width-captSpacing >= thumbnailData.clip.height )
			sprite.setScale(width / thumbnailData.clip.width, width / thumbnailData.clip.width);
		else 
			sprite.setScale((height-captSpacing) / thumbnailData.clip.height, (height-captSpacing) / thumbnailData.clip.height);
	}

	@Override
	protected void drawImpl(RenderTarget rt) {
		sprite.setPosition(new Vector2f(getPosition().x+getWidth()/2, getPosition().y+getHeight()/2));
		caption.setPosition(new Vector2f(getPosition().x+getWidth()/2, getPosition().y+ getHeight()));
		
		rt.draw(sprite);
		rt.draw(caption);
	}

	@Override
	protected void onKeyReleased(KeyType type) {
		if( type==KeyType.ENTER && clickListener!=null )
			clickListener.onClick();
	}

	@Override
	public void update(long frameTimeMs) {
	}
	
}
