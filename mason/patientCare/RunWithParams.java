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
	String PATIENT_INIT = "random";
	String PROVIDER_INIT = "random";
	Boolean obsH;
	Boolean obsN;
	Boolean obsC;
	Boolean obsT;
	Boolean obsE;
	Boolean obsB;
	Boolean configure_pathfinder = false; // configure simulation via pathfinder
	
	public RunWithParams(String par) {
		populateParameters();
		if (seed ==0) {simulation = new Care(System.currentTimeMillis());}
		else {simulation = new Care(seed);} 
		params = new JSONObject(par);
		if (configure_pathfinder) {
			configure_pathfinder(simulation);
		} else {
			regularParamImplementation(simulation);
		}
	}
	
	protected void regularParamImplementation(Care simulation) {
		simulation.setN(N);
		simulation.setvarsigma(varsigma);
		simulation.setW(W);
		simulation.setPATIENT_INIT(PATIENT_INIT);
		simulation.setPROVIDER_INIT(PROVIDER_INIT);
		simulation.setOBS_PERIOD(OBS_PERIOD);
		simulation.observer.obsH = obsH;
		simulation.observer.obsN = obsN;
		simulation.observer.obsC = obsC;
		simulation.observer.obsT = obsT;
		simulation.observer.obsE = obsE;
		simulation.observer.obsB = obsB;

	}
	
	protected void configure_pathfinder(Care simulation){
		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "2", "testing", "true"});
		pathfinder.configureCare(simulation);
		simulation.OBS_PERIOD = OBS_PERIOD;
		simulation.observer.obsH = obsH;
		simulation.observer.obsN = obsN;
		simulation.observer.obsC = obsC;
		simulation.observer.obsT = obsT;
		simulation.observer.obsE = obsE;
		simulation.observer.obsB = obsB;
	}
	
	private void populateParameters() {
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
		}}	
	}
	

	
	public String getParams() {
		if(configure_pathfinder) {
			HashMap<String, String> params = new HashMap();
			params.put("pathfinder_varsigma", Integer.toString(varsigma));
			params.put("pathfinder_seed", Long.toString(seed));
			HashMap configured_params = simulation.getParams();
			JSONObject sub_response = new JSONObject(configured_params);
			params.put("configured_params", sub_response.toString());

			JSONObject response = new JSONObject(params);
			return(response.toString());
		}
		HashMap params = simulation.getParams();
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
