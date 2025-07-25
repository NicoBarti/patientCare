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
	Boolean configure_pathfinder = false; // configure simulation via pathfinder
	int pathfinder_varsigma;
	long pathfinder_seed;
	int obsPeriod;
	
	public RunWithParams(String par) {
		simulation = new Care(System.currentTimeMillis());
		params = new JSONObject(par);
		populateParameters();
	}
	
	private void populateParameters() {
		//should implement this routine as first saving all params in this class, and then implement
		//in the simulation class after "pupolateParameters" method is conlcuded.
		Iterator<String> keys = params.keys();
		
		//iterate over keys and pass value params
		while(keys.hasNext()) {
		    String key = keys.next();
		    JSONArray a = (JSONArray)params.get(key);
		    
			switch (key) { // adds the parameters to simulation
			case "N":
				simulation.setN(a.getInt(0));
				break;
			case "varsigma":
				simulation.setvarsigma(a.getInt(0));
				break;
			case "W":
				simulation.setW(a.getInt(0));
				break;
			case "PATIENT_INIT":
				simulation.setPATIENT_INIT(a.getString(0) );
				break;
			case "PROVIDER_INIT":
				simulation.setPROVIDER_INIT(a.getString(0));
				break;
			case "OBS_PERIOD":
				simulation.setOBS_PERIOD(a.getInt(0));
				obsPeriod = a.getInt(0);
				break;
			case "pathfinder_varsigma":
				pathfinder_varsigma = a.getInt(0);
				configure_pathfinder = true;
				break;
			case "pathfinder_seed":
				pathfinder_seed = a.getLong(0);
				configure_pathfinder = true;
				break;
		}}
		if(configure_pathfinder) {
			simulation = new Care( Long.valueOf(pathfinder_seed));
			PathFinder pathfinder = new PathFinder(new String[] {
					"varsigma", String.valueOf(pathfinder_varsigma), "TIMES", "2", "testing", "true"});
			pathfinder.configureCare(simulation);
			simulation.OBS_PERIOD = this.obsPeriod;
		}	
	}
	
	public String getParams() {
		if(configure_pathfinder) {
			HashMap<String, String> params = new HashMap();
			params.put("pathfinder_varsigma", Integer.toString(pathfinder_varsigma));
			params.put("pathfinder_seed", Long.toString(pathfinder_seed));
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
