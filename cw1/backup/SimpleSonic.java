package teamd.cw1;

import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class SimpleSonic extends AbstractFilter {

    private float[] sample;
    private double minDistance;
    private double maxDistance;

    public SimpleSonic(SampleProvider source, double minDistance, double maxDistance)  {
        super(source);
        sample = new float[sampleSize];
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public boolean isTooClose() {
        super.fetchSample(sample, 0);
        return sample[0] < minDistance;
    }

    public boolean isTooFar() {
        super.fetchSample(sample, 0);
        return sample[0] > maxDistance;
    }

    public boolean isInRange() {
        super.fetchSample(sample, 0);
        return sample[0] >= minDistance && sample[0] <= maxDistance;
    }

    public double getDistance() {
        return sample[0];
    }

}