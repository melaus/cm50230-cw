package teamd.cw1;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.utility.Delay;


public class Motors {
    // Initialisation
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final int DEFAULT_MOTOR_SPEED = 50;

    // Initialisation
    private EV3LargeRegulatedMotor l_motor;
    private EV3LargeRegulatedMotor r_motor;
    private MotorThread motorThread;
    private int timeInterval;


    /*
     * Constructor
     */
    public Motors(Port l_motor_port, Port r_motor_port, int timeInterval) {
        //
        this.l_motor = new EV3LargeRegulatedMotor(l_motor_port);
        this.r_motor = new EV3LargeRegulatedMotor(r_motor_port);
        this.timeInterval = timeInterval;

        // start thread
        this.motorThread = new MotorThread();
        motorThread.start();
    }


    /*
     * FUNCTIONS
     */
    public void reverse() {
        l_motor.backward();
        r_motor.backward();
    }

    public void forward() {
        l_motor.forward();
        r_motor.forward();
    }

    public void stop() {
        l_motor.stop(true);
        r_motor.stop();
    }

    public void rotate(int angle, String direction) {
        // Translates desired rotation magnitude and direction of robot into wheel rotation
        angle *= direction.equals(LEFT) ? 2 : -2;
        l_motor.rotate(-angle, true);
        r_motor.rotate(angle);
    }

    public void smoothRotate(String direction, float sharpness, boolean isRotate) {
        float speed = isRotate ? DEFAULT_MOTOR_SPEED * sharpness : DEFAULT_MOTOR_SPEED;

        switch (direction) {
            case LEFT:
                l_motor.setSpeed(speed);
                break;
            case RIGHT:
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