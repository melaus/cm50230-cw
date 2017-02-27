package teamd.cw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import teamd.cw1.enums.CommandEnum;
import teamd.cw1.enums.DirectionEnum;
import teamd.cw1.enums.StatusEnum;

public class Robot {

    // Private constants
	private static final float MIN_WALL_DISTANCE = 0.25f;
	private static final float MAX_WALL_DISTANCE = 0.3f;
	private static final float WALL_NOT_FOUND_THRESHOLD = 0.5f;
	
	private static final int HEAD_SPEED = 1200;
	private static final int HEAD_ROTATION_TIME_INTERVAL = 1000;
	private static final int DEFAULT_MOTOR_SPEED = 200;
	private static final float MOTOR_ANGLE_RATIO = 2.05f;
	private static final float SMOOTH_TURN_SHARPNESS = 0.9f;
    private static final int RIGHT_ANGLE = 90;
    
    private static final DirectionEnum wallSide = DirectionEnum.RIGHT;
    
    private final ExitThread exitThread;
    private final PrintStream printStreamToFile;

    // Private variables
    private Head head;
    private Motors motors;
    private TouchSensors touch;

    private StatusEnum status;
    private boolean justTurnedConvexCorner = false;
	private long lastTime = 0;
    
    public Robot() throws FileNotFoundException {
    	File file = new File("logging.txt");        
    	printStreamToFile = new PrintStream(file);
    	System.setOut(printStreamToFile);

    	try {
    		head = new Head(MotorPort.B, SensorPort.S3, MIN_WALL_DISTANCE, MAX_WALL_DISTANCE, HEAD_SPEED, WALL_NOT_FOUND_THRESHOLD);
	        motors = new Motors(MotorPort.C, MotorPort.A, DEFAULT_MOTOR_SPEED, MOTOR_ANGLE_RATIO, SMOOTH_TURN_SHARPNESS);
	        touch = new TouchSensors(SensorPort.S1, SensorPort.S2);
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            System.exit(1);
    	}
        
        exitThread = new ExitThread();
        exitThread.start();
              
        startRobot();
    }
    
    private void startRobot() {
    	
    	status = StatusEnum.FINDING_WALL;
    	motors.forward();
    	    	
	    while (true) {  

	    	// Distance
	    	double distance = head.getDistance();
	    	System.out.println(distance);
	    	
	    	if (distance >= 0) {
		        if (head.isTooClose(distance)) {
		            handleInput(CommandEnum.TOO_CLOSE, distance);
		        } else if (head.isTooFar(distance)) {
		        	handleInput(CommandEnum.TOO_FAR, distance);
		        } else if (head.isInRangeClose(distance)) {
		        	handleInput(CommandEnum.IN_RANGE_CLOSE, distance);
		        } else if (head.isInRangeFar(distance)) {
		        	handleInput(CommandEnum.IN_RANGE_FAR, distance);
		        }
	    	}
            
            // Head movement
            Date date = new Date();
            long time = date.getTime();
            if (time - lastTime > HEAD_ROTATION_TIME_INTERVAL) {
            	handleInput(CommandEnum.INTERVAL_REACHED);
            	lastTime = time;
            }
            
            // Touch
            if (touch.isPressed()) {
            	handleInput(CommandEnum.TOUCH_CONTACT);
            }
	    }
    }

    public void handleInput(CommandEnum command) { 
    	handleInput(command, 0);
    }
    
    public void handleInput(CommandEnum command, double distance) {    		
    	switch(status) {
	    	case FINDING_WALL:
	    		findingWall(command, distance);
	    		break;
	    	case FOLLOWING_WALL:		
	    		followingWall(command, distance);	
	    		break;	
	    	case LOOKING_FORWARD:
	    		lookingForward(command, distance);
	    		break;
			default:
				break;
    	}
    }
    
    private void findingWall(CommandEnum command, double distance) {
    	switch (command) {
    		case TOO_CLOSE:
    		case IN_RANGE_CLOSE:
    			status = StatusEnum.FOLLOWING_WALL;
    	    	System.out.println("FOUND WALL: " + distance);
    			motors.rotate(RIGHT_ANGLE, opposite(wallSide));
				head.look(wallSide);
    			motors.forward();
    			break;
			case TOUCH_CONTACT:
				handleTouchContact();
				break;
			case TOO_FAR:
			case IN_RANGE_FAR:
			case INTERVAL_REACHED:
			default:
				break;
    	}
    }   
    
    private void followingWall(CommandEnum command, double distance) {
    	switch (command) {
    		case TOO_CLOSE :
    			motors.smoothTurn(opposite(wallSide));  
    			justTurnedConvexCorner = false;
    			break;
    			
    		case TOO_FAR :
    			if (justTurnedConvexCorner) return;
    			
				if (head.isPastWallThreshold(distance)) {
					Delay.msDelay(500);
					if (head.getDistance() < WALL_NOT_FOUND_THRESHOLD) return;
					System.out.println("FOUND CONVEX CORNER: " + distance);
					motors.rotate(RIGHT_ANGLE, wallSide);
					justTurnedConvexCorner = true;
					motors.forward();
				} else {
					motors.smoothTurn(wallSide); 
				}
		    	break;
		    	
    		case IN_RANGE_CLOSE:
    		case IN_RANGE_FAR:
    			motors.forward();
//    			motors.smoothForward();
    			justTurnedConvexCorner = false;
		    	break;
		    	
			case INTERVAL_REACHED:
				head.look(DirectionEnum.FRONT);
				System.out.println("LOOKING FORWARDS");
				status = StatusEnum.LOOKING_FORWARD;
				break;
				
			case TOUCH_CONTACT:
				handleTouchContact();
				break;
				
			default:
				break;
    	}
    }
    
    private void lookingForward(CommandEnum command, double distance) {
    	switch (command) {
    		case TOO_CLOSE:
			case IN_RANGE_CLOSE :
				System.out.println("FOUND CONCAVE CORNER: " + distance);
    			motors.rotate(RIGHT_ANGLE, opposite(wallSide));
    			status = StatusEnum.FOLLOWING_WALL;
    			motors.forward();
    			break;
			case INTERVAL_REACHED:
				head.look(wallSide);
				System.out.println("LOOKING AT WALL");
				status = StatusEnum.FOLLOWING_WALL;
				break;
			case TOUCH_CONTACT:
				handleTouchContact();
				break;
			case TOO_FAR :
			case IN_RANGE_FAR :
			default:
				break;
    	}
    }
    
    private void handleTouchContact() {
    	motors.recoverFromObstacle(DirectionEnum.LEFT);
    	motors.forward();
    }

//    public void Log(String command, String data) {
//    	String logString = "Status: " + status.toString()
//    			+ " Command: " + command
//    			+ " Data: " + data;
//    	System.out.println(logString);
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
    
    public class ExitThread extends Thread {
        public void run() {
            boolean alive = true;
            while (alive) {
                if ((Button.waitForAnyPress() & Button.ID_ESCAPE) != 0) {
                    alive = false;
                    printStreamToFile.close();
                    head.look(DirectionEnum.FRONT);
                    System.exit(1);
                }
            }
        }
    }

}
