package teamd.cw1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class Head {

    private static final String FRONT = "front";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    // Initialisation
    private EV3UltrasonicSensor sonic_module;
    private SimpleSonic sonic;

    private EV3MediumRegulatedMotor motor;

    private HeadThread headThread;

    private double lastDistance;

    private Date date = new Date();

    private List<DistanceTimestamp> distanceTimestamps = new ArrayList<DistanceTimestamp>();

    /*
     * CONSTRUCTOR
     */
    public Head(
            Port motor_port, Port sonic_port,
            double minDistance, double maxDistance) {
        this.sonic_module = new EV3UltrasonicSensor(sonic_port);
        this.sonic = new SimpleSonic(sonic_module.getDistanceMode(), minDistance, maxDistance);

        this.motor = new EV3MediumRegulatedMotor(motor_port);

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

                double distance = getDistance();
                double time = date.getTime();
                if (distance == lastDistance) return;
                lastDistance = distance;

                Collection<DistanceTimestamp> result = new ArrayList<DistanceTimestamp>();
                for (int i = 0; i < distanceTimestamps.size(); i++) {
                    DistanceTimestamp dt = distanceTimestamps.get(i);
                    if (time - dt.getTime() < 1000) {
                        result.add(dt);
                    }
                }

                distanceTimestamps = (List<DistanceTimestamp>) result;

                DistanceTimestamp dt = new DistanceTimestamp(date.getTime(), distance);
                distanceTimestamps.add(dt);

                double delta = distanceTimestamps.get(distanceTimestamps.size()).getDistance() - distanceTimestamps.get(0).getDistance();
                // If + moving away, if - moving towards
                System.out.println("Delta: " + delta);

                if (sonic.isTooClose()) {
                    Main.handleInput(ModuleEnum.HEAD, Main.TOO_CLOSE);
                } else if (sonic.isTooFar()) {
                    Main.handleInput(ModuleEnum.HEAD, Main.TOO_FAR);
                } else if (sonic.isInRange()) {
                    Main.handleInput(ModuleEnum.HEAD, Main.IN_RANGE);
                }
            }
        }
    }

    public class DistanceTimestamp {

        private double timestamp;
        private double distance;

        public DistanceTimestamp(double timestamp, double distance) {
            this.timestamp = timestamp;
            this.distance = distance;
        }

        public double getTime() {
            return timestamp;
        }

        public double getDistance() {
            return distance;
        }
    }

}
