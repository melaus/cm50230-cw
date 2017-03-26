package teamd.cw1;

import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class SimpleSonic extends AbstractFilter {
	
	private float[] sample;

	public SimpleSonic(SampleProvider source)  {
		super(source);
		sample = new float[sampleSize];
	}

	public double getDistance() {
		super.fetchSample(sample, 0);
	    return sample[0];
    }

}