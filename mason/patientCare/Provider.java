package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
	private static final long serialVersionUID = 1L;
	
	// state variable
	/**
	 * Array with the number of past visits for each patient with this provider
	 */
	public int[] SumC_p;
	/**
	 * Appointments left during the this time-step
	 */
	public int alpha_w;
	
	//borra:
	Care care;
	
	// control variables
	public double lambda_w;
	public double tau_w;
	public int A_w;
	
	//internals
	protected int w;
	
	//test
	protected Boolean testing = false;
	protected int interact =0;

	@Override
	public void step(SimState state) {
		care = (Care)state;
		alpha_w = A_w; //open agenda
	}

	public double interactWithPatient(int p, double h) {
		SumC_p[p] += 1;
		alpha_w = alpha_w-1;
		if(h == 0) { //this should never happen, patient's don't ask for visit when h ==0. Only here to be consistent with docs.
			return(0);
		}
			return(Math.min(Math.min(lambda_w * SumC_p[p]/h,tau_w), h));
	}
	
	public boolean isAvailable() {		
		if(alpha_w > 0) {return(true);
		} else {return(false);}
	}
	
	public void increaseNmidway(int N_increase) {
		int[] newSumC_p = new int[SumC_p.length+N_increase];
		for (int i = 0; i< SumC_p.length; i++) {
			newSumC_p[i] = SumC_p[i];
		}
		SumC_p = newSumC_p.clone();
	}
}

