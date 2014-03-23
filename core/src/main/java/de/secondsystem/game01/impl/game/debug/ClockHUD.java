package de.secondsystem.game01.impl.game.debug;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;

import de.secondsystem.game01.impl.ResourceManager;

public final class ClockHUD implements Drawable {

	private final FrameClock clock;
	
	private final ConstFont font;

	public ClockHUD(FrameClock clock) throws IOException {
		this(clock, ResourceManager.font.get("FreeMono.otf"));
	}
	public ClockHUD(FrameClock clock, ConstFont font) {
		this.clock = clock;
		this.font = font;
	}

	@Override
	public void draw(RenderTarget target, RenderStates states) {
		// Gather the available frame time statistics.
        Stat[] stats = build();

        Text elem = new Text();
        elem.setFont(font);
        elem.setCharacterSize(20);
        elem.setPosition(5.0f, 5.0f);

        // Draw the available frame time statistics.
        for(Stat stat : stats) {
            elem.setString(stat.str);
            elem.setColor(stat.color);

            target.draw(elem, states);

            // Next line.
            elem.move(0.0f, 20.0f);
        }
	}

    static String format(String name, String resolution, float value) {
    	StringBuilder str = new StringBuilder(name);
    	while(str.length()<5)
    		str.append(' ');
    	
    	str.append(" : ");
    	
    	String valStr = String.format("%.2f", value);
    	for(int i=valStr.length(); i<7; ++i)
    		str.append(' ');
    	
    	str.append(valStr).append(" ").append(resolution);
    	
        return str.toString();
    }

    private Stat[] build() {
    	return new Stat[]{
    			new Stat(Color.YELLOW, 	format("Time",  "(sec)", 	clock.getTotalFrameTime())),
    			new Stat(Color.WHITE, 	format("Frame", "", 		clock.getTotalFrameCount())),
    			new Stat(Color.GREEN, 	format("FPS",  	"", 		clock.getFramesPerSecond())),
    			new Stat(Color.GREEN, 	format("min.",  "", 		clock.getMinFramesPerSecond())),
    			new Stat(Color.GREEN, 	format("avg.",  "", 		clock.getAverageFramesPerSecond())),
    			new Stat(Color.GREEN, 	format("max.",  "", 		clock.getMaxFramesPerSecond())),
    			new Stat(Color.CYAN, 	format("Delta", "(ms)", 	clock.getLastFrameTime())),
    			new Stat(Color.CYAN, 	format("min.",  "(ms)", 	clock.getMinFrameTime())),
    			new Stat(Color.CYAN, 	format("avg.",  "(ms)", 	clock.getAverageFrameTime())),
    			new Stat(Color.CYAN, 	format("max.",  "(ms)", 	clock.getMaxtFrameTime())),
    	};
    }
}

class Stat {
	Color color;
	String str;
	public Stat(Color color, String str) {
		this.color = color;
		this.str = str;
	}
}