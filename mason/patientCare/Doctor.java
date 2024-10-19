package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Doctor implements Steppable {
	private int A;
	private int Capacity;
	private int[] visitCounter; //an index of patients and number of consultations
	double T;
	private double policy;

	@Override
	public void step(SimState state) {
		openAgenda();
		// TODO Auto-generated method stub

	}

	public void openAgenda() {
		A = Capacity;
	}
	
	public double interactWithPatient(int id, double d, double H) {
		visitCounter[id] += 1;
		T = prescribeTreatment(visitCounter[id], H);
		A = A - 1; 
		
		return(T);
	}
	
	public double prescribeTreatment(int n_visits, double H) {
		//int treatment = (int)Math.min((n_visits/H),2);
		/*
		 * if(H == 0) { return(0); } int treatment = (int)Math.min((int)(n_visits/H),H);
		 * return(treatment);
		 */
		return(1);
	}
	
	public void initializeDoctor(int capacity, int nPatients) {
		Capacity = capacity;
		visitCounter = new int[nPatients];
		policy = 1/3;
	}
	
	public boolean isAvailable() {
		if(A > 0) {return(true);
		} else {return(false);
		}
	}
	
	
	
}
