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
	protected boolean excluido = false;
	protected int failedAttempts;
	
	//Convenience variables

	protected int current_week; //is the step + 1
		
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;

		//disease progression
		biologicalMechanism(care);
		
		//test treatment efficacy at different needs
		//testTreatmentMec();
		
		//internal agent events
		expectationFormation(care);
		behaviouralRule(care.k, care.random.nextDouble(), care.SUBJECTIVE_INITIATIVE);
		
		//TESTING, ALWAYS RECEIVE TREATMENT
		//B[current_week] = 1;
		
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

	
	private void biologicalMechanism(Care care) {	
		// changes H
		int progress = 0;

		if(care.random.nextBoolean((double) (d*care.DISEASE_VELOCITY)/care.weeks)) {
			progress = 1;
			care.totalProgress = care.totalProgress + 1;
		}
		double noise = 0;//care.random.nextGaussian()*0.05; //NEED TO ADJUST NOISE PER STEPS
		double finalProgress =  progress + noise;
		H[current_week] = Math.max(0, Math.min(H[current_week-1] + finalProgress - T[current_week-1], 5));
	}
	
	protected void expectationFormation(Care care) {
		double noise = 0;//care.random.nextGaussian() *0.05;// NEED TO ADJUST NOISE PER STEPS
		//forms the expectation for the next consultation based on previous experience
		
		// got the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = expectation[current_week-1] + care.EXP_POS + noise;}
		//didn't get the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 0) { 
				expectation[current_week] = expectation[current_week-1] - care.EXP_NEG + noise;
				} 
		//didn't ask for a visit
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1] + noise;
			}	
		//limit to expecations
		if(expectation[current_week] >5) {expectation[current_week] = 5;}
		if(expectation[current_week] <= 0) {expectation[current_week] = 0;}
		
		//Conterfactual: patients don't form expectations
		//expectation[current_week] = 0;

	}
	
	protected void behaviouralRule(double k, double ran, double SUBJECTIVE_INITIATIVE) {
	// sets the value of B[current_week] to 0  won't seek care during this week
	// or B[curent_week] = 1 if patient will seek care this week

	currentMot = SUBJECTIVE_INITIATIVE*(0.1*expectation[current_week]) + (1-SUBJECTIVE_INITIATIVE)*0.1*H[current_week];

		  
		if(ran < currentMot) {
			B[current_week] = 1;
		} else {
			B[current_week] = 0;
		}	
		
	}
	

	public void initializePatient(int severity, int weeks, int i, Care care) {
		d = severity;
		
		H = new double[weeks+1];
			if(care.random.nextBoolean((double) (d*care.DISEASE_VELOCITY)/weeks)) {
				care.totalProgress = care.totalProgress + 1;
				H[0] = 1;
			} else {H[0] = 0;}
		// initialization with everybody high needs
		//H[0] = 1;	
		expectation = new double[weeks+1];
			double exp = care.random.nextGaussian()*Math.sqrt(2)+2.5;
			expectation[0] = Math.min(4, Math.max(1, exp));
		
		//fix initial expectation:
			//expectation[0] = 0;
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
	//public double getcurrentMot() {return currentMot;}
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
	
}
