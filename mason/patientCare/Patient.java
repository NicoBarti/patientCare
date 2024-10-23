package patientCare;

import sim.engine.*;

public class Patient implements Steppable {

	//Internals to agent:
	protected int[] C; // received care
	protected int d; // risk stratification {1, 2, 3}
	protected double[] H; // health status [0,+]
	protected double[] expectation; // expectation of outcomes for next appointment
	protected double[] T; //treatment effect
	protected int[] B;
	protected int id;
	protected double currentMot;
	
	//Convenience variables
	protected int current_week; //is the step + 1
		
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;

		//disease progression
		biologicalMechanism(care);
		
		//internal agent events
		expectationFormation();
		behaviouralRule(care.k, care.random.nextDouble());
		
		//TESTING, ALWAYS RECEIVE TREATMENT
		//B[current_week] = 1;
		
		//agent action
		if(B[current_week] == 1 & care.doctor.isAvailable()) {
				C[current_week] = 1;
				T[current_week] = care.doctor.interactWithPatient(id, d);
			} else {
				C[current_week] = 0;
				T[current_week] = 0;
				}
		care.allocationRule(C, d, id);
		} 

	
	private void biologicalMechanism(Care care) {	
		// changes H
		//double progress = care.random.nextGaussian() + d/care.weeks;
		int progress = 0;
		if(care.random.nextBoolean((double) d/52)) {
			progress = 1;
		}
		H[current_week] = Math.max(0, Math.min(H[current_week-1] + progress - T[current_week-1], 5));
	}
	
	protected void expectationFormation() {
		//forms the expectation for the next consultation based on previous experience
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = 0.5*expectation[current_week-1] + 0.5;}
		if(B[current_week-1] == 1 & C[current_week-1] == 0) {
				expectation[current_week] = 0.5*expectation[current_week-1] - 0.5;}
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1];}	
	}
	
	protected void behaviouralRule(double k, double ran) {
		// returns false if patient won't seek care
		// returns true if patient will seek care
		/*
		 * currentMot = 1/(1 + Math.exp(-k * (expectation[current_week] +
		 * H[current_week] - 5)));
		 * 
		 * //necesary discontinuities: if(expectation[current_week] == 0) { currentMot =
		 * 0; } if(H[current_week] == 0) { currentMot = 0; }
		 */
		  if(expectation[current_week] == 0 | H[current_week] == 0){
			  currentMot = 0;
		  } else {
			  currentMot = 0.1*expectation[current_week] + 0.1*H[current_week];
		  }
		  
		  
		  
		if(ran < currentMot) {
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
	
}
