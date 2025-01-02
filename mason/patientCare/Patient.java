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
		expectationFormation();
		behaviouralRule(care.k, care.random.nextDouble());
		
		//TESTING, ALWAYS RECEIVE TREATMENT
		//B[current_week] = 1;
		
		//agent action

		if(B[current_week] == 1 & care.doctor.isAvailable(care.random.nextBoolean(0.3))) { //doctor available 1/3 times
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

		if(care.random.nextBoolean((double) d/care.weeks)) {
			progress = 1;
		}
		double noise = care.random.nextGaussian()*0.05;
		//fix noise to 0
		noise = 0;
		double finalProgress = noise + progress;
		H[current_week] = Math.max(0, Math.min(H[current_week-1] + finalProgress - T[current_week-1], 5));
	}
	
	protected void expectationFormation() {
		//forms the expectation for the next consultation based on previous experience
		// got the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 1) { 
				expectation[current_week] = expectation[current_week-1] + 0.2;
				//limit to expecations
				if(expectation[current_week] >5) {expectation[current_week] = 5;}
		}
		//didn't get the visit last week
		if(B[current_week-1] == 1 & C[current_week-1] == 0) { 
				expectation[current_week] = expectation[current_week-1] - 0.2;
				failedAttempts += 1;
				//limit to expecations
				if(expectation[current_week] <= 0) {expectation[current_week] = 0;}
				//exclusion from participation
				if(current_week >3) {
					if(expectation[current_week] == 0 & expectation[current_week-1] == 0 
							& expectation[current_week-2] == 0 & expectation[current_week-3] == 0 &
							failedAttempts > 3) {
						excluido = true;
					}
				}

				} 
		//didn't ask for a visit
		if(B[current_week-1] == 0) { 
			expectation[current_week] = expectation[current_week-1];
			}	
		
		//Conterfactual: patients don't form expectations
		//expectation[current_week] = 0;

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
		//conuter factual check: expectations reamain high and the same:
		//expectation[current_week] = 0.5;
		
		  if( H[current_week] == 0){
			  currentMot = 0;
		  } else {
			  currentMot = 0.1*expectation[current_week] + 0.1*H[current_week];
			  //All motivation from Healthneeds
			  //currentMot = H[current_week]/5;
			  //conuter factual check: patients allways motivated
			  //currentMot=1;
		  }
		  if(excluido){
			 currentMot = -0.3;
		  }
		  
		if(ran < currentMot) {
			B[current_week] = 1;
		} else {
			B[current_week] = 0;
		}	
		
	}
	

	public void initializePatient(double severity, int weeks, int i, Care care) {
		if(severity < 0.33) {d = 1;} 
		else if(severity < 0.66) {d = 2;}
		else {d = 3;}
		
		// fix complexity to 4
		d = 3;
		
		H = new double[weeks+1];
			if(care.random.nextBoolean((double) d/20)) {
				H[0] = 1;
			} else {H[0] = 0;}
		
		// fix initial needs:
			H[0] = 1;
			
		expectation = new double[weeks+1];
			double exp = care.random.nextGaussian()*Math.sqrt(2)+2.5;
			expectation[0] = Math.min(5, Math.max(0.1, exp));
			
			//fix expectation to 2.5
			expectation[0] = 2.5;
					
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
	
	public void testTreatmentMec(){
		H[current_week] = id;
		//System.out.println(id);
	}
	
}
