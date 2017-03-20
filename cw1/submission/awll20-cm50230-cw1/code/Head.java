package teamd.cw1;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;
import teamd.cw1.enums.DirectionEnum;

/**
 * Created by melaus on 20/02/2017.
 */
public class Head {
	
	private final EV3UltrasonicSensor sonic_module;
	private final SimpleSonic sonic;
	private final EV3MediumRegulatedMotor motor;
	
	private final double minDistance;
	private final double maxDistance;
	private final double midDistance;
	private final double concaveWallThreshold;
	private final double convexWallThreshold;
	private DirectionEnum direction = DirectionEnum.FRONT;
	
    /*
     * CONSTRUCTOR
     */
    public Head(Port motor_port, Port sonic_port, double minDistance, double maxDistance,
    		float headSpeed, float concaveWallThreshold, float convexWallThreshold) {
    	
    	this.sonic_module = new EV3UltrasonicSensor(sonic_port);
    	this.sonic = new SimpleSonic(sonic_module.getDistanceMode());       
    	
    	this.motor = new EV3MediumRegulatedMotor(motor_port);
    	motor.setSpeed(headSpeed);

    	this.minDistance = minDistance;
    	this.maxDistance = maxDistance;
    	this.midDistance = (maxDistance + minDistance) / 2f;
    	
    	this.concaveWallThreshold = concaveWallThreshold;
    	this.convexWallThreshold = convexWallThreshold;
    }

    public DirectionEnum getDirection() {
    	return direction;
    }
    
    public double getDistance() {
    	return sonic.getDistance();
    }
    
    public boolean isTooClose(double distance) {
    	return distance < minDistance;
    }
    
    public boolean isTooFar(double distance) {
    	return distance > maxDistance;
    }
 
    public boolean isWithinConcaveWallThreshold(double distance) {
    	return distance <= concaveWallThreshold;
    }
    
    
    public boolean isWithinConvexWallThreshold(double distance) {
    	return distance <= convexWallThreshold;
    }
    
    public boolean isInRange(double distance) {
    	return distance >= minDistance && distance < maxDistance;
    }
    
    public void look(DirectionEnum direction) {
    	this.direction = direction;
    	switch (direction) {
    	case FRONT :
    		motor.rotateTo(0);
    		break;
    	case LEFT :
    		motor.rotateTo(-100);
    		break;
    	case RIGHT :
    		motor.rotateTo(100);
    		break;
    	}
    	
    	Delay.msDelay(250);
    }
}
