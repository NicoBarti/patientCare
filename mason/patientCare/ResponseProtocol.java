package patientCare;

public class ResponseProtocol {
    private static final int WAITING = 0;
    private static final int PARAMCHECK = 1;
    private static final int CHUNKING = 2;
    private static final int DISCONECTING = 3;
  
    private int status = WAITING;
    
    String results;
	String params;
	
	public String comunicate(String com) {
		if(status == WAITING) {
			status = PARAMCHECK;
			System.out.println(com);
			//TODO call simulation from here?
			RunWithParams run = new RunWithParams(com);			
			JSONResponse resutlsFetcher = new JSONResponse(com);
			results =  resutlsFetcher.result();
			// comunicates what parameter values were used by the simulation
			return run.getParams();
		}
		if(status == PARAMCHECK & com.equals("OK_params")) {
			//TODO check params
			status = CHUNKING;
			// comunicates the size of the message to the client
			return String.valueOf(results.toString().length());
		}
		if(status == CHUNKING) {
			String response = results;
			return response;
		}
	return "error";
	}
}
