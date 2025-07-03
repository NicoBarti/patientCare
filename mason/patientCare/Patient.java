package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	
	
	//State variables for the patient agent
	double h_i;
	double h_i_1;
	double t_i;
	double t_i_1;
	double n_i;
	double[] e_i;
	double[] e_i_1;
	int[] c_i;
	int[] c_i_1;
	int[] b_i;
	int[] b_i_1;
	
	//Control variables for the patient agent (parameters)
	double delta;
	double progressProbability;
	double capN;
	double rho;
	double eta;
	double capE;
	double psi;
	double iota;
	double kappa;
	
	//internals
	Care care;
	private int id;
	private double[] interaction;
	private Boolean interact;
	myUtil ut = new myUtil();
	private double e_fluctuation;
	
	
	public void step(SimState state) {
		care = (Care) state;
		interact = false;
		
		//disease progression
		diseaseEvolution(care);
		
		//compute agents mechanisms
		healthNeedPerception();
		expectationFormation(care);
		behaviouralRule(care);

		//eventually interact:
		int w = -1;
		for(int b_w = 0; b_w < care.W; b_w++) { //see if there is any b_w == 0
			if(b_i[b_w] == 1) {
				w = b_w;
				interact = true;
				break;}}
		 
		if(interact) {
			interaction = care.appointer.appoint(w, id, h_i);
			c_i[(int)interaction[0]] = 1;
			t_i = interaction[1];}
		
		stepForwardStateVariables();
		} 

	
	protected void diseaseEvolution(Care care) {	
		// changes H
		int Bernoulli = 0;
		if(care.random.nextBoolean(progressProbability)) {
			Bernoulli = 1;
		}
		h_i = h_i_1 - t_i_1 + Bernoulli ;
		h_i = Math.max(h_i, 0); // health status can't be negative
	}

	protected void healthNeedPerception() {
		n_i = Math.max(Math.min(h_i, capN), 0);
	}
	
	protected void expectationFormation(Care care) {
		//forms expectations for each provider based on previous experience
		// fluctuation
		for(int w = 0; w < care.W; w++) {
			e_fluctuation = care.random.nextGaussian()*(rho + eta)*kappa;
			// CASE 1 got the visit with provider w
			if(c_i_1[w] == 1) {
				e_i[w] = e_i_1[w]+ rho + e_fluctuation;
			} else
			// CASE 2 didn't get the visit with provider but wanted provider w
			if(b_i_1[w] == 1 & c_i_1[w] == 0) {
				e_i[w] = e_i_1[w] - eta + e_fluctuation;
			} else
			// CASE 3. didn't ask for a visit with provider w
			if(b_i_1[w] == 0) {
				e_i[w] = e_i_1[w] + e_fluctuation;
				
			}
			
			//limit expecations
			if(e_i[w] >5) {e_i[w] = capE;}
			if(e_i[w] <= 0) {e_i[w] = 0;}
		}
	}
	
	protected void behaviouralRule(Care care) {
	// sets the value of B[current_week] to determine next week seek behaviour
		// Find the provider with highest expectation
		int[] randomAccess = ut.accessArray(care.W, care.random.nextInt(care.W));
		int wMaxExpectation = randomAccess[0];
		for(int i = 1; i < care.W;i++) {
			if(e_i[randomAccess[i]] > e_i[wMaxExpectation]) {
				wMaxExpectation = randomAccess[i];
			}}
	double currentMot;
	if(e_i[wMaxExpectation] != 0 & n_i != 0) {
		b_i[wMaxExpectation] = 0;
		currentMot = (psi*e_i[wMaxExpectation] + (1-psi)*n_i)*iota;
		if(care.random.nextDouble() < currentMot) {
			b_i[wMaxExpectation] = 1;} 
	}}
	
	
	public void stepForwardStateVariables(){
		h_i_1 = h_i;
			h_i = 0;
		t_i_1 = t_i;
			t_i = 0;
		e_i_1 = e_i;
			for(int i = 0; i< e_i.length;i++) {e_i[i]=0;}
		c_i_1 = c_i;
			for(int i = 0; i< c_i.length;i++) {c_i[i]=0;}
		b_i_1 = b_i;
			for(int i = 0; i< b_i.length;i++) {b_i[i]=0;}
	}	
}
