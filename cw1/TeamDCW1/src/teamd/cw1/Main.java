package teamd.cw1;

import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

/*
 * Location of components
 * Left motor:   D
 * Right motor:  A
 * Bumper right: 1
 * Bumper left:  4
 * Head:         C
 * Head's motor: B
 */

public class Main {

    // Public constants
    public static final String DISTANCE_TRAVELLED = "distance_travelled";
    public static final String TOUCH_CONTACT = "touch_contact";
    public static final String ULTRASONIC_TOO_CLOSE = "ultrasonic_too_close";
    public static final String ULTRASONIC_TOO_FAR = "ultrasonic_too_far";

    // Private constants
    private static final String FORWARD = "forward";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    private static final String FOLLOWING_WALL = "following_wall";
    private static final String FINDING_WALL = "finding_wall";

    private static final int RIGHT_ANGLE = 90;
    private static final int MOTOR_SPEED = 50;
    private static final int TIME_INTERVAL = 2500;
    private static final float MIN_DISTANCE = 0.3f;
    private static final float MAX_DISTANCE = 0.5f;

    // Private variables
    private static Motors motors;
    private static TouchSensors touch;
    private static Head head;
    private static MainThread mainThread;

    private static String status = FINDING_WALL;
    private static String wallSide;

    private static float midDistance = (MAX_DISTANCE + MIN_DISTANCE) / 2f;

    public static void main(String[] args){
        motors = new Motors(MotorPort.D, MotorPort.A, 1000);
        touch = new TouchSensors(SensorPort.S4, SensorPort.S1);
        head = new Head(MotorPort.B, SensorPort.S2, MIN_DISTANCE, MAX_DISTANCE);
        mainThread = new MainThread();
        mainThread.start();

//        motors.forward();
        head.look(45, LEFT);
        head.look(45, LEFT);
        head.look(45, "");

    }

    public static void handleInput(DeviceTypeEnum deviceType, String command) {
        switch(deviceType) {
            case MOTOR :
                break;
            case TOUCH :
                handleTouchSensor(command);
                break;
            case ULTRASONIC :
                handleUltrasonicSensor(command);
                break;
        }
    }

    private static void handleMotor(String command) {

    }

    private static void handleTouchSensor(String command) {
        switch (command) {
            case TOUCH_CONTACT :
                motors.recoverFromObstacle(LEFT);
                motors.forward();
        }
    }

    private static void handleUltrasonicSensor(String command) {
        switch(command) {
            case ULTRASONIC_TOO_CLOSE :
                if (status.equals(FINDING_WALL)) {
                    status = FOLLOWING_WALL;
                    wallSide = RIGHT;
                    motors.rotate(RIGHT_ANGLE, opposite(wallSide));
                    head.look(45, wallSide);
                    motors.forward();
                } else if (status.equals(FOLLOWING_WALL)) {
                    //int correctionDistance = MIN_DISTANCE - head.getDistance();
                }
        }
    }

    private static String opposite(String direction) {
        return direction.equals(LEFT) ? RIGHT : LEFT;
    }

    public static class MainThread extends Thread {
        public MainThread() {

        }

        public void run() {
            boolean alive = true;
            while (alive) {
                if ((Button.waitForAnyPress() & Button.ID_ESCAPE) != 0) {
                    alive = false;
                    System.exit(1);
                }
            }
        }
    }

}
