package teamd.cw1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class TouchSensors {
    // Initialisation
    Port l_bumper_port;
    Port r_bumper_port;
    EV3TouchSensor l_bumper_module;
    EV3TouchSensor r_bumper_module;
    SimpleTouch l_bumper;
    SimpleTouch r_bumper;

    Motors motors;

    TouchThread touchThread;

    // CONSTRUCTOR
    public TouchSensors(
            Port l_bumper_port,
            Port r_bumper_port,
            Motors motors) {

        // touch sensors
        this.l_bumper_port = l_bumper_port;
        this.r_bumper_port = r_bumper_port;
        this.l_bumper_module = new EV3TouchSensor(l_bumper_port);
        this.r_bumper_module = new EV3TouchSensor(r_bumper_port);

        this.l_bumper = new SimpleTouch(this.l_bumper);
        this.r_bumper = new SimpleTouch(this.r_bumper);

        // motors
        this.motors = motors;

        // Thread
        this.touchThread = new TouchThread();
        this.touchThread.start();
    }


    public class TouchThread extends Thread {
        public TouchThread() {
            // TODO: do something
        }

        public void run() {
            while (true) {
                // check if bumper is pressed
                if (l_bumper.isPressed() || r_bumper.isPressed()) {
                    motors.stop();
                    motors.recoverFromObstacle(); // TODO: needs work
                    // MainClass.handleInput();
                }
            }
        }
    }
}
