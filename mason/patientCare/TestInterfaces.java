package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class TestInterfaces {
	RunWithParams runWithParams;
	
	
	@Test
	void try_a_parametrized_run_Run_With_Params() {
		runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [1752865141452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"]}");
		runWithParams.runSimulation();
	}
	
}
