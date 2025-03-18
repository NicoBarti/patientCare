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
	private float progressProbability;
	
	protected int current_week; //is the step + 1
		
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;

		//disease progression
		biologicalMechanism(care);
		
		//internal agent events
		expectationFormation(care);
		behaviouralRule(care.random.nextDouble(), care.SUBJECTIVE_INITIATIVE);
		
		//agent action
		if(B[current_week] == 1 & care.doctor.isAvailable()) { 
			care.totalInteractions = care.totalInteractions +1;
				C[current_week] = 1;
				T[current_week] = care.doctor.interactWithPatient(id, H[current_week]);
			} else {
				C[current_week] = 0;
				T[current_week] = 0;
				}
		care.allocationRule(C, d, id); //scheddule patient for the next week according to the rules
		} 

	
	protected void biologicalMechanism(Care care) {	
		// changes H
		int progress = 0;
		if(care.random.nextBoolean(progressProbability)) {
			progress = 1;
			care.totalProgress = care.totalProgress + 1;
		}
		H[current_week] = Math.max(0, Math.min(H[current_week-1] + progress - T[current_week-1], 5));
	}
	
	protected void expectationFormation(Care care) {
		//forms the expectation for the next consultation based on previous experience
		
		// CASE 1. got the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = expectation[current_week-1] + care.EXP_POS;}
		// CASE 2. didn't get the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 0) { 
				expectation[current_week] = expectation[current_week-1] - care.EXP_NEG;
				} 
		// CASE 3. didn't ask for a visit
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1];
			}	
		//limit to expecations
		if(expectation[current_week] >5) {expectation[current_week] = 5;}
		if(expectation[current_week] <= 0) {expectation[current_week] = 0;}
	}
	
	protected void behaviouralRule(double ran, double SUBJECTIVE_INITIATIVE) {
	// sets the value of B[current_week] to determine next week seek behaviour
	currentMot = (SUBJECTIVE_INITIATIVE*(expectation[current_week]) + (1-SUBJECTIVE_INITIATIVE)*H[current_week])*0.2;// very careful, the multiplier here is 0.2, and not 0.1, because there is a 1/2 factor inside!

		if(ran < currentMot) {
			B[current_week] = 1;
		} else {
			B[current_week] = 0;
		}	
	}
	
	public void initializePatient(int n_diseases, int weeks, int i, Care care) {
		H = new double[weeks+1];
		T = new double[weeks+1];
		C = new int[weeks+1];
		B = new int[weeks+1];
		expectation = new double[weeks+1];

		d = n_diseases;
		H[0] = 0;
		expectation[0] = 2.5;
		T[0] = 0;
		C[0] = 0;
		B[0] = 0;
		id = i;
	
		progressProbability = (float)Math.min(1, (d * care.DISEASE_VELOCITY/weeks));
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
	public double getExpectativas() {
		return expectation[current_week];
	}
	public double getNecesidades() {
		return H[current_week];
	}
	public double getBusquedaCuidado() {
		double total = 0;
		for(int i=0; i < B.length; i++) {
			total = total + B[i];
		}
		return total;
	}
	
	public void testTreatmentMec(){
		H[current_week] = id;
		//System.out.println(id);
	}
	public float getprogressProbability() {
		return progressProbability;
	}
	
}
