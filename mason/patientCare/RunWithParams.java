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
			case "capacity":
				simulation.setCapacity(a.getInt(0));
				break;
			case "numPatients":
				simulation.setNumPatients(a.getInt(0));
				break;
			case "k":
				simulation.setk(a.getDouble(0));
				break;
			case "weeks":
				simulation.setweeks(a.getInt(0));
				break;
			case "CONTINUITY_ALLOCATION":
				simulation.setCONTINUITY_ALLOCATION(a.getDouble(0));
				break;
			case "SEVERITY_ALLOCATION":
				simulation.setSEVERITY_ALLOCATION(a.getDouble(0));
				break;
			case "DISEASE_VELOCITY":
				simulation.setDISEASE_VELOCITY(a.getDouble(0));
				break;
			case "LEARNING_RATE":
				simulation.setLEARNING_RATE(a.getDouble(0));
				break;
			case "SUBJECTIVE_INITIATIVE":
				simulation.setSUBJECTIVE_INITIATIVE(a.getDouble(0));
				break;

		}}
	}
	
	public String getParams() {
		HashMap params = simulation.getParams();
		JSONObject response = new JSONObject(params);
		return(response.toString());
	}
	
	public void runSimulation() {
		simulation.start();
		do{
			if (!simulation.schedule.step(simulation)) {
				System.out.println("algo falso en schedule.step");
				break;}
		}
		while (simulation.schedule.getSteps() < simulation.getweeks());
		simulation.finish();
	}
	
	public Care getSimulation() {
		return(simulation);
	}
}
