package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Doctor implements Steppable {
	private int A;
	private int Capacity;
	private int[] visitCounter; //an index of patients and number of consultations
	double T;
	double t; //max treatment
	double LEARNING_RATE;
	//private double policy;

	@Override
	public void step(SimState state) {
		openAgenda();
	}

	public void openAgenda() {
		A = Capacity;
	}
	

	public double interactWithPatient(int id, double needs) {
		visitCounter[id] += 1;
		T = prescribeTreatment(visitCounter[id], needs);
		A = A - 1; 
		//conterfactual check: no complexity on treatments:
		//return(1);
		return(T);
	}
	

	public double prescribeTreatment(int n_visits, double needs) {
		//return(1);
		// First compute the maximum learned treatment, bounded by needs and by t
		//double learned_treatment = Math.min(Math.min(n_visits/(3*needs), t), needs);
		// Then compute the treatment mix using the learning parameter
		//System.out.println(LEARNING_RATE);
		//double treatment_mix = (LEARNING_RATE * learned_treatment) + ((1-LEARNING_RATE) * t);
		double treatment = Math.min(Math.min(LEARNING_RATE * n_visits/needs,t), needs);
		return(treatment);
	}
	
	public void initializeDoctor(int capacity, int nPatients, double tto, double RATE) {
		Capacity = capacity;
		visitCounter = new int[nPatients];
		t = tto;
		LEARNING_RATE = RATE;
		//policy = 1/3;
	}
	
	public boolean isAvailable() {		
		if(A > 0) {return(true);
		} else {return(false);}
	}
		
}