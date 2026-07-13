package runners;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import patientCare.Care;
import patientCare.Patient;

/**
 * Test suite verifying the parameter parsing, configuration initialization,
 * serialization, and execution loops of RunWithParams.
 */
public class TestRunWithParams {

	@Test
	void testBasicInitializationAndParsing() {
		String jsonParams = "{"
				+ "\"N\": [50],"
				+ "\"varsigma\": [20],"
				+ "\"W\": [3],"
				+ "\"Pi\": [\"risk\"],"
				+ "\"PROVIDER_INIT\": [\"random\"],"
				+ "\"PATIENT_INIT\": [\"basal\"],"
				+ "\"obsH\": [true],"
				+ "\"seed\": [12345]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals(50, sim.N);
		assertEquals(20, sim.varsigma);
		assertEquals(3, sim.W);
		assertEquals("risk", sim.Pi);
		assertEquals("random", sim.PROVIDER_INIT);
		assertEquals("basal", sim.PATIENT_INIT);
		assertEquals(12345, sim.getSeed());
	}

	@Test
	void testReproduceLineMode() {
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"varsigma\": [5],"
				+ "\"W\": [2],"
				+ "\"Pi\": [\"need\"],"
				+ "\"reproduce_line\": [true],"
				+ "\"totalCapacity\": [15],"
				+ "\"fixed_delta\": [7.5],"
				+ "\"fixed_capN\": [8.0],"
				+ "\"fixed_lambda\": [1.5],"
				+ "\"fixed_tau\": [1.0],"
				+ "\"fixed_rho\": [2.0],"
				+ "\"fixed_eta\": [1.0],"
				+ "\"fixed_kappa\": [0.5],"
				+ "\"fixed_capE\": [5.0],"
				+ "\"fixed_psi\": [0.3]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals("applyFixed", sim.PATIENT_INIT);
		assertEquals("applyFixed", sim.PROVIDER_INIT);
		assertEquals(15, sim.totalCapacity);

		// Verify patient properties are correctly initialised
		assertTrue(sim.patients.numObjs > 0);
		Patient firstPatient = (Patient) sim.patients.objs[0];
		assertEquals(7.5, firstPatient.getdelta());
		assertEquals(8.0, firstPatient.getcapN());
		assertEquals(5.0, firstPatient.getcapE());
		assertEquals(0.3, firstPatient.getpsi());
	}

	@Test
	void testPrioritizationGranularity() {
		String jsonParams = "{"
				+ "\"N\": [5],"
				+ "\"Pi\": [\"need\"],"
				+ "\"prioritization_granularity\": [0.6]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals(0.6, sim.prioritization_granularity);
		assertNotNull(sim.prioritize);
		// prioritization_granularity 0.6
		assertEquals(0.6, sim.prioritize.getGranularity(), 0.001);
	}

	@Test
	void testGetParamsSerialization() {
		String jsonParams = "{"
				+ "\"N\": [5],"
				+ "\"varsigma\": [10],"
				+ "\"Pi\": [\"basal\"]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		String response = runner.getParams();

		assertNotNull(response);
		assertTrue(response.contains("\"N\""));
		assertTrue(response.contains("\"varsigma\""));
		assertTrue(response.contains("\"Pi\""));
	}

	@Test
	void testRunSimulationExecution() {
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"varsigma\": [5],"
				+ "\"W\": [2],"
				+ "\"Pi\": [\"basal\"]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		runner.runSimulation();

		// After simulation is run, it is finished and step count reaches varsigma
		assertEquals(5, runner.getSimulation().schedule.getSteps());
	}

	@Test
	void testPathFinderConfigurationMode() {
		String jsonParams = "{"
				+ "\"N\": [5],"
				+ "\"varsigma\": [25],"
				+ "\"pathfinder\": [true]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertEquals(25, sim.getvarsigma());
		assertEquals("sensitivity_1", sim.PATIENT_INIT);
		assertEquals("sensitivity_1", sim.PROVIDER_INIT);
	}

	@Test
	void testRandomizedInitializationParameters() {
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"reproduce_line\": [true],"
				+ "\"random_delta_min\": [2.0],"
				+ "\"random_delta_max\": [8.0],"
				+ "\"random_eta_min\": [0.1],"
				+ "\"random_eta_max\": [0.5],"
				+ "\"random_rho_min\": [1.2],"
				+ "\"random_rho_max\": [3.4],"
				+ "\"initial_h\": [6.5]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertNotNull(sim.pat_init);
		assertTrue(sim.pat_init.random_delta);
		assertEquals(2.0, sim.pat_init.random_delta_min);
		assertEquals(8.0, sim.pat_init.random_delta_max);
		assertTrue(sim.pat_init.random_eta);
		assertEquals(0.1, sim.pat_init.random_eta_min);
		assertEquals(0.5, sim.pat_init.random_eta_max);
		assertTrue(sim.pat_init.random_rho);
		assertEquals(1.2, sim.pat_init.random_rho_min);
		assertEquals(3.4, sim.pat_init.random_rho_max);
		assertTrue(sim.pat_init.initial_h);
		assertEquals(6.5, sim.pat_init.h0_value);
	}

	@Test
	void testLocScaleDeltaInitialization() {
		String jsonParams = "{"
				+ "\"N\": [10],"
				+ "\"reproduce_line\": [true],"
				+ "\"loc_delta\": [5.5],"
				+ "\"scale_delta\": [1.5],"
				+ "\"random_delta_min\": [2.0],"
				+ "\"random_delta_max\": [8.0],"
				+ "\"initial_h\": [6.5]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		assertNotNull(sim.pat_init);
		assertTrue(sim.pat_init.loc_scale_delta);
		assertFalse(sim.pat_init.random_delta);
		assertEquals(5.5, sim.pat_init.loc_delta);
		assertEquals(1.5, sim.pat_init.scale_delta);
		assertEquals(2.0, sim.pat_init.random_delta_min);
		assertEquals(8.0, sim.pat_init.random_delta_max);

		String response = runner.getParams();
		assertNotNull(response);
		assertTrue(response.contains("\"loc_delta\":\"5.5\""));
		assertTrue(response.contains("\"scale_delta\":\"1.5\""));
		assertTrue(response.contains("\"random_delta_min\":\"2.0\""));
		assertTrue(response.contains("\"random_delta_max\":\"8.0\""));
	}

	@Test
	void testConfigurationValidationMixedAndIncomplete() {
		// Incomplete Gaussian case (missing uniform delta bounds)
		String jsonIncompleteGaussian = "{"
				+ "\"N\": [10],"
				+ "\"reproduce_line\": [true],"
				+ "\"loc_delta\": [5.5],"
				+ "\"scale_delta\": [1.5]"
				+ "}";
		assertThrows(IllegalArgumentException.class, () -> new RunWithParams(jsonIncompleteGaussian));

		// Incomplete uniform case (missing max)
		String jsonIncompleteUniform = "{"
				+ "\"N\": [10],"
				+ "\"reproduce_line\": [true],"
				+ "\"random_delta_min\": [2.0]"
				+ "}";
		assertThrows(IllegalArgumentException.class, () -> new RunWithParams(jsonIncompleteUniform));

		// Incomplete loc-scale case (missing scale)
		String jsonIncompleteLocScale = "{"
				+ "\"N\": [10],"
				+ "\"reproduce_line\": [true],"
				+ "\"loc_delta\": [5.5]"
				+ "}";
		assertThrows(IllegalArgumentException.class, () -> new RunWithParams(jsonIncompleteLocScale));
	}

	@Test
	void testObservationFlagsConfiguration() {
		String jsonParams = "{"
				+ "\"N\": [5],"
				+ "\"obsH\": [true],"
				+ "\"obsN\": [true],"
				+ "\"obsC\": [true],"
				+ "\"obsT\": [true],"
				+ "\"obsE\": [true],"
				+ "\"obsB\": [true],"
				+ "\"obsSimpleC\": [true],"
				+ "\"obsSimpleE\": [true],"
				+ "\"obsSimpleB\": [true],"
				+ "\"obsDisease\": [true],"
				+ "\"obsDelta\": [true],"
				+ "\"obsRho\": [true],"
				+ "\"obsEta\": [true],"
				+ "\"obsExpNoise\": [true],"
				+ "\"obsInstExp\": [true],"
				+ "\"obsPerformance\": [true],"
				+ "\"obsMaxExp\": [true]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		String response = runner.getParams();

		assertNotNull(response);
		// Check that the returned params JSON string has all flags set to true
		assertTrue(response.contains("\"obsH\":\"true\""));
		assertTrue(response.contains("\"obsN\":\"true\""));
		assertTrue(response.contains("\"obsC\":\"true\""));
		assertTrue(response.contains("\"obsT\":\"true\""));
		assertTrue(response.contains("\"obsE\":\"true\""));
		assertTrue(response.contains("\"obsB\":\"true\""));
		assertTrue(response.contains("\"obsSimpleC\":\"true\""));
		assertTrue(response.contains("\"obsSimpleE\":\"true\""));
		assertTrue(response.contains("\"obsSimpleB\":\"true\""));
		assertTrue(response.contains("\"obsDisease\":\"true\""));
		assertTrue(response.contains("\"obsDelta\":\"true\""));
		assertTrue(response.contains("\"obsRho\":\"true\""));
		assertTrue(response.contains("\"obsEta\":\"true\""));
		assertTrue(response.contains("\"obsExpNoise\":\"true\""));
		assertTrue(response.contains("\"obsInstExp\":\"true\""));
		assertTrue(response.contains("\"obsPerformance\":\"true\""));
		assertTrue(response.contains("\"obsMaxExp\":\"true\""));
	}

	@Test
	void testSeedHandlingZero() {
		String jsonParams = "{"
				+ "\"N\": [5],"
				+ "\"seed\": [0]"
				+ "}";

		RunWithParams runner = new RunWithParams(jsonParams);
		Care sim = runner.getSimulation();

		assertNotNull(sim);
		// With seed = 0, it should use a non-zero time-based seed
		assertTrue(sim.getSeed() != 0);
	}

	@Test
	void testMalformedJsonThrowsException() {
		// Passing an invalid json should throw JSONException
		assertThrows(org.json.JSONException.class, () -> {
			new RunWithParams("{invalid_json");
		});
	}
}
