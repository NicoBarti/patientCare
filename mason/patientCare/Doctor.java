package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Doctor implements Steppable {
	private int A;
	private int Capacity;
	private int[] visitCounter; //an index of patients and number of consultations
	double T;

	@Override
	public void step(SimState state) {
		openAgenda();
		// TODO Auto-generated method stub

	}

	public void openAgenda() {
		A = Capacity;
	}
	
	public double interactWithPatient(int id, double d) {
		T = prescribeTreatment(visitCounter[id], d);
		visitCounter[id] =+ 1;
		A = A - 1; 
		return(T);
	}
	
	public double prescribeTreatment(int n_visits, double d) {
		return(Math.min(n_visits/d, 1));
	}
	
	public void initializeDoctor(int capacity, int nPatients) {
		Capacity = capacity;
		visitCounter = new int[nPatients];
	}
	
	public boolean isAvailable() {
		if(A > 0) {return(true);
		} else {return(false);
		}
	}
	
	
	
}