package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
	
	// state variable
	public int[][] C_w;
	public int alpha_w;
	
	// control variables
	public double lambda_w;
	public double tau_w;
	public int A_w;
	
	//internals
	private int thisweek;
	private int Ccounter;


	@Override
	public void step(SimState state) {
		thisweek = (int)state.schedule.getSteps();
		alpha_w = A_w; //open agenda
	}

	public double interactWithPatient(int id, double h) {
		C_w[id][thisweek] = 1;
		alpha_w = alpha_w-1;
		if(h == 0) { //this should never happen, patient's don't ask for visit when h ==0. Only here to be consistent with docs.
			return(0);
		}
		Ccounter = 0;
		for(int i=0; i<C_w[id].length; i++) {
			Ccounter += C_w[id][i]; }
		return(Math.min(Math.min(lambda_w * Ccounter/h,tau_w), h));
	}
	
	public boolean isAvailable() {		
		if(alpha_w > 0) {return(true);
		} else {return(false);}
	}
	
			
}