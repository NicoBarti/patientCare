package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class TestInterfaces {
	RunWithParams runWithParams;
	
	@Test
	//TODO maybe only test if the retreived params are ok, and the corresponding arrays exist
	//In a pathfinder run you specify a varsigma, and some outcome to look; usually only the final result
	//It's called a path finder because you get several repetitions keeping only varsigma stable and all the 
	//other params move at random.
	//The output is the params, and the seed, so you can reproduce it.
	//Also, a pathFinder with seed reproduces the call (but maybe this behaviour should be dealt with by a 
	//standard run with params.
	void try_a_pathFinder_run_Run_With_Params() {
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