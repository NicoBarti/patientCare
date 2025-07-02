package patientCare;

import sim.engine.*;

public class Patient implements Steppable {

	//Internals to agent:
	protected int[][] c; // received care
	protected int delta; // risk stratification {1, 2, 3}
	protected double[] h; // health status [0,+]
	protected double[] d;
	protected double[][] e; // expectation of outcomes for next appointment
	protected double[] t; //treatment effect
	protected int[][] b;
	protected int id;
	protected double currentMot;
	private float progressProbability;
	protected int totalProgress = 0; // The total number of needs during the simulation including initialization
	protected int capN;
	protected int capE;
	
	protected int current_week; //is the step + 1
		
	public void step(SimState state) {
		Care care = (Care) state;
		current_week = (int)care.schedule.getSteps() + 1;

		//disease progression
		diseaseEvolution(care);
		
		//internal agent events
		expectationFormation(care);
		behaviouralRule(care);
		
		//agent action
		if(B[current_week] == 1 & care.doctor.isAvailable()) { 
			care.totalInteractions = care.totalInteractions +1;
				C[current_week] = 1;
				T[current_week] = care.doctor.interactWithPatient(id, H[current_week]);
			} else {
				C[current_week] = 0;
				T[current_week] = 0;
				}
		} 

	
	protected void diseaseEvolution(Care care) {	
		// changes H
		int Bernoulli = 0;
		if(care.random.nextBoolean(progressProbability)) {
			Bernoulli = 1;
			totalProgress = totalProgress + 1;
		}
		h[current_week] = h[current_week-1] - t[current_week-1] + Bernoulli ;

	}

	protected void healthNeedPerception(Care care) {
		d[current_week] = Math.max(Math.min(h[current_week], capN), 0);
	}
	
	protected void expectationFormation(Care care) {
		//forms the expectation for the next consultation based on previous experience
		
		for(int w = 0; w < care.W; w++) {
			// CASE 1 got the visit with doctor w
			if(b[w][current_week-1] == 1 & c[w][current_week-1] == 1) {
				e[w][current_week] = e[w][current_week-1]+ care.rho;
			}
			// CASE 2 didn't get the visit with doctor w
			if(b[w][current_week-1] == 1 & c[w][current_week-1] == 0) {
				e[w][current_week] = e[w][current_week-1] - care.eta;
			}
			// CASE 3. didn't ask for a visit
			if(b[w][current_week-1] == 0) {
				e[w][current_week] = e[w][current_week-1];
			}
			//limit expecations
			if(e[w][current_week] >5) {e[w][current_week] = capE;}
			if(e[w][current_week] <= 0) {e[w][current_week] = 0;}
		}

	}
	
	protected void behaviouralRule(Care care) {
	// sets the value of B[current_week] to determine next week seek behaviour
		// Find the provider with highest expectation
		double[] expectationsPerProvider = new double[care.W]; // array with all privider's expectations
		for (int w=0;w<care.W;w++) {
			expectationsPerProvider[w] = e[w][current_week];
		}
		int[] randomAccess = new int[care.W]; // to break ties with some randomness if there are
		randomAccess = randomArray(care);
		
		int wMaxExpectation = randomAccess[0];
		for(int i = 1; i < care.W;i++) {
			if(expectationsPerProvider[randomAccess[i]] > expectationsPerProvider[wMaxExpectation]) {
				wMaxExpectation = randomAccess[i];
			}
		}
		

	currentMot = (care.SUBJECTIVE_INITIATIVE*(e[wMaxExpectation][current_week]) + (1-care.SUBJECTIVE_INITIATIVE)*h[current_week])*0.2;// very careful, the multiplier here is 0.2, and not 0.1, because there is a 1/2 factor inside!

		if(care.random.nextDouble() < currentMot) {
			b[wMaxExpectation][current_week] = 1;
		} 	
	}
	
	public void initializePatient(int n_diseases, int weeks, int i, Care care) {
		h = new double[weeks+1];
		d = new double[weeks+1];
		t = new double[weeks+1];
		c = new int[care.W][weeks+1];
		b = new int[care.W][weeks+1];
		e = new double[care.W][i];

		delta = n_diseases;
		h[0] = 0;
		
		for(int w = 0; w<care.W; w++) {
			e[w][0] = 2.5;
		}
		
		id = i;
		capN = 5;
		capE=5;
		progressProbability = (float)Math.min(1, (d * care.DISEASE_SEVERITY/weeks));
	}
	

	public int[] getReceivedCare() {return C;}
	public int getd(){ return d;}
	public int gettotalProgress() { return totalProgress;}
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
	
	protected int[] randomArray(Care care) {
	int[] randomAccessArray = new int[care.W]; // to break ties at random
	int index = care.random.nextInt(care.W);
	//int index = 0;
	int counter = 0;
	while (counter < care.W) {
		randomAccessArray[index] = counter;
		index += 1;
		counter += 1;
		if (index == care.W) {index = 0;}
	}
	return(randomAccessArray);
	}
	
}
