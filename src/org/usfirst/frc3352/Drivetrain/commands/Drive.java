// RobotBuilder Version: 1.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.
package org.usfirst.frc3352.Drivetrain.commands;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc3352.Drivetrain.Robot;
import org.flamingmonkeys.lib.Utils;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 *
 */
public class  Drive extends Command {
    double rightPreviousVal;
    double leftPreviousVal;
    final double rampLimit = .05;
    final double kDeadband = .2;
    final double kGain = .8;
    double turnVal;
    double forwardVal;
    double motorVals[] = new double[2];
    double leftVal;
    double rightVal;

    /**
     *@author = Zaque
     */
    public Drive() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
	
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.drivechassis);
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        forwardVal = inverseDeadband(Robot.oi.getForwardSpeed());
        SmartDashboard.putNumber("inverseDeadband", forwardVal);
        turnVal = Utils.deadband(Robot.oi.getTurnSpeed(), .2);
        SmartDashboard.putNumber("turnVal", turnVal);
        turnVal = quickTurn(turnVal, forwardVal);
        SmartDashboard.putNumber("quickTurn", turnVal);
        motorVals[0]=forwardVal+turnVal;
        motorVals[1]=forwardVal-turnVal;
        SmartDashboard.putNumber("motorVal1", motorVals[0]);
        SmartDashboard.putNumber("motorVal2", motorVals[1]);
        motorVals = saturation(motorVals[0], motorVals[1]);
        SmartDashboard.putNumber("leftNormalized", motorVals[0]);
        SmartDashboard.putNumber("rightNormalized", motorVals[1]);
        leftVal = ramp(motorVals[0], leftPreviousVal);
        SmartDashboard.putNumber("leftRamp", leftVal);
        rightVal = ramp(motorVals[1], rightPreviousVal);
        SmartDashboard.putNumber("rightRamp", rightVal);
        Robot.drivechassis.setMotors(leftVal, rightVal);
        leftPreviousVal = leftVal;
        rightPreviousVal = rightVal;
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    // Called once after isFinished returns true
    protected void end() {
        Robot.drivechassis.setMotors(0, 0);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
    public double ramp(double setVal, double lastVal){
        if(Math.abs(setVal)>kDeadband){
            double changeVal = setVal - lastVal;
            if(changeVal>rampLimit){
                setVal = lastVal + rampLimit;
            }else if(changeVal<-rampLimit){
                setVal = lastVal - rampLimit;
            }
        }
        return setVal;
    }
    public double inverseDeadband(double x){
        if(x>0){
            x=kDeadband+(1-kDeadband)*((kGain*(x*x*x))+(1-kGain)*x);
        }else if(x<0){
            x=-kDeadband+(1-kDeadband)*((kGain*(x*x*x))+(1-kGain)*x);
        }
        return x;
    }
    public double[] saturation(double leftSpeed, double rightSpeed){
        double maxMagnitude;
        double speeds[] = new double[2];
        maxMagnitude=Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
        if(maxMagnitude>1){
            speeds[0]=leftSpeed/maxMagnitude;
            speeds[1]=rightSpeed/maxMagnitude;
        }else{
            speeds[0]=leftSpeed;
            speeds[1]=rightSpeed;
        }
        return speeds;
    }
    public double quickTurn(double turnSpeed, double forwardSpeed){
        if(forwardSpeed>0){
            turnSpeed = -forwardSpeed*turnSpeed;
        }else if(forwardSpeed<0){
            turnSpeed = forwardSpeed*turnSpeed;
        }else{
            turnSpeed = -turnSpeed;
        }
        return turnSpeed;
    }
}