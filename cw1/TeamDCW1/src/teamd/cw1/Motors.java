package teamd.cw1;

import java.util.Date;

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
    
//    private ForwardThread thread;
//    private Date date;
//    private long smoothTurnStartTime = -1L;
//    private DirectionEnum smoothTurningDirection;

    /*
     * Constructor
     */
    public Motors(Port l_motor_port, Port r_motor_port, int motorSpeed, float motorAngleRatio, float smoothTurnSharpness) {
        
        this.l_motor = new EV3LargeRegulatedMotor(l_motor_port);
        this.r_motor = new EV3LargeRegulatedMotor(r_motor_port);
        
        this.defaultMotorSpeed = motorSpeed;   
        this.motorAngleRatio = motorAngleRatio;
        this.smoothTurnSharpness = smoothTurnSharpness;
        
//        this.thread = new ForwardThread();
//        this.thread.start();
    }

    /*
     * FUNCTIONS
     */
    public void forward() {
//    	thread.stopTimer();
    	
    	l_motor.setSpeed(defaultMotorSpeed);
    	r_motor.setSpeed(defaultMotorSpeed);
    	l_motor.forward();
    	r_motor.forward();
    }
    
    public void reverse() {
//    	thread.stopTimer();
    	
    	l_motor.setSpeed(defaultMotorSpeed);
    	r_motor.setSpeed(defaultMotorSpeed);
        l_motor.backward();
        r_motor.backward();
    }
    
    public void rotate(int angle, DirectionEnum direction) {
    	
    	System.out.println("ROTATING");
//    	thread.stopTimer();
    	
        // Translates desired rotation magnitude and direction of robot into wheel rotation
        angle *= direction.equals(DirectionEnum.LEFT) ? motorAngleRatio : -motorAngleRatio;
        l_motor.rotate(-angle, true);
        r_motor.rotate(angle);
    }

    public void smoothTurn(DirectionEnum direction) {
//    	    	
//    	if (smoothTurnStartTime < 0) {
//    		System.out.println("START SMOOTH TURN " + direction.toString());
//    		date = new Date();
//    		smoothTurnStartTime = date.getTime();
//    		smoothTurningDirection = direction;
//    	}
    	
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
    
//    public void smoothForward() {
//    	if (smoothTurnStartTime != -1L) {
//    		thread.startTimer((long)(smoothTurnStartTime / 2f));
//    		smoothTurn(opposite(smoothTurningDirection));
//    		System.out.println("SMOOTH FORWARD " + opposite(smoothTurningDirection).toString());
//    	} else {
//    		forward();
//    	}
//    }

    public void recoverFromObstacle(DirectionEnum direction) {
        // reverse and turn an angle
        this.reverse();
        Delay.msDelay(1000);
        this.rotate(45, direction);
    }
    
//    public class ForwardThread extends Thread {
//    	
//    	private long forwardTime = -1L;
//    	
//    	public void startTimer(long time) {
//    		smoothTurnStartTime = -1L;
//    		this.forwardTime = new Date().getTime() + time;
//    	}
//    	
//    	public void stopTimer() {
//    		smoothTurnStartTime = -1L;
//    		this.forwardTime = -1L;
//    	}
//    	
//        public void run() {
//            while (true) {
//                if (forwardTime != -1L) {
//                	long currentTime = new Date().getTime();
//                	
//                	if (currentTime >= forwardTime) {
//                		System.out.println("FORWARD TIMER EXPIRED");
//                		forwardTime = -1L;
//                		forward();
//                	}
//                }
//            }
//        }
//    }
    
    private DirectionEnum opposite(DirectionEnum direction) {
    	switch (direction) {
			case LEFT :
				return DirectionEnum.RIGHT;
			case RIGHT :
				return DirectionEnum.LEFT;
			case FRONT :
			default:
				return null;
		}
    }
}