package patientCare;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	public JSONResponse(Care simulation, String fetchType) {
			
			buildResults_json("d", simulation.getds());
			buildResults_json("C", simulation.getCs());
			buildResults_json("H", simulation.getHs());
			buildResults_json("exp", simulation.getexpectations());
			buildResults_json("T", simulation.getTs());
			buildResults_json("B", simulation.getBs());
			
			if (fetchType == "add_progression") {
			buildResults_json("P", simulation.gettotalProgress());}
	}
	
	private JSONObject buildResults_json(String name, Object array) {
		results_json.put(name, array);
		return results_json;
	}
	
	
	public String result() {
		return results_json.toString();
	}
}
