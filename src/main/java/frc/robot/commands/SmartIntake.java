package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.DriveConstants;
import frc.robot.LimelightHelpers;
import frc.robot.subsystems.Drivetrain;
import frc.robot.utilities.MathUtils;

public class SmartIntake extends Command {
    private final Drivetrain m_drive;
    private final CommandXboxController m_controller;
    private final PIDController  m_pid = new PIDController(0.09,0.0,0.0);
    private boolean m_detectedOnce = false;
    private double ty_check = 25.0;
    public SmartIntake(Drivetrain drive, CommandXboxController controller){
        m_drive = drive;
        m_controller = controller;
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        ty_check = 25.0;
        m_detectedOnce = false;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
    double desiredTrans[] = MathUtils.inputTransform(-m_controller.getLeftY(), -m_controller.getLeftX());
    double maxLinear = DriveConstants.kMaxSpeedMetersPerSecond;

    desiredTrans[0] *= maxLinear;
    desiredTrans[1] *= maxLinear;

    double tx = LimelightHelpers.getTX("limelight-note");
    boolean tv = LimelightHelpers.getTV("limelight-note");
    double ty = LimelightHelpers.getTY("limelight-note");

    double desiredRot = -MathUtils.inputTransform(m_controller.getRightX())* DriveConstants.kMaxAngularSpeed;

    if(tv && !m_detectedOnce){
        m_detectedOnce = true;
        ty_check = ty;
        desiredRot = m_pid.calculate(tx);
    }
    else if(tv && (ty_check+1.0) >= ty){
        ty_check = ty;
        desiredRot = m_pid.calculate(tx);
    }

    SmartDashboard.putNumber("TX Note", tx);
    SmartDashboard.putNumber("TY Note", ty);


    //double desiredRot = -MathUtils.inputTransform(m_controller.getRightX())* DriveConstants.kMaxAngularSpeed;

    //Translation2d rotAdj= desiredTranslation.rotateBy(new Rotation2d(-Math.PI/2.0)).times(desiredRot*0.05);

    //desiredTranslation = desiredTranslation.plus(rotAdj);

    m_drive.drive(desiredTrans[0], desiredTrans[1],desiredRot,true,true);

/*     m_robotDrive.drive(m_slewX.calculate(
        -inputTransform(m_controller.getLeftY()))
        * DriveConstants.kMaxSpeedMetersPerSecond,
        m_slewY.calculate(
            -inputTransform(m_controller.getLeftX()))
            * DriveConstants.kMaxSpeedMetersPerSecond,
        m_slewRot.calculate(-inputTransform(m_controller.getRightX()))
            * DriveConstants.kMaxAngularSpeed,
        fieldOrient); */

        SmartDashboard.putBoolean("DrivingByController", true);    }

    @Override
    public void end(boolean interrupted) {
        // TODO Auto-generated method stub
        super.end(interrupted);
    }
    
}
