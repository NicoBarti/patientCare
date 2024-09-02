package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare; // counts the number of visits
	protected double S; // the current satisfaction level
	protected double R; // expected Reward from visiting the doctor 
	protected int C; // the maximum number of consultations a patient can make in a period
	protected double P; // the probability of seeking care
	protected double[] patientExpDist; 
	protected double[] patientSatDist; 
	protected double[] patientPDist;
	protected double strat;

	public void initialize(int weeks) {
		receivedCare = 0;
		patientExpDist = new double[weeks];
		patientSatDist = new double[weeks];
		patientPDist = new double[weeks];
	}
	
	public void step(SimState state) {
		Care care = (Care) state;
		//save current motivation and severity
		if(care.schedule.getSteps() < care.weeks+1) {
			patientExpDist[(int)care.schedule.getSteps()] = R;
			patientSatDist[(int)care.schedule.getSteps()] = S;
			patientPDist[(int)care.schedule.getSteps()] = P;
		} 
		// excecute PROGRESSION sub-model
		progress(care);
		//if(care.schedule.getSteps() == 0) { // save initial conditions for output
		//	patientExpDist[(int)care.schedule.getSteps()] = R;
		//	patientSatDist[(int)care.schedule.getSteps()] = S;
		//}
		// excecute SEEK sub-model
		if (seek(care)) {
			// ask for an appoitnment and EXPERIENCE CARE:
			if(care.askAppointment()){
				// if received care excecute RECEIVE CARE sub-model
				receiveCare(care);
			} else { // else, experience negative care
				notCare(care); 
				
			};

		} 


		//else {System.out.println("52 weeks reached, patients are not registering their severity and motivation any longer");}
	}


	private boolean seek(Care care) {
		//returns true if will seek, false if will not seek care.
		if (this.receivedCare > this.C) {
			return false;
		}
		// compute the patient`s seek probability	
		P = 1/(1 + Math.exp(-care.k*(this.R - this.S - care.m0)));
		// a random draw from a Bernoully (its CDF) to determine seek behaviour
		if(care.random.nextDouble() < P) {
			return true;
			//if (care.askAppointment()) {
			//	return true;
			} else return false;
		//} return false;
	}

	private void receiveCare(Care care) {
		receivedCare += 1;
		//this.S += care.E;
		this.S = (this.S + care.E)/2 + this.S/2;
		//this.R = care.RCpos/2 + this.R/2;
		this.R = (this.R + care.RCpos)/2 + this.R/2;
	}
	
	private void notCare(Care care) {
		//this.R = care.RCneg/2 + this.R/2;
		this.R = (this.R + care.RCneg)/2 + this.R/2;
	}

	public int getReceivedCare() {
		return receivedCare;
	}
	
private void progress(Care care) {
		//this.severity += care.random.nextGaussian() * 0.01;
		//this.motivation += care.random.nextGaussian() * 0.01;
		this.S = (this.S - this.strat*0.05)/2 + this.S/2;
		//this.S = care.random.nextGaussian() + S_0; // = sample from ~N(S_0,1)
	}
	
	//public double getCurrentMotivation() {
	//	return P;
	//}
	
	//public double getCurrentSeverity(){
	//	return severity;
	//}

}