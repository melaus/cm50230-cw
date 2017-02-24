package teamd.cw1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class TouchSensors {

    private boolean touchDetected = false;


    /*
     * Initialisation
     */
    // Touch sensors and object to obtain results
    EV3TouchSensor l_bumper_module;
    EV3TouchSensor r_bumper_module;
    SimpleTouch l_bumper;
    SimpleTouch r_bumper;

    TouchThread touchThread;


    /*
     * CONSTRUCTOR
     */
    public TouchSensors(
            Port l_bumper_port,
            Port r_bumper_port) {

        // touch sensors
        this.l_bumper_module = new EV3TouchSensor(l_bumper_port);
        this.r_bumper_module = new EV3TouchSensor(r_bumper_port);

        this.l_bumper = new SimpleTouch(this.l_bumper_module);
        this.r_bumper = new SimpleTouch(this.r_bumper_module);

        // Thread
        this.touchThread = new TouchThread();
        this.touchThread.start();
    }


    /*
     * THREAD
     * to detect touch sensor activity and perform actions
     */
    public class TouchThread extends Thread {
        public TouchThread() {
            // TODO: do something
        }

        public void run() {
            while (true) {
                // check if bumper is pressed
                if (!touchDetected && (l_bumper.isPressed() || r_bumper.isPressed())) {
                    touchDetected = true;
                    Main.handleInput(ModuleEnum.TOUCH, Main.TOUCH_CONTACT);
                } else if (touchDetected && !(l_bumper.isPressed() || r_bumper.isPressed())) {
                    touchDetected = false;
                }
            }
        }
    }
}
