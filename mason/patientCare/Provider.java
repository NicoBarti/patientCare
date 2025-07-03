package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
	
	// state variable
	public int[][] C;
	
	// control variables
	public int alpha;
	public double lambda;
	public double tau;
	public int capacity;
	
	//internals
	private int thisweek;
	private int Ccounter;


	@Override
	public void step(SimState state) {
		thisweek = (int)state.schedule.getSteps();
		alpha = capacity; //open agenda
	}

	public double interactWithPatient(int id, double h) {
		C[id][thisweek] = 1;
		alpha = alpha-1;
		if(h == 0) { //this should never happen, patient's don't ask for visit when h ==0. Only here to be consistent with docs.
			return(0);
		}
		Ccounter = 0;
		for(int i=0; i<C[id].length; i++) {
			Ccounter += C[id][i]; }
		return(Math.min(Math.min(lambda * Ccounter/h,tau), h));
	}
	
	public boolean isAvailable() {		
		if(alpha > 0) {return(true);
		} else {return(false);}
	}
	
			
}