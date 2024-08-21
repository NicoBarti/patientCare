package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare = 0; // counts the number of visits
	protected double S; // the current satisfaction level
	protected double R; // expected Reward from visiting the doctor 
	protected int C; // the maximum number of consultations a patient can make in a period
	protected double P; // the probability of seeking care
	protected double[] patientExpDist = new double[53];
	protected double[] patientSatDist = new double[53];

	public void step(SimState state) {
		Care care = (Care) state;
		// excecute PROGRESSION sub-model
		//progress(care);

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
		
		//save current motivation and severity
		if(care.schedule.getSteps() < 53) {
			patientExpDist[(int)care.schedule.getSteps()] = R;
			patientSatDist[(int)care.schedule.getSteps()] = S;
		} 
		//else {System.out.println("52 weeks reached, patients are not registering their severity and motivation any longer");}
	}


	private boolean seek(Care care) {
		//returns true if will seek, false if will not seek care.
		if (this.receivedCare > this.C) {
			return false;
		}
		// compute the patient`s seek probability	
		P = 1/(1 + Math.exp(this.R - this.S));
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
		this.S += care.E;
		this.R = care.RCpos/2 + this.R/2;
	}
	
	private void notCare(Care care) {
		this.R = care.RCneg/2 + this.R/2;
	}

	public int getReceivedCare() {
		return receivedCare;
	}
	
//	private void progress(Care care) {
		//this.severity += care.random.nextGaussian() * 0.01;
		//this.motivation += care.random.nextGaussian() * 0.01;
		//this.S = care.random.nextGaussian() + S_0; // = sample from ~N(S_0,1)
//	}
	
	//public double getCurrentMotivation() {
	//	return P;
	//}
	
	//public double getCurrentSeverity(){
	//	return severity;
	//}

}