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

	// Test parameters
	private static final int[] DEFAULT_MOTOR_SPEEDS = {200, 400, 600};
	private static final float[] DEFAULT_MIN_WALL_FOLLOW_DISTANCES = {0.2f, 0.3f, 0.4f};

    // Private constants
	private static final int DEFAULT_MOTOR_SPEED = DEFAULT_MOTOR_SPEEDS[1];
	private static final float MIN_WALL_FOLLOW_DISTANCE = DEFAULT_MIN_WALL_FOLLOW_DISTANCES[1];
	private static final float MAX_WALL_FOLLOW_DISTANCE = MIN_WALL_FOLLOW_DISTANCE + 0.05f;
	private static final float CONCAVE_WALL_THRESHOLD = MIN_WALL_FOLLOW_DISTANCE + 0.1f;
	private static final float CONVEX_WALL_THRESHOLD = MIN_WALL_FOLLOW_DISTANCE + 0.3f;
	
	private static final int HEAD_SPEED = 1200;
	private static final int HEAD_ROTATION_TIME_INTERVAL = 500;
	private static final float MOTOR_ANGLE_RATIO = 2.10f;
	private static final float SMOOTH_TURN_SHARPNESS = 0.95f;
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
    		head = new Head(MotorPort.B, SensorPort.S3,
    				MIN_WALL_FOLLOW_DISTANCE,
    				MAX_WALL_FOLLOW_DISTANCE,
    				HEAD_SPEED,
    				CONCAVE_WALL_THRESHOLD,
    				CONVEX_WALL_THRESHOLD);
	        motors = new Motors(MotorPort.C, MotorPort.A,
	        		DEFAULT_MOTOR_SPEED,
	        		MOTOR_ANGLE_RATIO,
	        		SMOOTH_TURN_SHARPNESS);
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
		    handleInput(CommandEnum.DISTANCE, distance);
            
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
    	Log(command.toString(), String.valueOf(distance));

    	switch (command) {
	    	case DISTANCE:
	    		if (head.isWithinConcaveWallThreshold(distance)) {
	    			status = StatusEnum.FOLLOWING_WALL;
	    			System.out.println("FOUND WALL: " + distance);
	    			motors.rotate(RIGHT_ANGLE, opposite(wallSide));
	    			head.look(wallSide);
	    			motors.forward();
	    		}
	    		break;

			case TOUCH_CONTACT:
				handleTouchContact();
				break;
			case INTERVAL_REACHED:
			default:
				break;
    	}
    }   
    
    private void followingWall(CommandEnum command, double distance) {
    	Log(command.toString(), String.valueOf(distance));

    	switch (command) {
	    	case DISTANCE:
	    		if (head.isTooClose(distance)) {
	    			motors.smoothTurn(opposite(wallSide));  
	    			justTurnedConvexCorner = false;
	    		} else if (head.isTooFar(distance) && head.isWithinConvexWallThreshold(distance)) {
					motors.smoothTurn(wallSide); 
					justTurnedConvexCorner = false;
	    		} else if (!head.isWithinConvexWallThreshold(distance)) {
	    			if (justTurnedConvexCorner) return;
		    		Delay.msDelay(500);
		    		if (head.isWithinConvexWallThreshold(head.getDistance())) return;
		    		
		    		System.out.println("FOUND CONVEX CORNER: " + distance);
		    		motors.rotate(RIGHT_ANGLE, wallSide);
		    		justTurnedConvexCorner = true;
		    		motors.forward();
	    		} else if (head.isInRange(distance)) {
	    			motors.forward();
	    			justTurnedConvexCorner = false;
	    		}
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
    	Log(command.toString(), String.valueOf(distance));
    	switch (command) {
    		case DISTANCE:
    			if (head.isWithinConcaveWallThreshold(distance)) {
    				System.out.println("FOUND CONCAVE CORNER: " + distance);
    				motors.rotate(RIGHT_ANGLE, opposite(wallSide));
    				status = StatusEnum.FOLLOWING_WALL;
	    			justTurnedConvexCorner = false;
    				motors.forward();
    			}
    			break;
			case INTERVAL_REACHED:
				head.look(wallSide);
				System.out.println("LOOKING AT WALL");
				status = StatusEnum.FOLLOWING_WALL;
				break;
			case TOUCH_CONTACT:
				handleTouchContact();
				break;
			default:
				break;
    	}
    }
    
    private void handleTouchContact() {
    	motors.recoverFromObstacle(DirectionEnum.LEFT);
    	motors.forward();
    }

    public void Log(String command, String data) {
    	String logString = "Status: " + status.toString()
    			+ " Command: " + command
    			+ " Data: " + data;
    	System.out.println(logString);
    }

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
