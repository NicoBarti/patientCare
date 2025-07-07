package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	
	//State variables for the patient agent
	double h_p_i;	double h_p_i_1;
	double t_p_i;	double t_p_i_1;
	double[] e_p_i;	double[] e_p_i_1;
	int[] c_p_i;	int[] c_p_i_1;
	int[] b_p_i;	int[] b_p_i_1;
	double n_p_i;
	
	//Control variables for the patient agent (parameters)
	double delta_p;
	double capN_p;
	double rho_p;
	double eta_p;
	double capE_p;
	double psi_p;
	double iota_p;
	float kappa_p;
	
	//internals
	protected Care care;
	protected int p;
	protected double[] interaction;
	protected Boolean interact;
	protected myUtil ut = new myUtil();
	protected double e_fluctuation;
	protected double progressProbability;
	protected double currentMot;
	protected int wMaxExpectation;
	
	//debug test
	protected Boolean testing=false;
	
	
	public void step(SimState state) {
		//System.out.println("Patient "+p+" delta "+delta_p);
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
			if(b_p_i[b_w] == 1) {
				w = b_w;
				interact = true;
				break;}}
		 
		if(interact) {
			//System.out.println("Wants provider: "+t_p_i );
			interaction = care.appointer.appoint(w, p, h_p_i);
			if((int)interaction[0] == -1) {
				c_p_i[w] = 0;
				t_p_i = 0;
				//System.out.println("No treatment t_p_i: "+t_p_i );
			} else {
				c_p_i[(int)interaction[0]] = 1; //w or another available provider
				t_p_i = interaction[1];
				//System.out.println("Treatment w "+(int)interaction[0]+" t_p_i "+interaction[1] );
				}
			}
		
		stepForwardStateVariables();
		} 

	
	protected void diseaseEvolution(Care care) {	
		// changes H
		int Bernoulli = 0;
		if(care.random.nextBoolean(progressProbability)) {
			Bernoulli = 1;
		}
		h_p_i = h_p_i_1 - t_p_i_1 + Bernoulli ;
		h_p_i = Math.max(h_p_i, 0); // health status can't be negative
	}

	protected void healthNeedPerception() {
		n_p_i = Math.max(Math.min(h_p_i, capN_p), 0);
	}
	
	protected void expectationFormation(Care care) {
		//forms expectations for each provider based on previous experience
		e_fluctuation = 0;
		if(care.random.nextBoolean((float)kappa_p)) {e_fluctuation = care.random.nextGaussian();}
		for(int w = 0; w < care.W; w++) {
			// CASE 1 got the visit with provider w
			if(c_p_i_1[w] == 1) {
				e_p_i[w] = e_p_i_1[w]+ rho_p + e_fluctuation;
			} else
			// CASE 2 didn't get the visit with provider but wanted provider w
			if(b_p_i_1[w] == 1 & c_p_i_1[w] == 0) {
				e_p_i[w] = e_p_i_1[w] - eta_p + e_fluctuation;
			} else
			// CASE 3. didn't ask for a visit with provider w
			if(b_p_i_1[w] == 0) {
				e_p_i[w] = e_p_i_1[w] + e_fluctuation;
			}
			
			//limit expecations
			if(e_p_i[w] >5) {e_p_i[w] = capE_p;}
			if(e_p_i[w] <= 0) {e_p_i[w] = 0;}
		}
	}
	
	protected void behaviouralRule(Care care) {
	// sets the value of B[current_week] to determine next week seek behaviour
		// Find the provider with highest expectation
		int[] randomAccess = ut.accessArray(care.W, care.random.nextInt(care.W));
		wMaxExpectation = randomAccess[0];
		for(int i = 1; i < care.W;i++) {
			if(e_p_i[randomAccess[i]] > e_p_i[wMaxExpectation]) {
				wMaxExpectation = randomAccess[i];
			}}

	if(e_p_i[wMaxExpectation] != 0 & n_p_i != 0) {
		b_p_i[wMaxExpectation] = 0;
		currentMot = (psi_p*e_p_i[wMaxExpectation] + (1-psi_p)*n_p_i)*iota_p;
		if(care.random.nextDouble() < currentMot) {
			b_p_i[wMaxExpectation] = 1;} 
	}}
	
	
	public void stepForwardStateVariables(){
		h_p_i_1 = h_p_i;	h_p_i = 0;
		t_p_i_1 = t_p_i;	t_p_i = 0;
		for(int i = 0; i< e_p_i.length;i++) {
			e_p_i_1[i] = e_p_i[i];
			e_p_i[i]=0;}
		for(int i = 0; i< c_p_i.length;i++) {
			c_p_i_1[i] = c_p_i[i];
			c_p_i[i]=0;}
		for(int i = 0; i< b_p_i.length;i++) {
			b_p_i_1[i] = b_p_i[i];
			b_p_i[i]=0;}
	}	
}
