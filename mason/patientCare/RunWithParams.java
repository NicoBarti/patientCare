package patientCare;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	
	String PATIENT_INIT = "random";
	String PROVIDER_INIT = "random";
	Boolean obsH= false;
	Boolean obsN= false;
	Boolean obsC= false;
	Boolean obsT= false;
	Boolean obsE= false;
	Boolean obsB= false;
	Boolean simpleC = false;
	Boolean simpleE = false;
	Boolean simpleB = false;
	Boolean configure_pathfinder = false; // configure simulation via pathfinder
	Boolean reproduce_line = false; // configure reproduce line
	
	//Patient initializers
	double fixed_delta;
	double fixed_capN;
	double fixed_rho;
	double fixed_eta;
	float fixed_kappa;
	double fixed_capE;
	double fixed_psi;
	
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
		simulation.startObserver(obsH, obsN, obsC, obsT, obsE, obsB, 
				simpleC, simpleE, simpleB);
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
				"varsigma", String.valueOf(varsigma), "TIMES", "2", "testing", "true"});
		pathfinder.configureCare(simulation);
		simulation.setOBS_PERIOD(OBS_PERIOD);
	}
	
	protected void reproduceLine() {
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.setvarsigma(varsigma);
		simulation.totalCapacity = totalCapacity;
		simulation.W = W;
		simulation.N = N;
		simulation.pat_init = new PatientInitializer(simulation, "default");
				simulation.pat_init.fixed_delta = fixed_delta;
				simulation.pat_init.fixed_capN = fixed_capN;
				simulation.pat_init.fixed_lambda = fixed_lambda;
				simulation.pat_init.fixed_tau = fixed_tau;
				simulation.pat_init.fixed_rho = fixed_rho;
				simulation.pat_init.fixed_eta = fixed_eta;
				simulation.pat_init.fixed_kappa = fixed_kappa;
				simulation.pat_init.fixed_capE = fixed_capE;
				simulation.pat_init.fixed_psi = fixed_psi;
				
		simulation.prov_init = new ProviderInitializer(simulation, "default");
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
		}}	
	}
	
	
	public String getParams() {

		HashMap params = simulation.getParams();
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
