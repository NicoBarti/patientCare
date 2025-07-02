package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Provider implements Steppable {
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
		return(T);
	}
	

	public double prescribeTreatment(int n_visits, double needs) {
		if(needs == 0) {
			return(0);
		}
		double treatment = Math.min(Math.min(LEARNING_RATE * n_visits/needs,t), needs);
		return(treatment);
	}
	
	public void initializeDoctor(int capacity, int nPatients, double tto, double RATE) {
		Capacity = capacity;
		visitCounter = new int[nPatients];
		t = tto;
		LEARNING_RATE = RATE;
	}
	
	public boolean isAvailable() {		
		if(A > 0) {return(true);
		} else {return(false);}
	}
		
}