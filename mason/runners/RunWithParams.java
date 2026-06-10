package runners;

import org.json.JSONObject;
import patientCare.Care;
import patientCare.PatientInitializer;
import patientCare.ProviderInitializer;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RunWithParams is a configuration wrapper and execution harness for the patientCare simulation.
 * It parses JSON strings containing simulation parameters, dynamically instantiates and
 * configures the MASON simulation engine class (Care), handles initial state set up (such as fixed or
 * randomized distributions), manages simulation scheduler stepping, and handles serialization
 * of parameters back to JSON format.
 */
public class RunWithParams {
	/** The active simulation instance. */
	Care simulation;

	/** The raw JSON object containing the parsed parameters. */
	JSONObject params;

	// internals
	/** The simulation execution seed value. A value of 0 triggers a timestamp-based seed. */
	long seed;

	/** Frequency of state observations in simulation steps. Defaults to 0 (which triggers end-of-sim observation). */
	int OBS_PERIOD = 0;

	/** The total number of patient agents. Defaults to 1000. */
	int N = 1000;

	/** The total number of simulation steps to run. Defaults to 150. */
	int varsigma = 150;

	/** The total number of doctor/provider agents. Defaults to 2. */
	int W = 2;

	/** Total available medical appointments across all providers. */
	int totalCapacity;

	/** Initialisation strategy for patient agents. Defaults to "default". */
	String PATIENT_INIT = "default";

	/** Initialisation strategy for provider agents. Defaults to "default". */
	String PROVIDER_INIT = "default";

	/** The resource allocation policy (e.g., "basal", "risk", "need", "risk_need"). Defaults to "basal". */
	String pi = "basal";

	/** Flag to enable observing patient health status (H). */
	boolean obsH = false;

	/** Flag to enable observing patient need (N). */
	boolean obsN = false;

	/** Flag to enable observing patient actual interaction with provider (C). */
	boolean obsC = false;

	/** Flag to enable observing patient treatment status (T). */
	boolean obsT = false;

	/** Flag to enable observing patient expectations (E). */
	boolean obsE = false;

	/** Flag to enable observing patient seeking behavior (B). */
	boolean obsB = false;

	/** Flag to enable simplified summary observations for patient actual interaction. */
	boolean simpleC = false;

	/** Flag to enable simplified summary observations for expectations. */
	boolean simpleE = false;

	/** Flag to enable simplified summary observations for seeking behavior. */
	boolean simpleB = false;

	/** Flag indicating if the simulation should be configured using PathFinder presets. */
	boolean configure_pathfinder = false;

	/** Flag indicating if the simulation should reproduce a specific fixed line configuration. */
	boolean reproduce_line = false;

	/** Flag to enable observing disease progression event counters. */
	boolean obsDisease = false;

	/** Flag to enable observing severity parameter value distribution. */
	boolean obsDelta = false;

	/** Flag to enable observing expectations noise. */
	boolean obsExpNoise = false;

	/** Flag to enable observing instantaneous expectation updates. */
	boolean obsInstExp = false;

	/** Flag to enable observing performance indicators. */
	boolean obsPerformance = false;

	/** Flag to enable observing maximum expectation values. */
	boolean obsMaxExp = false;

	// Patient initializers
	/** The fixed severity (delta) assigned to all patient agents. */
	double fixed_delta;

	/** The fixed need capacity (capN) assigned to all patient agents. */
	double fixed_capN;

	/** The fixed expectations growth rate (rho) assigned to all patient agents. */
	double fixed_rho;

	/** The fixed expectations decay rate (eta) assigned to all patient agents. */
	double fixed_eta;

	/** The fixed expectations fluctuation scale (kappa) assigned to all patient agents. */
	float fixed_kappa;

	/** The fixed expectations limit (capE) assigned to all patient agents. */
	double fixed_capE;

	/** The fixed motivation weight (psi) assigned to all patient agents. */
	double fixed_psi;

	/** Flag enabling randomized severity parameters among patients. */
	private boolean random_delta = false;

	/** The minimum randomized severity (delta) boundary for patients. */
	double random_delta_min;

	/** The maximum randomized severity (delta) boundary for patients. */
	double random_delta_max;

	/** Flag enabling randomized expectations decay rate parameters among patients. */
	private boolean random_eta = false;

	/** The minimum randomized expectations decay rate (eta) boundary for patients. */
	double random_eta_min;

	/** The maximum randomized expectations decay rate (eta) boundary for patients. */
	double random_eta_max;

	/** Flag enabling randomized expectations growth rate parameters among patients. */
	private boolean random_rho = false;

	/** The minimum randomized expectations growth rate (rho) boundary for patients. */
	double random_rho_min;

	/** The maximum randomized expectations growth rate (rho) boundary for patients. */
	double random_rho_max;

	/** Flag indicating whether the initial health status of patients should be fixed. */
	private boolean initial_h = false;

	/** The fixed initial health status value for patients if initial_h is enabled. */
	public double h0_value = 0;

	// Provider initializers
	/** The fixed treatment efficacy (lambda) assigned to all provider agents. */
	double fixed_lambda;

	/** The fixed treatment unit cost/time (tau) assigned to all provider agents. */
	double fixed_tau;

	/**
	 * Setting the granularity of Prioritization.
	 * -1 represents basal (automatic policy defaults as defined in Prioritizator.java).
	 * Any value between 0.0 and 10.0 specifies custom class-level granularity.
	 */
	double prioritization_granularity = -1;

	/**
	 * Constructs a RunWithParams instance and initializes a MASON simulation (Care)
	 * with parameters parsed from a JSON string.
	 *
	 * @param par The JSON string representation of the parameters.
	 */
	public RunWithParams(String par) {
		params = new JSONObject(par);
		readParameters();
		if (seed == 0) {
			long uniqueSeed = Math.abs(System.currentTimeMillis() ^ (Thread.currentThread().getId() << 16) ^ System.nanoTime());
			simulation = new Care(uniqueSeed);
		} else {
			simulation = new Care(seed);
			System.out.println("JAVA (RunWithParams.java) started with seed " + seed);
		}

		if (configure_pathfinder) {
			System.out.println("JAVA (RunWithParams.java) going to use configure_pathfinder");
			configure_pathfinder();
		} else {
			if (reproduce_line) {
				System.out.println("JAVA (RunWithParams.java) going to use reproduceLine");
				reproduceLine();
			} else {
				regularParamImplementation();
				System.out.println("JAVA (RunWithParams.java) going to use regularParamImplementation");
			}
		}
		simulation.start();
		simulation.startObserver(obsH, obsN, obsC, obsT, obsE, obsB,
				simpleC, simpleE, simpleB, obsDisease, obsExpNoise, obsInstExp, obsDelta, obsPerformance, obsMaxExp);
	}

	/**
	 * Configures basic parameters on the simulation instance (N, varsigma, W,
	 * patient and provider initialisation keys, observation frequency, and prioritization granularity).
	 */
	protected void regularParamImplementation() {
		simulation.setN(N);
		simulation.setvarsigma(varsigma);
		simulation.setW(W);
		simulation.setPATIENT_INIT(PATIENT_INIT);
		simulation.setPROVIDER_INIT(PROVIDER_INIT);
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setPi(pi);
		simulation.prioritization_granularity = prioritization_granularity;
	}

	/**
	 * Configures the simulation via the PathFinder model parameters. This pathway
	 * leverages the PathFinder configurations to override standard settings.
	 */
	protected void configure_pathfinder() {
		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "testing", "true" });
		pathfinder.configureCare(simulation);
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setPi(pi);
		simulation.prioritization_granularity = prioritization_granularity;
	}

	/**
	 * Configures fixed-value parameters on the patient and provider initializers.
	 * This ensures that patients and providers receive uniform, reproducible initial states
	 * based on fixed fields instead of stochastic distributions.
	 */
	protected void reproduceLine() {
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setvarsigma(varsigma);
		simulation.settotalCapacity(totalCapacity);
		simulation.W = W;
		simulation.N = N;
		simulation.setPi(pi);
		simulation.prioritization_granularity = prioritization_granularity;

		simulation.pat_init = new PatientInitializer(simulation, "applyFixed");
		simulation.setPATIENT_INIT("applyFixed");
		simulation.pat_init.fixed_delta = fixed_delta;
		simulation.pat_init.fixed_capN = fixed_capN;
		simulation.pat_init.fixed_lambda = fixed_lambda;
		simulation.pat_init.fixed_tau = fixed_tau;
		simulation.pat_init.fixed_rho = fixed_rho;
		simulation.pat_init.fixed_eta = fixed_eta;
		simulation.pat_init.fixed_kappa = fixed_kappa;
		simulation.pat_init.fixed_capE = fixed_capE;
		simulation.pat_init.fixed_psi = fixed_psi;
		if (initial_h) {
			simulation.pat_init.initial_h = true;
			simulation.pat_init.h0_value = h0_value;
		}
		if (random_delta) {
			simulation.pat_init.random_delta = true;
			simulation.pat_init.random_delta_min = random_delta_min;
			simulation.pat_init.random_delta_max = random_delta_max;
		}
		if (random_eta) {
			simulation.pat_init.random_eta = true;
			simulation.pat_init.random_eta_min = random_eta_min;
			simulation.pat_init.random_eta_max = random_eta_max;
		}
		if (random_rho) {
			simulation.pat_init.random_rho = true;
			simulation.pat_init.random_rho_min = random_rho_min;
			simulation.pat_init.random_rho_max = random_rho_max;
		}

		simulation.prov_init = new ProviderInitializer(simulation, "applyFixed");
		simulation.setPROVIDER_INIT("applyFixed");
		simulation.prov_init.fixed_lambda = fixed_lambda;
		simulation.prov_init.fixed_tau = fixed_tau;
	}

	/**
	 * Iterates over the JSON object keys and populates the class variables
	 * with corresponding values.
	 */
	private void readParameters() {
		Iterator<String> keys = params.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			JSONArray a = (JSONArray) params.get(key);

			switch (key) {
				case "N":
					N = a.getInt(0);
					break;
				case "varsigma":
					varsigma = a.getInt(0);
					break;
				case "W":
					W = a.getInt(0);
					break;
				case "PATIENT_INIT":
					PATIENT_INIT = a.getString(0);
					break;
				case "PROVIDER_INIT":
					PROVIDER_INIT = a.getString(0);
					break;
				case "OBS_PERIOD":
					OBS_PERIOD = a.getInt(0);
					break;
				case "pathfinder":
					configure_pathfinder = true;
					break;
				case "reproduce_line":
					reproduce_line = true;
					break;
				case "seed":
					seed = a.getLong(0);
					break;
				case "obsH":
					obsH = true;
					break;
				case "obsN":
					obsN = true;
					break;
				case "obsC":
					obsC = true;
					break;
				case "obsT":
					obsT = true;
					break;
				case "obsE":
					obsE = true;
					break;
				case "obsB":
					obsB = true;
					break;
				case "obsSimpleC":
					simpleC = true;
					break;
				case "obsSimpleE":
					simpleE = true;
					break;
				case "obsSimpleB":
					simpleB = true;
					break;
				case "fixed_delta":
					fixed_delta = a.getDouble(0);
					break;
				case "fixed_capN":
					fixed_capN = a.getDouble(0);
					break;
				case "fixed_lambda":
					fixed_lambda = a.getDouble(0);
					break;
				case "fixed_tau":
					fixed_tau = a.getDouble(0);
					break;
				case "fixed_rho":
					fixed_rho = a.getDouble(0);
					break;
				case "fixed_eta":
					fixed_eta = a.getDouble(0);
					break;
				case "fixed_kappa":
					fixed_kappa = a.getFloat(0);
					break;
				case "fixed_capE":
					fixed_capE = a.getDouble(0);
					break;
				case "fixed_psi":
					fixed_psi = a.getDouble(0);
					break;
				case "totalCapacity":
					totalCapacity = a.getInt(0);
					break;
				case "Pi":
					pi = a.getString(0);
					break;
				case "prioritization_granularity":
					prioritization_granularity = a.getDouble(0);
					break;
				case "obsDisease":
					obsDisease = true;
					break;
				case "obsDelta":
					obsDelta = true;
					break;
				case "obsExpNoise":
					obsExpNoise = true;
					break;
				case "obsInstExp":
					obsInstExp = true;
					break;
				case "initial_h":
					initial_h = true;
					h0_value = a.getDouble(0);
					break;
				case "random_delta_min":
					random_delta = true;
					random_delta_min = a.getDouble(0);
					break;
				case "random_delta_max":
					random_delta = true;
					random_delta_max = a.getDouble(0);
					break;
				case "random_eta_min":
					random_eta = true;
					random_eta_min = a.getDouble(0);
					break;
				case "random_eta_max":
					random_eta = true;
					random_eta_max = a.getDouble(0);
					break;
				case "random_rho_min":
					random_rho = true;
					random_rho_min = a.getDouble(0);
					break;
				case "random_rho_max":
					random_rho = true;
					random_rho_max = a.getDouble(0);
					break;
				case "obsPerformance":
					obsPerformance = true;
					break;
				case "obsMaxExp":
					obsMaxExp = true;
			}
		}
	}

	/**
	 * Pulls the final configurations and metrics from the Care simulation model,
	 * merges them with RunWithParams execution parameters, and outputs a serialized JSON string.
	 *
	 * @return The serialized parameter and run configuration JSON string.
	 */
	public String getParams() {
		HashMap<String, String> params = simulation.getParams();
		params.put("pathfinder", Boolean.toString(configure_pathfinder));
		params.put("reproduce_line", Boolean.toString(reproduce_line));

		params.put("PATIENT_INIT", PATIENT_INIT);
		params.put("PROVIDER_INIT", PROVIDER_INIT);
		params.put("obsH", Boolean.toString(obsH));
		params.put("obsN", Boolean.toString(obsN));
		params.put("obsC", Boolean.toString(obsC));
		params.put("obsT", Boolean.toString(obsT));
		params.put("obsE", Boolean.toString(obsE));
		params.put("obsB", Boolean.toString(obsB));
		params.put("obsSimpleC", Boolean.toString(simpleC));
		params.put("obsSimpleE", Boolean.toString(simpleE));
		params.put("obsSimpleB", Boolean.toString(simpleB));
		params.put("obsDisease", Boolean.toString(obsDisease));
		params.put("obsDelta", Boolean.toString(obsDelta));
		params.put("obsPerformance", Boolean.toString(obsPerformance));
		params.put("obsMaxExp", Boolean.toString(obsMaxExp));

		params.put("obsExpNoise", Boolean.toString(obsExpNoise));
		params.put("obsInstExp", Boolean.toString(obsInstExp));
		if (random_delta) {
			params.put("random_delta_min", Double.toString(random_delta_min));
			params.put("random_delta_max", Double.toString(random_delta_max));
		}
		if (random_eta) {
			params.put("random_eta_min", Double.toString(random_eta_min));
			params.put("random_eta_max", Double.toString(random_eta_max));
		}
		if (random_rho) {
			params.put("random_rho_min", Double.toString(random_rho_min));
			params.put("random_rho_max", Double.toString(random_rho_max));
		}

		JSONObject response = new JSONObject(params);
		return (response.toString());
	}

	/**
	 * Steps the simulation schedule continuously until the current step count reaches
	 * the specified step count parameter (varsigma). Finalizes observations at teardown.
	 */
	public void runSimulation() {
		do {
			if (!simulation.schedule.step(simulation)) {
				System.out.println("Unknown problem when calling schedule.step");
				break;
			}
		} while (simulation.schedule.getSteps() < simulation.getvarsigma());
		simulation.finish();
		simulation.kill();
	}

	/**
	 * Retrieves the Care simulation instance.
	 *
	 * @return The active Care simulation state object.
	 */
	public Care getSimulation() {
		return (simulation);
	}
}
