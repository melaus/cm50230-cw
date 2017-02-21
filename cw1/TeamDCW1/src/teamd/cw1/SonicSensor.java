package teamd.cw1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class SonicSensor {
    // Initialisation
    Port sonic_port;
    EV3UltrasonicSensor sonic_module;
    SimpleSonic sonic;
    SonicThread sonicThread;
    double minDistance;
    private boolean isTooClose = false;

    // CONSTRUCTOR
    public SonicSensor(
            Port sonic_port,
            double minDistance) {
        this.sonic_port = sonic_port;
        this.sonic_module = new EV3UltrasonicSensor(sonic_port);
        this.sonic = new SimpleSonic(sonic_module.getDistanceMode(), minDistance);
        this.minDistance = minDistance;

        // Thread
        this.sonicThread = new SonicThread();
        this.sonicThread.start();
    }


    public class SonicThread extends Thread {
        public SonicThread() {
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
