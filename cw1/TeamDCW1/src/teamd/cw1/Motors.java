package teamd.cw1;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

/**
 * Created by melaus on 20/02/2017.
 */
public class Motors {
    // Initialisation
    private Port l_motor_port;
    private Port r_motor_port;
    private EV3LargeRegulatedMotor l_motor;
    private EV3LargeRegulatedMotor r_motor;
    private int previousRecoverDirection = 1;  // 1 means right, 0 means left
    MotorThread motorThread;
    int distanceTravelled;


    // Constructor
    public Motors(Port l_motor_port, Port r_motor_port, int distanceTravelled) {
        //
        this.l_motor_port = l_motor_port;
        this.r_motor_port = r_motor_port;
        this.l_motor = new EV3LargeRegulatedMotor(l_motor_port);
        this.r_motor = new EV3LargeRegulatedMotor(r_motor_port);
        this.distanceTravelled = distanceTravelled;

        // start thread
        this.motorThread =  new MotorThread();
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

    public void rotate(int angle, int direction) {
        switch (direction) {
            default:
                System.out.println("invalid direction!");

            case 1: // right
                l_motor.rotate(angle, true);
                r_motor.rotate(-angle);
                break;

            case 0: // left
                l_motor.rotate(-angle, true);
                r_motor.rotate(angle);
                break;
        }
    }

    public void recoverFromObstacle() {
        // reverse and turn an angle
        this.reverse();
        Delay.msDelay(500);
        this.rotate(10,1); // TODO: somehow set direction
    }


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