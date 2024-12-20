package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Doctor implements Steppable {
	private int A;
	private int Capacity;
	private int[] visitCounter; //an index of patients and number of consultations
	double T;
	//private double policy;

	@Override
	public void step(SimState state) {
		openAgenda();
	}

	public void openAgenda() {
		A = Capacity;
	}
	

	public double interactWithPatient(int id, double complexity) {
		visitCounter[id] += 1;
		T = prescribeTreatment(visitCounter[id], complexity);
		A = A - 1; 
		//conterfactual check: no complexity on treatments:
		//return(1);
		return(T);
	}
	

	public double prescribeTreatment(int n_visits, double complexity) {
		//return(1);
		return(Math.min(n_visits/(2*complexity), 1));
	}
	
	public void initializeDoctor(int capacity, int nPatients) {
		Capacity = capacity;
		visitCounter = new int[nPatients];
		//policy = 1/3;
	}
	
	public boolean isAvailable() {
		if(A > 0) {return(true);
		} else {return(false);
		}
	}
		
}