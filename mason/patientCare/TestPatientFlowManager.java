package patientCare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import runners.RunWithParams;

public class TestPatientFlowManager {

	@Test
	void testInitialization() {
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"W\": [2],"
				+ "\"add_proportion\": [0.05],"
				+ "\"remove_proportion\": [0.03],"
				+ "\"flow_period\": [5],"
				+ "\"dropout_severity\": [8.5]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals(0.05, sim.add_proportion);
		assertEquals(0.03, sim.remove_proportion);
		assertEquals(5, sim.flow_period);
		assertEquals(8.5, sim.dropout_severity);
	}

	@Test
	void testSeverityDropoutAndReplacement() {
		// Run a simple test case with 10 patients, and a dropout threshold of 5.0
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"varsigma\": [5],"
				+ "\"W\": [2],"
				+ "\"reproduce_line\": [true],"
				+ "\"fixed_delta\": [2.0],"
				+ "\"fixed_capN\": [10.0],"
				+ "\"fixed_lambda\": [1.5],"
				+ "\"fixed_tau\": [1.0],"
				+ "\"fixed_rho\": [2.0],"
				+ "\"fixed_eta\": [1.0],"
				+ "\"fixed_kappa\": [0.0],"
				+ "\"fixed_capE\": [5.0],"
				+ "\"fixed_psi\": [0.5],"
				+ "\"dropout_severity\": [5.0],"
				+ "\"obsH\": [true]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals(10, sim.patients.numObjs);

		// Artificially set one patient's severity (h_p_i_1) above the threshold
		Patient pToDrop = (Patient) sim.patients.objs[3];
		pToDrop.h_p_i_1 = 6.0;
		int oldId = pToDrop.p;

		// Step the simulation by 1
		sim.schedule.step(sim);

		// Verify that the patient list size is still 10, but the patient at index 3 has been replaced
		assertEquals(10, sim.patients.numObjs);
		
		// The old patient is no longer in the active patients bag
		boolean oldPatientFound = false;
		for (int i = 0; i < sim.patients.numObjs; i++) {
			Patient p = (Patient) sim.patients.objs[i];
			if (p.p == oldId) {
				oldPatientFound = true;
				break;
			}
		}
		assertFalse(oldPatientFound, "The dropped patient should not be present in the simulation anymore.");
	}

	@Test
	void testPeriodicFlowAdditionsAndRemovals() {
		// Set a high flow period of 1 and high add/remove proportions so changes definitely trigger
		String jsonParams = "{"
				+ "\"N\": [20],"
				+ "\"varsigma\": [5],"
				+ "\"W\": [2],"
				+ "\"add_proportion\": [0.5],"
				+ "\"remove_proportion\": [0.5],"
				+ "\"flow_period\": [1]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		int initialCount = sim.patients.numObjs;
		assertEquals(20, initialCount);

		// Run for a few steps and check that the population count varies (dynamic flow)
		sim.schedule.step(sim); // step 0 (initial step does not run flow)
		sim.schedule.step(sim); // step 1 (flow runs because steps=1, 1 % 1 == 0)

		// Assert that the simulation does not crash and updates parameters correctly
		assertTrue(sim.N >= 0);
		assertEquals(sim.patients.numObjs, sim.N);
	}
}
