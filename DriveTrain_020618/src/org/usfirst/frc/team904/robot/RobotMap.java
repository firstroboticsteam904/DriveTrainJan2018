package org.usfirst.frc.team904.robot;

import org.opencv.core.Mat;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

/**
 * TODO: On a press of a button make set of value's true to prepare for climbing.
 * Also, another button press to begin a set of climbing instructions.
 * @author DCube
 */

public class RobotMap {

	public static WPI_TalonSRX[] leftMotors = {new WPI_TalonSRX(2), new WPI_TalonSRX(3), new WPI_TalonSRX(4)};
	public static WPI_TalonSRX[] rightMotors = {new WPI_TalonSRX(5), new WPI_TalonSRX(6), new WPI_TalonSRX(7)};

	public static WPI_TalonSRX arms = new WPI_TalonSRX(8);
	public static WPI_TalonSRX climber = new WPI_TalonSRX(9);

	public static DoubleSolenoid shift = new DoubleSolenoid(0, 1);
	public static DoubleSolenoid.Value shiftLow = DoubleSolenoid.Value.kReverse;
	public static DoubleSolenoid.Value shiftHigh = DoubleSolenoid.Value.kForward;
	
	public static DoubleSolenoid grabber = new DoubleSolenoid(2, 3);
	public static DoubleSolenoid.Value grabberClose = DoubleSolenoid.Value.kForward;
	public static DoubleSolenoid.Value grabberOpen = DoubleSolenoid.Value.kReverse;
	
	public static Joystick driveStick = new Joystick(0);

	// channels for controls on the drive controller,
	// shown as axes on the driver station when looking at USB devices
	public static int driveStickForwardAxis = driveStick.getYChannel();
	public static int driveStickTurnAxis = driveStick.getZChannel();
	
	
	public static Joystick accessoryStick = new Joystick(1);
	
	// channels for controls on the accessory controller,
	// shown as axes on the driver station when looking at USB devices
	public static int accessoryStickArmsAxis = 5;
	public static int accessoryStickClimbAxis = 1;
	public static int accessoryStickGrabberGrabTrigger = 3;
	public static int accessoryStickGrabberReleaseTrigger = 2;

	// encoder values to disable high gear to resist tipping the robot
	public static final double elevation = 0.0; // climber elevation
	public static final double extend = 0.0; // arm extension
	
	// encoder values for auton
	public static final int baseline = 21200;
	
	// visual processing
	public static Mat source = new Mat();
	public static UsbCamera camera;
	public static CvSink cvSink;
	public static CvSource outputStream;
}
