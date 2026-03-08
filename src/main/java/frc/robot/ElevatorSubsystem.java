import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class ElevatorSubsystem extends SubsystemBase {
  // 1. Real Hardware (Using Constants for Ports)
  private final PWMSparkMax m_motor = new PWMSparkMax(Constants.kMotorPort);
  private final Encoder m_encoder = new Encoder(Constants.kEncoderAChannel, Constants.kEncoderBChannel);

  // 2. Simulation Physics Engine
  private final ElevatorSim m_elevatorSim = new ElevatorSim(
      DCMotor.getVex775Pro(4),      // Gearbox (4 motors) 
      Constants.kElevatorGearing,   // Gearing ratio [cite: 27]
      Constants.kCarriageMass,      // Mass in kg [cite: 27]
      Constants.kElevatorDrumRadius,// Pulley radius [cite: 27]
      0.0,                          // Min Height (meters) [cite: 27]
      1.5,                          // Max Height (meters) [cite: 27]
      true                          // Simulate Gravity [cite: 27]
  );

  // 3. Virtual Hardware for the Sim to "talk" to
  private final EncoderSim m_encoderSim = new EncoderSim(m_encoder);
  private final PWMSim m_motorSim = new PWMSim(m_motor);

  public void setSpeed(double speed) {
    m_motor.set(speed);
  }

  @Override
  public void simulationPeriodic() {
    // Tell the sim how much voltage is being applied
    m_elevatorSim.setInput(m_motorSim.getSpeed() * RobotController.getBatteryVoltage());

    // Advance physics by 20ms
    m_elevatorSim.update(0.020);

    // Update the 'fake' encoder so the rest of the code sees the movement
    m_encoderSim.setDistance(m_elevatorSim.getPositionMeters());
  }
}