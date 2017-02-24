package teamd.cw1;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.utility.Delay;


public class Motors {
    // Initialisation
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    // Initialisation
    private EV3LargeRegulatedMotor l_motor;
    private EV3LargeRegulatedMotor r_motor;
    private MotorThread motorThread;
    private int timeInterval;
    private final int defaultMotorSpeed;


    /*
     * Constructor
     */
    public Motors(Port l_motor_port, Port r_motor_port, int motorSpeed, int timeInterval) {
        //
        this.l_motor = new EV3LargeRegulatedMotor(l_motor_port);
        this.r_motor = new EV3LargeRegulatedMotor(r_motor_port);
        this.timeInterval = timeInterval;

        this.defaultMotorSpeed = motorSpeed;

        // start thread
        this.motorThread = new MotorThread();
        motorThread.start();


    }


    /*
     * FUNCTIONS
     */
    public void reverse() {
        l_motor.setSpeed(defaultMotorSpeed);
        r_motor.setSpeed(defaultMotorSpeed);
        l_motor.backward();
        r_motor.backward();
    }

    public void forward() {
        l_motor.setSpeed(defaultMotorSpeed);
        r_motor.setSpeed(defaultMotorSpeed);
        l_motor.forward();
        r_motor.forward();
    }

    public void stop() {
        l_motor.stop(true);
        r_motor.stop();
    }

    public void rotate(int angle, String direction) {

        System.out.println("     Rotating " + direction + " " + angle);

        // Translates desired rotation magnitude and direction of robot into wheel rotation
        angle *= direction.equals(LEFT) ? 1.8 : -1.8;
        l_motor.rotate(-angle, true);
        r_motor.rotate(angle);
    }

    public void smoothRotate(String direction, float sharpness) {

        System.out.println("  Smooth rotating " + direction + " " + sharpness);

        float speed = defaultMotorSpeed * sharpness;

        switch (direction) {
            case LEFT:
                l_motor.setSpeed(speed);
                r_motor.setSpeed(defaultMotorSpeed);
                break;
            case RIGHT:
                l_motor.setSpeed(defaultMotorSpeed);
                r_motor.setSpeed(speed);
        }
    }


    public void recoverFromObstacle(String direction) {
        // reverse and turn an angle
        this.reverse();
        Delay.msDelay(1000);
        this.rotate(45, direction);
        Delay.msDelay(1000);
    }


    public String getSpeed() {
        return " L: " + l_motor.getSpeed() + " R: " + r_motor.getSpeed();
    }

    /*
     * Thread for Motors
     */
    public class MotorThread extends Thread{
        public MotorThread() {
            // TODO: do something
        }

        public void run() {
            while (true) {
                // TODO: do something
            }
        }
    }

}