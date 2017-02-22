package teamd.cw1;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class Head {
    // Initialisation
    Port sonic_port;
    EV3UltrasonicSensor sonic_module;
    SimpleSonic sonic;

    Port motor_port;
    EV3MediumRegulatedMotor motor;

    HeadThread headThread;


    double minDistance;
    double maxDistance;
    private boolean isTooClose = false;

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String FRONT = "front";


    /*
     * CONSTRUCTOR
     */
    public Head(
            Port motor_port, Port sonic_port,
            double minDistance, double maxDistance) {
        this.sonic_port = sonic_port;
        this.sonic_module = new EV3UltrasonicSensor(sonic_port);
        this.sonic = new SimpleSonic(sonic_module.getDistanceMode(), minDistance);

        this.motor = new EV3MediumRegulatedMotor(motor_port);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;

        // Thread
        this.headThread = new HeadThread();
        this.headThread.start();
    }


    /*
     * FUNCTIONS
     */
    public void look(int angle, String direction) {
        // get angle to look at based on desired direction
        angle *= direction.equals(LEFT) ? -2 : (direction.equals(RIGHT) ? 2 : 0);

        if (angle != 0) {
            motor.rotate(angle);
        } else {
            motor.rotateTo(0);
        }
    }

    public double getDistance() {
        return sonic.getDistance();
    }


    /*
     * THREAD
     */
    public class HeadThread extends Thread {
        public HeadThread() {
            // TODO: do something
        }

        public void run() {
            while (true) {
                if (!isTooClose & sonic.isObstacle()) {
                    isTooClose = true;
                    // TODO:  Main.handleInput();
                } else if (isTooClose & !sonic.isObstacle()) {
                    isTooClose = false;
                }
            }
        }
    }

}
