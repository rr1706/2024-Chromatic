package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;

public class BiDirectionalIntake extends Command {
    private final Intake m_intake;
    private final Drivetrain m_robotDrive;
    private final Indexer m_indexer;
    private final Feeder m_feeder;
    private final Timer m_timer = new Timer();
    private boolean m_finishedIntake = false;
    private boolean m_currentSpiked = false;

    public BiDirectionalIntake(Intake intake, Drivetrain robotDrive, Indexer indexer, Feeder feeder){
        m_intake = intake;
        m_robotDrive = robotDrive;
        m_indexer = indexer;
        m_feeder = feeder;

    }
    

@Override
public void initialize(){
    SmartDashboard.putNumber("Feeder Encoder", m_feeder.getEncoder());
    SmartDashboard.putNumber("Velocity", m_robotDrive.getChassisSpeed().vxMetersPerSecond);
    m_timer.reset();
    m_timer.start();
    m_currentSpiked = false;
    m_finishedIntake = false;
    m_indexer.run(0.8);
    m_feeder.run(0.4);
    double robotVelocity = m_robotDrive.getChassisSpeed().vxMetersPerSecond;
    m_intake.run(1.0, robotVelocity);
}

@Override
public void execute(){
    double robotVelocity = m_robotDrive.getChassisSpeed().vxMetersPerSecond;
    m_intake.run(1.0, robotVelocity);

    if(m_feeder.getCurrent() > 15.0 && !m_currentSpiked && m_timer.get() > 0.2){
        m_currentSpiked = true;
        m_indexer.run(0.4);
        m_feeder.setZero();
        m_feeder.setPose(3.0);
    }
    if (m_currentSpiked && m_feeder.atSetpoint()){
        m_finishedIntake = true;
    }


   
}

@Override
public boolean isFinished() {
    return m_finishedIntake;
}
@Override
public void end(boolean interrupted){
    m_intake.stop();
    m_indexer.stop();
    m_feeder.stop();
}

}