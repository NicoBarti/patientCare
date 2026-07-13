package runners;

import org.json.JSONObject;

import patientCare.Care;

import java.util.HashMap;
import java.util.Map;

public class JSONResponse {
	String params;
	JSONObject results_json = new JSONObject();
	
	/** Builds a JSON output of the simulation
	 * @param simulation
	 */
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
			
			buildResults_json("Disease", simulation.observer.getDisease());
			buildResults_json("ExpNoise", simulation.observer.getExpNoise());
			buildResults_json("InstExp", simulation.observer.getInstExp());
			
			buildResults_json("Delta", simulation.observer.getDelta());
			buildResults_json("Rho", simulation.observer.getRho());
			buildResults_json("Eta", simulation.observer.getEta());
			buildResults_json("Performance", simulation.observer.getPerformance());
			buildResults_json("MaxExp", simulation.observer.getMaxExp());
			
			if (simulation.obsStepPerformance) {
				buildResults_json("stepPerformance", simulation.observer.getStepPerformance());
				double[] capacity = simulation.observer.getStepCapacity();
				double[] slice = new double[simulation.getvarsigma()];
				System.arraycopy(capacity, 0, slice, 0, simulation.getvarsigma());
				buildResults_json("stepCapacity", slice);
			}
	}
	
	private JSONObject buildResults_json(String name, Object array) {
		results_json.put(name, array);
		return results_json;
	}
	
	
	public String result() {
		return results_json.toString();
	}
}
