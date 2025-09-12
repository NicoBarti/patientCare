package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class TestInterfaces {
	RunWithParams runWithParams;
	
	@Test
	void try_a_parametrized_run_Run_With_Params() {
		//runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [1752865141452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"]}");
		//runWithParams.runSimulation();
		//runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [1752871014452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"], \"obsSimpleE\": [\"t\"]}");
		runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [15286512341452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"], \"obsSimpleE\": [\"t\"]}");

		System.out.println("SimpleE in care observer: "+runWithParams.simulation.observer.obsSimpleE);
		runWithParams.runSimulation();
		double [][] E_p_i = runWithParams.simulation.observer.getSimpleE();
		for(int p = 0; p<runWithParams.simulation.N;p++) {
			for(int i = 0; i<runWithParams.simulation.varsigma;i++) {
				System.out.println(E_p_i[p][i]);
			}
		}
	}	
}