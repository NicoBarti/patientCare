package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	//Internals to agent:
	private int[] C; // received care
	protected int d; // risk stratification {1, 2, 3}
	private double[] H; // health status [0,+]
	private double[] expectation; // expectation of outcomes for next appointment
	private double[] T; //treatment effect
	private int[] B;
	
	//Convenience variables
	private int current_week; //is the step + 1
	
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;
		} 

	//First mechanism to be activated
	private void biologicalMechanism(Care care) {	
		// changes H
		double progress = care.random.nextDouble()/d;
		H[current_week] = H[current_week-1] + progress - T[current_week-1];
	}
	
	//Second mechanism to be activated
	private void expectationFormation(boolean careReceived) {
		//forms the expectation for the next consultation based on previous experience
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = 0.5*expectation[current_week-1] + 0.5;}
		if(B[current_week-1] == 1 & C[current_week-1] == 0) {
				expectation[current_week] = 0.5*expectation[current_week-1] - 0.5;}
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1];}	
	}
	
	//Third mechanism to be activated
	private void behaviouralRule(Care care) {
		// returns false if patient won't seek care
		// returns true if patient will seek care
		double m = 1/(1 + Math.exp(-care.k*(expectation[current_week] + H[current_week])));
		if(care.random.nextDouble() < m) {
			B[current_week] = 1;
		} else {
			B[current_week] = 0;
		}	
	}
	
	public void initializePatient(double severity, int weeks) {
		if(severity < 0.33) {d = 1;} 
		else if(severity < 0.66) {d = 2;}
		else {d = 3;}
		
		H = new double[weeks+1];
			H[0] = 0;
		expectation = new double[weeks+1];
			expectation[0] = 0.5;
		T = new double[weeks+1];
			T[0] = 0;
		C = new int[weeks+1];
			C[0] = 0;
		B = new int[weeks+1];
			B[0] = 0;
	}
	

	public int[] getReceivedCare() {
		return C;
	}
	
	public int getD() {
		return d;
	}
	
}