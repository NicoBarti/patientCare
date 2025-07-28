package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
	
	// state variable
	public int[] SumC_w;
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
		//System.out.println("Provider "+w+" interact "+interact);
		alpha_w = A_w; //open agenda
		//if(interact == 1) {
		//System.out.println("THIS IS MY COUNTING FOR STEP "+state.schedule.getSteps());
		//System.out.println("PATIENT 0 "+SumC_w[0]);}
		//at timestep 119 there is +1
	}

	public double interactWithPatient(int p, double h) {
//if(testing & p==9) {System.out.print("\n / previous SumC_w"+" patient "+p+": "+SumC_w[p]);}
		SumC_w[p] += 1;
		alpha_w = alpha_w-1;
//if(testing & p==9) {System.out.print("post SumC_w[9] :"+SumC_w[p]+ " / ");}
		if(h == 0) { //this should never happen, patient's don't ask for visit when h ==0. Only here to be consistent with docs.
			return(0);
		}
			return(Math.min(Math.min(lambda_w * SumC_w[p]/h,tau_w), h));
	}
	
	public boolean isAvailable() {		
		if(alpha_w > 0) {return(true);
		} else {return(false);}
	}
}