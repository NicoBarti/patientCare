package patientCare;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	public JSONResponse(Care simulation) {
			
			buildResults_json("C", simulation.observer.getC());
			buildResults_json("SimpleC", simulation.observer.getSimpleC());

			buildResults_json("H", simulation.observer.getH());
			
			buildResults_json("E", simulation.observer.getE());
			buildResults_json("SimpleE", simulation.observer.getSimpleE());

			buildResults_json("T", simulation.observer.getT());
			buildResults_json("B", simulation.observer.getB());
			buildResults_json("SimpleB", simulation.observer.getSimpleB());

			
			buildResults_json("N", simulation.observer.getN());

			buildResults_json("windows", simulation.observer.getWindows());
	}
	
	private JSONObject buildResults_json(String name, Object array) {
		results_json.put(name, array);
		return results_json;
	}
	
	
	public String result() {
		return results_json.toString();
	}
}
