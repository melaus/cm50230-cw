package teamd.cw1;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import teamd.cw1.enums.DirectionEnum;


public class Motors {
	
    private final EV3LargeRegulatedMotor l_motor;
    private final EV3LargeRegulatedMotor r_motor;
    private final int defaultMotorSpeed;
    private final float motorAngleRatio;
    private final float smoothTurnSharpness;

    /*
     * Constructor
     */
    public Motors(Port l_motor_port, Port r_motor_port, int motorSpeed, float motorAngleRatio, float smoothTurnSharpness) {
        
        this.l_motor = new EV3LargeRegulatedMotor(l_motor_port);
        this.r_motor = new EV3LargeRegulatedMotor(r_motor_port);
        
        this.defaultMotorSpeed = motorSpeed;   
        this.motorAngleRatio = motorAngleRatio;
        this.smoothTurnSharpness = smoothTurnSharpness;
    }

    /*
     * FUNCTIONS
     */
    public void forward() {
    	
    	l_motor.setSpeed(defaultMotorSpeed);
    	r_motor.setSpeed(defaultMotorSpeed);
    	l_motor.forward();
    	r_motor.forward();
    }
    
    public void reverse() {
    	
    	l_motor.setSpeed(defaultMotorSpeed);
    	r_motor.setSpeed(defaultMotorSpeed);
        l_motor.backward();
        r_motor.backward();
    }
    
    public void rotate(int angle, DirectionEnum direction) {
    	
    	System.out.println("ROTATING " + angle + direction.toString());
    	
        // Translates desired rotation magnitude and direction of robot into wheel rotation
        angle *= direction.equals(DirectionEnum.LEFT) ? motorAngleRatio : -motorAngleRatio;
        l_motor.rotate(-angle, true);
        r_motor.rotate(angle);
    }

    public void smoothTurn(DirectionEnum direction) {
    	
    	System.out.println("SMOOTH TURNING: " + direction.toString());
    	
        float speed = defaultMotorSpeed * smoothTurnSharpness;

        switch (direction) {
            case LEFT:
                l_motor.setSpeed(speed);
                r_motor.setSpeed(defaultMotorSpeed);
                break;
            case RIGHT:
                l_motor.setSpeed(defaultMotorSpeed);
                r_motor.setSpeed(speed);
			case FRONT:
			default:
				break;
        }
    }

    public void recoverFromObstacle(DirectionEnum direction) {
    	
    	System.out.println("RECOVERING FROM OBSTACLE");
    	
        // reverse and turn an angle
        this.reverse();
        Delay.msDelay(1000);
        this.rotate(45, direction);
    }
}