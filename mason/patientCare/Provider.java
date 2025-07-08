package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
	
	// state variable
	public int[] SumC_w;
	public int alpha_w;
	
	// control variables
	public double lambda_w;
	public double tau_w;
	public int A_w;
	
	//internals
	protected int w;
	
	//test
	protected Boolean testing = false;


	@Override
	public void step(SimState state) {
		alpha_w = A_w; //open agenda
		if(testing) {System.out.println("alpha_w" + alpha_w +" in w "+w);}
	}

	public double interactWithPatient(int id, double h) {
		SumC_w[id] += 1;
		alpha_w = alpha_w-1;
		if(h == 0) { //this should never happen, patient's don't ask for visit when h ==0. Only here to be consistent with docs.
			return(0);
		}
			return(Math.min(Math.min(lambda_w * SumC_w[id]/h,tau_w), h));
	}
	
	public boolean isAvailable() {		
		if(alpha_w > 0) {return(true);
		} else {return(false);}
	}
}