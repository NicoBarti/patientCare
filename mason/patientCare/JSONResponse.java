package patientCare;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	public JSONResponse(Care simulation, String fetchType) {
			
			//buildResults_json("d", simulation.getds());
			buildResults_json("C", simulation.observer.getC());
			buildResults_json("H", simulation.observer.getH());
			buildResults_json("E", simulation.observer.getE());
			buildResults_json("T", simulation.observer.getT());
			buildResults_json("B", simulation.observer.getB());
			buildResults_json("N", simulation.observer.getN());

			buildResults_json("windows", simulation.observer.getWindows());

			
			//if (fetchType.equals("add_progression")) {
			//buildResults_json("P", simulation.gettotalProgress());}
	}
	
	private JSONObject buildResults_json(String name, Object array) {
		results_json.put(name, array);
		return results_json;
	}
	
	
	public String result() {
		return results_json.toString();
	}
}
