package patientCare;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	public JSONResponse(String parList) {
		params = parList;
		Care simulation = new Care(System.currentTimeMillis());
		simulation.start();
		do{
			if (!simulation.schedule.step(simulation)) {
				System.out.println("algo falso en schedule.step");
				break;}
			}
			while (simulation.schedule.getSteps() < simulation.getweeks());
			simulation.finish();

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
