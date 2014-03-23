package de.secondsystem.game01.impl.game.debug;

import org.jsfml.system.Clock;

public final class FrameClock {

    private Range time = new Range();
    private Range  freq = new Range();
    private SampleData sample;
    private final Clock clock = new Clock();
    
	public FrameClock() {
		this(100);
	}
	public FrameClock(int depth) {
		assert(depth >= 1);
		sample = new SampleData(depth);

        freq.minimum = Float.MAX_VALUE;
        time.minimum = Long.MAX_VALUE;
	}

    /**
     * Resets all times to zero and discards accumulated samples.
     */
	public void clear() {
       time = new Range();
       freq = new Range();
       sample = new SampleData(sample.data.length);
    }

    /**
     *  Begin frame timing.
     * Should be called once at the start of each new frame.
     */
	public void beginFrame() {
        clock.restart();
    }

    /** 
     * End frame timing.
     * Should be called once at the end of each frame.
     * Returns: Time elapsed since the matching FrameClock::beginFrame.
     */
	public long endFrame()
    {
        time.current = clock.getElapsedTime().asMilliseconds();

        sample.accumulator -= sample.data[sample.index];
        sample.data[sample.index] = (long) time.current;

        sample.accumulator += time.current;
        time.elapsed       += time.current;

        if (++sample.index >= sample.data.length)
        {
            sample.index = 0;
        }

        if (time.current != 0)
        {
            freq.current = 1.0f / (time.current/1000.f);
        }

        if (sample.accumulator != 0)
        {
            float smooth = sample.data.length;
            freq.average = smooth / (sample.accumulator/1000.f);
        }

        long smooth = sample.data.length;
        time.average = sample.accumulator / smooth;

        if (freq.current < freq.minimum)
            freq.minimum = freq.current;
        if (freq.current > freq.maximum)
            freq.maximum = freq.current;

        if (time.current < time.minimum)
            time.minimum = time.current;
        if (time.current > time.maximum)
            time.maximum = time.current;

        ++freq.elapsed;

        return (long) time.current;
    }

    /**
     * @return The number of frames to be sampled for averaging.
     */
	public int getSampleDepth() {
        return sample.data.length;
    }

    /**
     * @return The total accumulated frame time.
     */
	public float getTotalFrameTime() {
        return time.elapsed / 1000.f;
    }

    /**
     * @return The total accumulated number of frames.
     */
	public long getTotalFrameCount() {
        return freq.elapsed;
    }

    /**
     * @return Time elapsed during the last 'FrameClock::beginFrame/endFrame' pair.
     */
	public float getLastFrameTime() {
        return time.current;
    }

    /**
     * @return The shortest measured frame time.
     */
	public float getMinFrameTime() {
        return time.minimum;
    }

    /**
     * @return The longest measured frame time.
     */
	public float getMaxtFrameTime() {
        return time.maximum;
    }

    // Returns: Average frame time over the last getSampleDepth() frames.
	public float getAverageFrameTime() {
        return time.average;
    }

    // Returns: Frames per second, considering the pervious frame only.
	public float getFramesPerSecond() {
        return freq.current;
    }

    // Returns: The lowest measured frames per second.
	public float getMinFramesPerSecond() {
        return freq.minimum;
    }

    // Returns: The highest measured frames per second.
	public float getMaxFramesPerSecond() {
        return freq.maximum;
    }

    // Returns: Average frames per second over the last getSampleDepth() frames.
	public float getAverageFramesPerSecond() {
        return freq.average;
    }
}

class SampleData {
	float accumulator;
    long[] data;
    int index;
    
    public SampleData(int depth){
    	data = new long[depth];
    }
}

class Range {
	float minimum;
    float maximum;
    float average;
    float current;
    long elapsed;
}