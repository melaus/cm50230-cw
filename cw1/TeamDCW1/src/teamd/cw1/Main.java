package teamd.cw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class Main {

    // Public constants
    public static final String INTERVAL_REACHED = "interval_reached";
    public static final String TOUCH_CONTACT = "touch_contact";
    public static final String TOO_CLOSE = "too_close";
    public static final String TOO_FAR = "too_far";
    public static final String IN_RANGE = "in_range";

    // Private constants
    private static final String FORWARD = "forward";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    private static final int RIGHT_ANGLE = 90;
    private static final int DEFAULT_MOTOR_SPEED = 360;
    private static final int MOTOR_TIME_INTERVAL = 2500;
    private static final float MIN_DISTANCE = 0.2f;
    private static final float MAX_DISTANCE = 0.2f;
    private static final int DEFAULT_HEAD_ANGLE = 45;
    private static final float WALL_THRESHOLD = 0.25f;


    // Private variables
    private static Motors motors;
    private static TouchSensors touch;
    private static Head head;
    private static ExitThread exitThread;

    private static StatusEnum status = StatusEnum.FINDING_WALL;
    private static String wallSide;

    private static float midDistance = (MAX_DISTANCE + MIN_DISTANCE) / 2f;

    private static PrintStream printStreamToFile;

    public static void main(String[] args) throws FileNotFoundException {
        motors = new Motors(MotorPort.D, MotorPort.A, DEFAULT_MOTOR_SPEED, 1000);
        touch = new TouchSensors(SensorPort.S4, SensorPort.S1);
        head = new Head(MotorPort.B, SensorPort.S2, MIN_DISTANCE, MAX_DISTANCE);

        exitThread = new ExitThread();
        exitThread.start();

        File file = new File("logging.txt");
        printStreamToFile = new PrintStream(file);
        System.setOut(printStreamToFile);


        motors.forward();

    }

    public static synchronized void handleInput(ModuleEnum module, String command) {

        Log(module.toString(), command, String.valueOf("D: " + head.getDistance()));

        switch(module) {
            case HEAD :
                handleHead(command);
                break;
            case MOTOR :
                break;
            case TOUCH :
                handleTouchSensor(command);
                break;
        }

    }

    private static void handleHead(String command) {
        switch(command) {
            case TOO_CLOSE :
                tooClose(command);
                break;
            case TOO_FAR :
                tooFar(command);
                break;
            case IN_RANGE :
                inRange(command);
                break;
        }
    }

    private static void tooClose(String command) {
        switch (status) {
            case FINDING_WALL :

                status = StatusEnum.FOLLOWING_WALL;
                wallSide = RIGHT;
                motors.rotate(RIGHT_ANGLE, opposite(wallSide));
                head.look(DEFAULT_HEAD_ANGLE, wallSide);
                motors.forward();
                break;

            case FOLLOWING_WALL :

                double correctionDistance = Math.abs(midDistance - head.getDistance());
                double turnSharpness = 1 - Math.max(0, Math.min(1, correctionDistance));
                motors.smoothRotate(opposite(wallSide), (float)turnSharpness);
                break;

            case TURNING_CORNER :

                status = StatusEnum.FOLLOWING_WALL;
                break;

        }
    }

    private static void tooFar(String command) {
        switch(status) {
            case FINDING_WALL :
                break;
            case FOLLOWING_WALL :

                double correctionDistance = Math.abs(midDistance - head.getDistance());

                if (correctionDistance > WALL_THRESHOLD) {
                    Delay.msDelay(1000);
                    status = StatusEnum.TURNING_CORNER;
                    motors.rotate(RIGHT_ANGLE, wallSide);
                    motors.forward();
                    Delay.msDelay(1000);
                } else {
                    double turnSharpness = 1 - Math.max(0, Math.min(1, correctionDistance));
                    motors.smoothRotate(wallSide, (float) turnSharpness);
                }
                break;

            case TURNING_CORNER :
                break;
        }
    }

    private static void inRange(String command) {
        switch(status) {
            case FINDING_WALL :
                break;
            case FOLLOWING_WALL :
                motors.forward();
                break;
            case TURNING_CORNER :
                status = StatusEnum.FOLLOWING_WALL;
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

    private static void Log(String module, String command, String data) {
        String log = "Status: " + status.toString() + " Module: " + module + " Command: " + command + " Data: " + data + "?";
//    	System.out.println(log);
    }

    private static String opposite(String direction) {
        return direction.equals(LEFT) ? RIGHT : LEFT;
    }

    public static class ExitThread extends Thread {
        public ExitThread() {

        }

        public void run() {
            boolean alive = true;
            while (alive) {
                if ((Button.waitForAnyPress() & Button.ID_ESCAPE) != 0) {
                    alive = false;
                    printStreamToFile.close();
                    head.look(DEFAULT_HEAD_ANGLE, ""); // reset head
                    System.exit(1);
                }
            }
        }
    }

}
