// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
// MoveElevator.java
public class MoveElevator extends Command {
  private final ElevatorSubsystem m_subsystem;
  private final double m_targetHeight;

  public MoveElevator(ElevatorSubsystem subsystem, double height) {
    m_subsystem = subsystem;
    m_targetHeight = height;
    addRequirements(m_subsystem); [cite: 78]
  }

  @Override
  public void execute() {
    // Simple logic: if below target, go up; if above, go down
    double speed = (m_subsystem.getHeight() < m_targetHeight) ? 0.3 : -0.3;
    m_subsystem.setVoltage(speed * 12.0);
  }

  @Override
  public boolean isFinished() {
    // Stop when we are within 5cm of target [cite: 48, 52]
    return Math.abs(m_subsystem.getHeight() - m_targetHeight) < 0.05;
  }

  @Override
  public void end(boolean interrupted) {
    m_subsystem.setVoltage(0);
  }
}