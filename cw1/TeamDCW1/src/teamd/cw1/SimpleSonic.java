package teamd.cw1;

import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class SimpleSonic extends AbstractFilter{
	private float[] sample;
	private double minDistance;

	public SimpleSonic(SampleProvider source, double minDistance)  {
		super(source);
		sample = new float[sampleSize];
		this.minDistance = minDistance;
	}
	
	public boolean isObstacle() {
		super.fetchSample(sample, 0);
		return sample[0] < minDistance;
	}

	public double getDistance() {
	    return sample[0];
    }

}