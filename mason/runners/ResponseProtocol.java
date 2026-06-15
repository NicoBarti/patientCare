package runners;

import org.json.JSONArray;
import org.json.JSONObject;

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
			JSONObject[] responseObjects = new JSONObject[length];

			java.util.stream.IntStream.range(0, length).parallel().forEach(i -> {
				try {
					JSONObject singleParams = batchArray.getJSONObject(i);
					RunWithParams singleRun = new RunWithParams(singleParams.toString());
					singleRun.runSimulation();
					JSONResponse singleFetcher = new JSONResponse(singleRun.getSimulation());
					JSONObject singleResults = singleFetcher.results_json;
					
					singleResults.put("resolved_params", new JSONObject(singleRun.getParams()));
					responseObjects[i] = singleResults;
				} catch (Exception e) {
					e.printStackTrace();
					JSONObject errorObj = new JSONObject();
					try {
						errorObj.put("error", e.getMessage() != null ? e.getMessage() : "Unknown simulation error");
					} catch (Exception ex) {
						// ignore
					}
					responseObjects[i] = errorObj;
				}
			});

			JSONArray resultsArray = new JSONArray();
			for (JSONObject obj : responseObjects) {
				if (obj != null) {
					resultsArray.put(obj);
				}
			}
			return resultsArray.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}
}
