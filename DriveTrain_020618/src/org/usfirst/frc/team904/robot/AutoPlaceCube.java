package org.usfirst.frc.team904.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPlaceCube {
	
	static double config_climberRaiseTime = 0.5;
	static double config_grabberReleaseTime = 0.1;
	
	static double config_armRaiseSpeed = 0.2;
	static double config_climberRaiseSpeed = 1.0;
	
	boolean placeCubeScale = false;
	boolean placeCubeSwitch = false;
	Timer climberRaiseTimer = new Timer();
	Timer grabberReleaseTimer = new Timer();
	
	public void onAutonomousInit(String robotSide)
	{
		climberRaiseTimer.stop();
		climberRaiseTimer.reset();
		
		grabberReleaseTimer.stop();
		grabberReleaseTimer.reset();
		
		String fieldSides = DriverStation
			.getInstance()
			.getGameSpecificMessage();
			
		if(robotSide == null) {robotSide = "X";}
		if(robotSide.length() != 1) {robotSide = "X";}
		
		if(
				robotSide.charAt(0) != 'X'
				&& fieldSides.length() == 3
				&& robotSide.charAt(0) == fieldSides.charAt(1))
		{
			placeCubeScale = true;
			
		} else if(
				robotSide.charAt(0) != 'X'
				&& fieldSides.length() == 3
				&& robotSide.charAt(0) == fieldSides.charAt(0))
		{
			placeCubeSwitch = true;
		}
	}
	
	public boolean raiseArm()
	{
		
		if(placeCubeScale || placeCubeSwitch)
		{
			if(RobotMap.armEncoderVal <= RobotMap.armEncoderLimit)
			{
				RobotMap.armEncoderVal += 1000;
				SmartDashboard.putString("Status", "raising arm");
				SmartDashboard.putNumber("arm encoder", RobotMap.armEncoderVal);
				RobotMap.arms.set(config_armRaiseSpeed);
			}
			if(RobotMap.armEncoderVal >= RobotMap.armEncoderLimit)
			{
				RobotMap.arms.set(0);
				return true;
			}
		}
		return false;
	}
	
	public boolean raiseClimber()
	{
		if(placeCubeScale)
		{
			if(climberRaiseTimer.get() == 0)
			{
				SmartDashboard.putString("Status", "raising arm");
				climberRaiseTimer.start();
				RobotMap.climber.set(config_climberRaiseSpeed);
			}
			if(climberRaiseTimer.get() > config_climberRaiseTime)
			{
				climberRaiseTimer.stop();
				RobotMap.climber.set(0);
				return true;
			}
		}
		return false;
	}
	
	public void maybePlaceCube()
	{
		if(placeCubeScale || placeCubeSwitch)
		{
			if(climberRaiseTimer.get() > config_climberRaiseTime)
			{
				climberRaiseTimer.stop();
				RobotMap.arms.set(0);
				
				if(grabberReleaseTimer.get() == 0)
				{
					grabberReleaseTimer.start();
					RobotMap.grabber.set(RobotMap.grabberOpen);
				}
				
				if(grabberReleaseTimer.get() > config_grabberReleaseTime)
				{
					grabberReleaseTimer.stop();
					RobotMap.grabber.set(DoubleSolenoid.Value.kOff);
				}
			}
		}
	}
}
