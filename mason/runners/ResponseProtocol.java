package runners;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.stream.IntStream;

public class ResponseProtocol {
    private static final int WAITING = 0;
    private static final int PARAMCHECK = 1;
    private static final int CHUNKING = 2;
    private static final int DISCONECTING = 3;
  
    private int status = WAITING;
    
    String results;
	String params;
	RunWithParams run;
	JSONResponse resutlsFetcher;

	public String comunicate(String com) {
		if (com != null && com.startsWith("[")) {
			return executeBatch(com);
		}
		if(com.equals("NextCall")) {
			status = WAITING;
			return "Done";
		}
		if(status == WAITING) {
			status = PARAMCHECK;
			params = com;
			run = new RunWithParams(params);
			//run.simulation.start();
			// comunicates what parameter values are used by the simulation
			return run.getParams();
		}
		if(status == PARAMCHECK & (com.equals("OK_params") || com.equals("add_progression"))) {
			status = CHUNKING;
			run.runSimulation(); //runs the simulation
			resutlsFetcher = new JSONResponse(run.getSimulation()); //
			results =  resutlsFetcher.result();
			// comunicates the size of the message to the client
			return String.valueOf(results.length());
		}
		if(status == CHUNKING) {
			return results;
		}
		return "error";
	}

	private String executeBatch(String com) {
		try {
			JSONArray batchArray = new JSONArray(com);
			int length = batchArray.length();
			JSONObject[] resultsList = new JSONObject[length];

			// Run all simulations in parallel using ForkJoinPool
			IntStream.range(0, length).parallel().forEach(i -> {
				try {
					JSONObject runParams = batchArray.getJSONObject(i);
					RunWithParams runInstance = new RunWithParams(runParams.toString());
					runInstance.runSimulation();
					
					JSONResponse responseFetcher = new JSONResponse(runInstance.getSimulation());
					JSONObject resultJson = responseFetcher.results_json;
					
					// Inject resolved parameters (such as the actual random seed used)
					resultJson.put("resolved_params", new JSONObject(runInstance.getParams()));
					
					resultsList[i] = resultJson;
				} catch (Exception e) {
					e.printStackTrace();
					JSONObject errorJson = new JSONObject();
					try {
						errorJson.put("error", e.getMessage() != null ? e.getMessage() : "Unknown simulation error");
					} catch (Exception ex) {
						// ignore
					}
					resultsList[i] = errorJson;
				}
			});

			JSONArray finalResults = new JSONArray();
			for (JSONObject res : resultsList) {
				if (res != null) {
					finalResults.put(res);
				}
			}
			return finalResults.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\": \"Failed to parse or execute batch: " + e.getMessage() + "\"}";
		}
	}
}
