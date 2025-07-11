package patientCare;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RunWithParams {
	Care simulation;
	JSONObject params;

	public RunWithParams(String par) {
		simulation = new Care(System.currentTimeMillis());
		params = new JSONObject(par);
		populateParameters();
	}
	
	private void populateParameters() {
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
				break;
		}}
	}
	
	public String getParams() {
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
