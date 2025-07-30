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
	
	public RunWithParams(String par) {
		params = new JSONObject(par);
		readParameters();
		if (seed ==0) {simulation = new Care(System.currentTimeMillis());}
		else {simulation = new Care(seed);} 
		
		if (configure_pathfinder) {
			configure_pathfinder();
		} else {
			regularParamImplementation();
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
		}}	
	}
	
	
	public String getParams() {

		HashMap params = simulation.getParams();
		params.put("pathfinder", Boolean.toString(configure_pathfinder));
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
