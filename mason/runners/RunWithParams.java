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
 * 
 */
public class RunWithParams {
	Care simulation;
	JSONObject params;

	// internals
	//int pathfinder_varsigma;
	long seed;
	int OBS_PERIOD = 0;
	int N = 1000;
	int varsigma = 150;
	int W = 2;
	int totalCapacity;
	
	String PATIENT_INIT = "default";
	String PROVIDER_INIT = "default";
	String pi = "basal";
	boolean obsH= false;
	boolean obsN= false;
	boolean obsC= false;
	boolean obsT= false;
	boolean obsE= false;
	boolean obsB= false;
	boolean simpleC = false;
	boolean simpleE = false;
	boolean simpleB = false;
	boolean configure_pathfinder = false; // configure simulation via pathfinder
	boolean reproduce_line = false; // configure reproduce line
	boolean obsDisease = false;
	boolean obsDelta = false;
	boolean obsExpNoise = false;
	boolean obsInstExp = false;
	boolean obsPerformance = false;
	
	//Patient initializers
	double fixed_delta;
	double fixed_capN;
	double fixed_rho;
	double fixed_eta;
	float fixed_kappa;
	double fixed_capE;
	double fixed_psi;
	
	private boolean random_delta = false;
	/** Only specify if want random delta among patients
	 * The minimum delta
	 */
	double random_delta_min;
	
	/** Only specify if want random delta among patients
	 * The maximum delta
	 */
	double random_delta_max;
	
	/** For fixing h at the bgining to all patients
	 * True for fixing the initial value
	 */
	
	private boolean initial_h = false;
	
	/** The inivial value of h
	 * 
	 */
	public double h0_value = 0;
	
	//Provider initializers
	double fixed_lambda;
	double fixed_tau;
	
	public RunWithParams(String par) {
		params = new JSONObject(par);
		readParameters();
		if (seed ==0) {simulation = new Care(System.currentTimeMillis());}
		else {simulation = new Care(seed);
		System.out.println("JAVA (RunWithParams.java) started with seed "+seed);} 
		
		if (configure_pathfinder) {
			System.out.println("JAVA (RunWithParams.java) going to use configure_pathfinder");

			configure_pathfinder();
		} else {
			if(reproduce_line) {
				System.out.println("JAVA (RunWithParams.java) going to use reproduceLine");
				reproduceLine();
			}
			else {
			regularParamImplementation();
			System.out.println("JAVA (RunWithParams.java) going to use regularParamImplementation");
}
		}
		simulation.start();
		//override custom observer: (do this safetely after start has set the
		//defenitive N and W
//		if(PATIENT_INIT.equals("classExample")) {
//			Boolean delta = true;
//			simulation.startObserver(obsH, obsN, obsC, obsT, obsE, obsB, 
//					simpleC, simpleE, simpleB, delta);
//		} else {
		simulation.startObserver(obsH, obsN, obsC, obsT, obsE, obsB, 
				simpleC, simpleE, simpleB, obsDisease, obsExpNoise, obsInstExp, obsDelta, obsPerformance);
		//}
	}
	
	protected void regularParamImplementation() {
		simulation.setN(N);
		simulation.setvarsigma(varsigma);
		simulation.setW(W);
		simulation.setPATIENT_INIT(PATIENT_INIT);
		simulation.setPROVIDER_INIT(PROVIDER_INIT);
		simulation.setOBS_PERIOD(OBS_PERIOD);
	}
	
	protected void configure_pathfinder(){
		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "testing", "true"});
		pathfinder.configureCare(simulation);
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setPi(pi); //this allows to change a pathfinder line's pi (and we could add other params)
	}
	
	protected void reproduceLine() {
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setvarsigma(varsigma);
		simulation.settotalCapacity(totalCapacity);
		simulation.W = W;
		simulation.N = N;
		simulation.setPi(pi);
		
		simulation.pat_init = new PatientInitializer(simulation, "applyFixed");
				simulation.setPATIENT_INIT("applyFixed"); //for params comparison before simulation compatibility
				simulation.pat_init.fixed_delta = fixed_delta;
				simulation.pat_init.fixed_capN = fixed_capN;
				simulation.pat_init.fixed_lambda = fixed_lambda;
				simulation.pat_init.fixed_tau = fixed_tau;
				simulation.pat_init.fixed_rho = fixed_rho;
				simulation.pat_init.fixed_eta = fixed_eta;
				simulation.pat_init.fixed_kappa = fixed_kappa;
				simulation.pat_init.fixed_capE = fixed_capE;
				simulation.pat_init.fixed_psi = fixed_psi;
				if(initial_h) { 
					simulation.pat_init.initial_h = true;
					simulation.pat_init.h0_value= h0_value;}
				if(random_delta) {
					simulation.pat_init.random_delta = true;
					simulation.pat_init.random_delta_min = random_delta_min;
					simulation.pat_init.random_delta_max = random_delta_max;}
				
		simulation.prov_init = new ProviderInitializer(simulation, "applyFixed");
				simulation.setPROVIDER_INIT("applyFixed");
				simulation.prov_init.fixed_lambda = fixed_lambda;
				simulation.prov_init.fixed_tau = fixed_tau;
	}
	
	private void readParameters() {
		Iterator<String> keys = params.keys();
		
		//iterate over keys and pass value params
		while(keys.hasNext()) {
		    String key = keys.next();
		    JSONArray a = (JSONArray)params.get(key);
		    
			switch (key) { // adds the parameters to simulation
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
			case"fixed_psi":
				fixed_psi = a.getDouble(0);
				break;
			case "totalCapacity":
				totalCapacity = a.getInt(0);
				break;
			case "Pi":
				pi = a.getString(0);
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
			case "obsPerformance":
				obsPerformance = true;
				break;
			
		}}	
	}
	
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

		params.put("obsExpNoise", Boolean.toString(obsExpNoise));
		params.put("obsInstExp", Boolean.toString(obsInstExp));
		if(random_delta) {
			params.put("random_delta_min", Double.toString(random_delta_min));
			params.put("random_delta_max", Double.toString(random_delta_max));
		}
		

		JSONObject response = new JSONObject(params);
		return(response.toString());
	}
	
	public void runSimulation() {
		do{
			if (!simulation.schedule.step(simulation)) {
				System.out.println("Unknown problem when calling schedule.step");
				break;}
		}
		while (simulation.schedule.getSteps() < simulation.getvarsigma());
		simulation.finish();
		simulation.kill();
	}
	
	public Care getSimulation() {
		return(simulation);
	}
}
