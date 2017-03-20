package teamd.cw1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

/**
 * Created by melaus on 20/02/2017.
 */
public class TouchSensors {

    /*
     * Initialisation
     */
    // Touch sensors and object to obtain results
    private EV3TouchSensor l_bumper_module;
    private EV3TouchSensor r_bumper_module;
    private SimpleTouch l_bumper;
    private SimpleTouch r_bumper;

    private boolean touchDetected = false;


    /*
     * CONSTRUCTOR
     */
    public TouchSensors(Port l_bumper_port, Port r_bumper_port) {
    	
        // touch sensors
        this.l_bumper_module = new EV3TouchSensor(l_bumper_port);
        this.r_bumper_module = new EV3TouchSensor(r_bumper_port);

        this.l_bumper = new SimpleTouch(this.l_bumper_module);
        this.r_bumper = new SimpleTouch(this.r_bumper_module);
    }


    public boolean isPressed() {
    	if (!touchDetected && (l_bumper.isPressed() || r_bumper.isPressed())) {
            touchDetected = true;
            return true;
        } else if (touchDetected && !(l_bumper.isPressed() || r_bumper.isPressed())) {
            touchDetected = false;
        }
    	
    	return false;
    }
}
