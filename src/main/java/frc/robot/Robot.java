// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.simulation.AnalogInputSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

/**
 * This sample program shows how to use Mechanism2d - a visual representation of arms, elevators,
 * and other mechanisms on dashboards; driven by a node-based API.
 *
 * <p>Ligaments are based on other ligaments or roots, and roots are contained in the base
 * Mechanism2d object.
 */
public class Robot extends TimedRobot {

  private final PWMSparkMax m_elevatorMotor = new PWMSparkMax(Constants.kelevatorDIOport);
  private final PWMSparkMax m_wristMotor = new PWMSparkMax(Constants.kwristDIOport);
  private final AnalogPotentiometer m_wristPot = new AnalogPotentiometer(1, 90);
  private final Encoder m_elevatorEncoder = new Encoder(0, 1);

  private final EncoderSim m_elevatorEncoderSim = new EncoderSim(m_elevatorEncoder);
  private final AnalogInputSim m_wristPotSim = new AnalogInputSim(1);  
  // Keyboard 0 in the Sim GUI acts as a Joystick on port 0
  private final Joystick m_keyboard = new Joystick(0); 

  private final MechanismLigament2d m_elevator;
  private final MechanismLigament2d m_wrist;

  public Robot() {
    m_elevatorEncoder.setDistancePerPulse(Constants.kMetersPerPulse);
    Mechanism2d mech = new Mechanism2d(3, 3);
    MechanismRoot2d root = mech.getRoot("climber", 2, 0);

    m_elevator = root.append(new MechanismLigament2d("elevator", Constants.kElevatorMinimumLength, 90));
    m_wrist = m_elevator.append(new MechanismLigament2d("wrist", 0.5, 90, 6, new Color8Bit(Color.kPurple)));

    SmartDashboard.putData("Mech2d", mech);
  }

  @Override
  public void robotPeriodic() {
    // 1. Update the dashboard mechanism's state 
    m_elevator.setLength(Constants.kElevatorMinimumLength + m_elevatorEncoder.getDistance());
    m_wrist.setAngle(m_wristPot.get());
  }

  @Override
  public void teleopPeriodic() {
    // 2. Elevator Control: Button 1 (Z) = Up, Button 2 (X) = Down
    if (m_keyboard.getRawButton(1)) {
        m_elevatorMotor.set(0.5);
    } else if (m_keyboard.getRawButton(2)) {
        m_elevatorMotor.set(-0.5);
    } else {
        m_elevatorMotor.set(0.0);
    }

    // 3. Wrist Control: Button 3 (C) = Up, Button 4 (V) = Down
    if (m_keyboard.getRawButton(3)) {
        m_wristMotor.set(0.3);
    } else if (m_keyboard.getRawButton(4)) {
        m_wristMotor.set(-0.3);
    } else {
        m_wristMotor.set(0.0);
    }

    // Reset Logic: Button 9 (R Key) = Back to Home
    if (m_keyboard.getRawButton(9)) {
        // Force the sim objects back to their initial constants
        m_elevatorEncoderSim.setDistance(Constants.kElevatorMinimumLength);
        
        // Assuming 0.0V is your 'Home' position for the potentiometer
        m_wristPotSim.setVoltage(0.0); 
    }
  }

  @Override
  public void simulationPeriodic() {
    // 3. Update Elevator Physics
    double elevatorSpeed = m_elevatorMotor.get();
    double newElevatorDistance = m_elevatorEncoder.getDistance() + (elevatorSpeed * 0.02);
    m_elevatorEncoderSim.setDistance(newElevatorDistance);

    // 4. Update Wrist Physics
    // We calculate the change in voltage based on motor speed
    double wristSpeed = m_wristMotor.get();
    // Adjust the '0.1' value to make the wrist move faster or slower in sim
    double newWristVoltage = m_wristPotSim.getVoltage() + (wristSpeed * 0.1);
    
    // Push the new voltage to the simulated potentiometer
    m_wristPotSim.setVoltage(newWristVoltage);
  }
}