package patientCare;

import sim.engine.*;

public class Patient implements Steppable {

	//Internals to agent:
	protected int[] C; // received care
	protected int d; // risk stratification {1, 2, 3}
	protected double[] H; // health status [0,+]
	protected double[] expectation; // expectation of outcomes for next appointment
	private double[] T; //treatment effect
	private int[] B;
	protected int id;
	private double currentMot;
	
	//Convenience variables
	private int current_week; //is the step + 1
	
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;

		//disease progression
		biologicalMechanism(care);
		
		//test treatment efficacy at different needs
		//testTreatmentMec();
		
		//internal agent events
		expectationFormation();
		behaviouralRule(care);
		
		//agent action
		if(B[current_week] == 1 & care.doctor.isAvailable()) { 
			C[current_week] = 1;
		    T[current_week] = care.doctor.interactWithPatient(id, d, H[current_week]); 
		}
		  else { 
			  C[current_week] = 0; 
			  T[current_week] = T[current_week-1]*0.95; 
		  }
		 
		care.allocationRule(C, d, id);
		} 

	
	private void biologicalMechanism(Care care) {	
		// changes H
		int progress = 0;
		if(care.random.nextBoolean((double)2*d/(care.weeks))){
			progress = 1;
		}
		H[current_week] = Math.min(5,Math.max(0,H[current_week-1] + progress - T[current_week-1]));
	}
	
	private void expectationFormation() {
		//forms the expectation for the next consultation based on previous experience
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = 0.5*expectation[current_week-1] + 0.5;}
		if(B[current_week-1] == 1 & C[current_week-1] == 0) {
				expectation[current_week] = 0.5*expectation[current_week-1] - 0.5;}
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1];}	
	}
	
	private void behaviouralRule(Care care) {
		// returns false if patient won't seek care
		// returns true if patient will seek care
		currentMot = 1/(1 + Math.exp(-care.k*(expectation[current_week] + H[current_week])));
		if(care.random.nextDouble() < currentMot) {
			B[current_week] = 1;
		} else {
			B[current_week] = 0;
		}	
	}
	

	public void initializePatient(double severity, int weeks, int i) {
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
		id = i;
//		if(id == 0) {
//			System.out.println(d);
//		}
		//System.out.println(id);

	}
	

	public int[] getReceivedCare() {return C;}
	public int getd(){ return d;}
	public double[] getH() {return H;}
	public double[] getexpectation() {return expectation;}
	public double[] getT() {return T;}
	public int[] getB() {return B;}
	public int getTotalCare() {
		int total = 0;
		for(int i =0; i < C.length ; i++) {
			total = total + C[i];
		}
		return total;
	}
	public double getcurrentMot() {return currentMot;}
	
	public void testTreatmentMec(){
		H[current_week] = id;
		//System.out.println(id);
	}
	
}
