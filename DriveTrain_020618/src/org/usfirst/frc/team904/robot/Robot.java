/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team904.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private static final String kVisionAutoRed = "red";
	private static final String kVisionAutoBlue = "blue";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		for(WPI_TalonSRX motor : RobotMap.leftMotors)
		{
			motor.setNeutralMode(NeutralMode.Coast);
			motor.setInverted(false);
			motor.set(0);
		}
		for(WPI_TalonSRX motor : RobotMap.rightMotors)
		{
			motor.setNeutralMode(NeutralMode.Coast);
			motor.setInverted(true);
			motor.set(0);
		}

		RobotMap.climber.setNeutralMode(NeutralMode.Brake);
		RobotMap.climber.set(0);

		RobotMap.arms.setNeutralMode(NeutralMode.Brake);
		RobotMap.arms.set(0);
		
		RobotMap.shift.set(RobotMap.shiftLow);
		RobotMap.grabber.set(RobotMap.grabberClose);
		
		RobotMap.camera  = CameraServer.getInstance().startAutomaticCapture();
		RobotMap.camera.setResolution(640, 480);
		
		RobotMap.cvSink = CameraServer.getInstance().getVideo();
		RobotMap.outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
		
		RobotMap.cvSink.grabFrame(RobotMap.source);
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		RobotMap.leftMotors[0].configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				baseline();
				break;
			case kVisionAutoRed:
				if(isRed()) {
					//place cube on switch
				} else {
					//go and check scale
				}
				break;
			case kVisionAutoBlue:
				if(isBlue()) {
					//place cube on switch
				} else {
					//go and check scale
				}
				break;
			case kDefaultAuto:
			default:
				// Do Nothing
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		// Drivetrain
		///////////////////
		double[] xy = deadzone(
				RobotMap.driveStick.getRawAxis(RobotMap.driveStickTurnAxis),
				RobotMap.driveStick.getRawAxis(RobotMap.driveStickForwardAxis));
		drive(xy[0], xy[1]);
		
		// Accessory motors
		/////////////////////
		RobotMap.arms.set(deadzone(RobotMap.accessoryStick.getRawAxis(RobotMap.accessoryStickArmsAxis)));
		RobotMap.climber.set(deadzone(RobotMap.accessoryStick.getRawAxis(RobotMap.accessoryStickClimbAxis)));
		
		// Grabber
		////////////////////
		if(RobotMap.accessoryStick.getRawAxis(RobotMap.accessoryStickGrabberGrabTrigger) > 0.5)
		{
			RobotMap.grabber.set(RobotMap.grabberClose);
		}
		else if(RobotMap.accessoryStick.getRawAxis(RobotMap.accessoryStickGrabberReleaseTrigger) > 0.5)
		{
			RobotMap.grabber.set(RobotMap.grabberOpen);
		}
		else
		{
			RobotMap.grabber.set(DoubleSolenoid.Value.kOff);
		}
		
		
		// Gear shifting
		/////////////////////
		boolean triggerHighGear = RobotMap.driveStick.getTrigger();
		boolean buttonLowGear = RobotMap.driveStick.getTop();
		
		if (triggerHighGear) {
			RobotMap.shift.set(RobotMap.shiftHigh);
		} else if (buttonLowGear) {
			RobotMap.shift.set(RobotMap.shiftLow);
		} else {
			RobotMap.shift.set(DoubleSolenoid.Value.kOff);
		}
		
		
		// Camera Streaming
		/////////////////////
		streamCameraFeed();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	/**
	 * Autonomous methods
	 */
	public void baseline() {
		drive(0, 1);
		while(Math.abs(RobotMap.leftMotors[0].getSelectedSensorPosition(0))
				> RobotMap.baseline);
		drive(0, 0);
	}
	
	public int pixVal() {
		int color = 0;
		double[] temp = new double[5];
		
		RobotMap.cvSink.grabFrame(RobotMap.source);
		for(int j = 0; j < RobotMap.source.cols(); j++) {
			for(int i = 0; i < RobotMap.source.rows(); i++) {
				color += RobotMap.source.get(i, j, temp);
			}
		}
		
		return color;
	}
	
	public boolean isRed() {
		int color =  pixVal();
		
		if(color > RobotMap.redVal) {
			return true;
		}
		
		return false;
	}
	
	public boolean isBlue() {
		int color = pixVal();
		
		if(color < RobotMap.blueVal) {
			return true;
		}
		
		return false;
	}
	
	public void streamCameraFeed() {
		// stuff
	}
	
	/**
	 * Drive methods
	 */
	public double deadzone(double x) {
		if(x > 0.20)
			x = (x - 0.20) * 1.25;
		else if(x < -0.20)
			x = (x + 0.20) * 1.25;
		else
			x = 0;
		
		return x;
	}
	
	public double[] deadzone(double x, double y) {	
		return new double[] {deadzone(x), deadzone(y)};
	}
	
	public void drive(double turn, double forward) {
		double motorLeft = (forward + turn);
		double motorRight = (forward - turn);
		
		double scaleFactor;
		
		if ((Math.max(Math.abs(motorLeft), Math.abs(motorRight)) > 1)) {
			scaleFactor = Math.max(Math.abs(motorLeft), Math.abs(motorRight));
		} else {
			scaleFactor = 1;
		}
		
		motorLeft = motorLeft / scaleFactor;
		motorRight = motorRight / scaleFactor;
	
		for(WPI_TalonSRX motor : RobotMap.leftMotors)
		{
			motor.set(motorLeft);
		}
		for(WPI_TalonSRX motor : RobotMap.rightMotors)
		{
			motor.set(motorRight);
		}
	}
}
