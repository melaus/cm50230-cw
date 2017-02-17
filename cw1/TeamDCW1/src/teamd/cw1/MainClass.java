package teamd.cw1;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;
import lejos.robotics.TouchAdapter;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;

public class MainClass {
	
	/*
	 * Location of components
	 * Left motor:   D
	 * Right motor:  A
	 * Bumper right: 1
	 * Bumper left:  4
	 * Head:         C
	 * Head's motor: B
	 */
	
	public static void main(String[] args) {
//		Brick brick = BrickFinder.getDefault();
//		Port D = brick.getPort("D");
//		Port A = brick.getPort("A");
//		Port S4 = brick.getPort("S4");
//		Port S1 = brick.getPort("S1");
		
		
		EV3LargeRegulatedMotor l_motor = new EV3LargeRegulatedMotor(MotorPort.D);
		EV3LargeRegulatedMotor r_motor = new EV3LargeRegulatedMotor(MotorPort.A);
		
		EV3TouchSensor l_bumper = new EV3TouchSensor(SensorPort.S4);
		EV3TouchSensor r_bumper = new EV3TouchSensor(SensorPort.S1);
		
		SimpleTouch l_touch = new SimpleTouch(l_bumper);
		SimpleTouch r_touch = new SimpleTouch(r_bumper);
		
		
		while(!l_touch.isPressed() && !r_touch.isPressed()) {
			System.out.println("left:  " + (l_touch.isPressed() ? "true" : "false"));
			System.out.println("right: "+ (r_touch.isPressed() ? "true" : "false"));
			Delay.msDelay(1000);
		}
		
		LCD.drawString("WE ARE DONE!", 0, 4);
		Delay.msDelay(5000);
		
	}
}
