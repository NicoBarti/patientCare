package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare; // counts the number of visits
	protected double S; // the current satisfaction level
	protected double R; // expected Reward from visiting the doctor 
	protected double P; // the probability of seeking care
	protected double[] patientExpDist; // array to save the distribution of expectations
	protected double[] patientSatDist; // array to save the distribution of satisfactions
	protected double[] patientPDist; // array to save the distribution of probabilities of seeking care
	protected double strat; // the severity stratification level of this patient
	protected double progression; // the status-progression of the disease
	protected double[] patientProgress; // an array to save the progression of the disease
	private int received; // internal to keep track of progression due to treatment

	public void initialize(int weeks, double stratInitializer) {
		receivedCare = 0;
		patientExpDist = new double[weeks+1];
		patientSatDist = new double[weeks+1];
		patientPDist = new double[weeks+1];
		patientProgress = new double[weeks+1];
		strat = stratInitializer;
		progression = 0;
		saveToArrays(0);
	}
	
	public void step(SimState state) {
		Care care = (Care) state;
		//no effect of treatment (unless seek and receive treatment)
		received = 0;
		
		// excecute SEEK sub-model
		if (seek(care)) {
			received = care.askAppointment(strat);
			//pass the result of askappointment (Bolean) to experience care
			experienceCare(care, received);
		} 
		
		// excecute PROGRESSION sub-model, it sums effect of treatment
		progress(care);
		
		//save current state of patient
		if(care.schedule.getSteps() < care.weeks+1) {
		saveToArrays((int)care.schedule.getSteps()+1);
		}
	}
	

	
	/// SUB - MODELS
	
	private boolean seek(Care care) {
		//returns true if will seek, false if will not seek care.	
		P = 1/(1 + Math.exp(-care.k*(this.R - this.S - care.m0))); // compute the patient`s seek probability
		if(care.random.nextDouble() < P) { // a random draw from a Bernoully (its CDF) to determine seek behaviour
			return true;
			} else return false;
	}

	private void experienceCare(Care care, int received) {
		if (received == 1) {
			receivedCare += 1;
			R = (R + care.RCpos)/2 + R/2;
		} else {
			R = (R + care.RCneg)/2 + R/2;
		}	
	}
	
	private void progress(Care care) {
		final double prog_t_1 = care.random.nextGaussian()/100 // stochastic term of progression
				+ strat*0.05 - // bias of progression depending on stratum
				care.E*receivedCare*received; // effect of treatment (if received !=0) augmented by continuity
		S = (S - prog_t_1)/5 + 4*S/5; 
		progression = progression + prog_t_1;
	}
	
	
	
	
	/////
	// HELPERS:
	/////
	
	public int getReceivedCare() {
		return receivedCare;
	}
	
	public double getStrat() {
		return strat;
	}
	
	public double getProgression() {
		return progression;
	}
	
	private void saveToArrays(int pos) {
			patientExpDist[pos] = R;
			patientSatDist[pos] = S;
			patientPDist[pos] = P;
			patientProgress[pos] = progression;
	}
	

	
}