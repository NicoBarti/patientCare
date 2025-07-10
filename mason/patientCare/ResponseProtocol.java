package patientCare;

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
		if(com.equals("NextCall")) {
			status = WAITING;
			return "Done";
		}
		if(status == WAITING) {
			status = PARAMCHECK;
			params = com;
			run = new RunWithParams(params);	
			run.simulation.start();
			// comunicates what parameter values were used by the simulation
			return run.getParams();
		}
		if(status == PARAMCHECK & (com.equals("OK_params") || com.equals("add_progression"))) {
			status = CHUNKING;
			run.runSimulation(); //runs the simulation
			resutlsFetcher = new JSONResponse(run.getSimulation(), com); //
			results =  resutlsFetcher.result();
			// comunicates the size of the message to the client
			return String.valueOf(results.length());
		}
		if(status == CHUNKING) {
			return results;
		}
	return "error";
	}
}
