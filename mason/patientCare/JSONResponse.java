package patientCare;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	public JSONResponse(Care simulation) {

			buildResults_json("Ds", simulation.getds());
			buildResults_json("Cs", simulation.getCs());
			buildResults_json("Hs", simulation.getHs());
			buildResults_json("Es", simulation.getexpectations());
			buildResults_json("Ts", simulation.getTs());
			buildResults_json("Bs", simulation.getBs());
	}
	
	private JSONObject buildResults_json(String name, Object array) {
		results_json.put(name, array);
		return results_json;
	}
	
	
	public String result() {
		return results_json.toString();
	}
	
}
