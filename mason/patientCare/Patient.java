package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private static final long serialVersionUID = 1L;
	
	//State variables for the patient agent
	double h_p_i;	double h_p_i_1;
	double t_p_i;	double t_p_i_1;
	double[] e_p_i;	double[] e_p_i_1;
	int[] c_p_i;	int[] c_p_i_1;
	int[] b_p_i_counter;
	int[] b_p_i;	int[] b_p_i_1;
	double n_p_i;	
	
	//Control variables for the patient agent (parameters)
	double delta_p;
	double capN_p; //this parameter should be chanched to a single capN for all agents
	double rho_p;
	double eta_p;
	double capE_p;
	double psi_p;
	double iota_p;
	float kappa_p;
	//boolean active = true;
	
	//internals
	protected Care care;
	protected int p;
	protected double[] interaction;
	protected Boolean interact;
	protected myUtil ut = new myUtil();
	protected double progressProbability;
	protected double currentMot;
	protected int wMaxExpectation;
	protected int Bernoulli=0;
	protected double Gaussian=0;
	protected double instExp=0;

	
	//debug test
	protected Boolean testing=false;
	double testing_prev_H;
	
	
	public void step(SimState state) {
		//System.out.println("Patient "+p+" delta "+delta_p);
		care = (Care) state;
		
		//if(!active) {
		//	setMinusOnes();}
		//else {
		
		interact = false;
		
		//for policy prioritization tesging
		if (testing) {testing_order();}
		
		//disease progression
		diseaseEvolution(care);
		
		//compute agents mechanisms
		healthNeedPerception();
		expectationFormation(care);
		behaviouralRule(care);

		//eventually interact:
		int w = -1;
		for(int b_w = 0; b_w < b_p_i.length; b_w++) { //see if there is any b_w == 0
			if(b_p_i[b_w] == 1) {
				w = b_w; //find intended provider
				interact = true;
				break;}}

		if(interact) {
			interaction = care.appointer.appoint(w, p, h_p_i); //try to interact with prefered provider
			if((int)interaction[0] == -1) { //no provider was available
				c_p_i[w] = 0;
				t_p_i = 0;
			} else {
				c_p_i[(int)interaction[0]] = 1; //w or another available provider
				t_p_i = interaction[1];
				if (care.observer != null) {
					care.observer.recordInteraction((int)state.schedule.getSteps(), interaction[1]);
				}
				}
			}
		

		stepForwardStateVariables();
		}
		//} 

	
	protected void diseaseEvolution(Care care) {	
		// changes H
		Bernoulli = 0;
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
		Gaussian = 0;
		if(care.random.nextBoolean((float)kappa_p)) {Gaussian = care.random.nextGaussian();}
		for(int w = 0; w < e_p_i_1.length; w++) {
			// CASE 1 got the visit with provider w
			if(c_p_i_1[w] == 1) {
				instExp =  rho_p ;
				//e_p_i[w] = e_p_i_1[w] +instExp;
			} else
			// CASE 2 didn't get the visit with provider w, but wanted provider w
			if(b_p_i_1[w] == 1 & c_p_i_1[w] == 0) {
				instExp =  - eta_p ;
				//e_p_i[w] = e_p_i_1[w] - instExp;
			} else
			// CASE 3. didn't ask for a visit with provider w
			if(b_p_i_1[w] == 0) {
				instExp =  0;
				//e_p_i[w] = e_p_i_1[w] + Gaussian;
			}
			
			e_p_i[w] = e_p_i_1[w] + Gaussian + instExp;
			
			//limit expecations
			if(e_p_i[w] >capE_p) {
				instExp = instExp+Gaussian + capE_p-e_p_i[w]; //keep track of effective change only
				e_p_i[w] = capE_p;}
			if(e_p_i[w] <= 0) {
				instExp = instExp+Gaussian - e_p_i[w]; //keep track of effective change only
				e_p_i[w] = 0;}
		}
	}
	
	protected void behaviouralRule(Care care) {
	// sets the value of B[current_week] to determine next week seek behaviour
		// Find the provider with highest expectation
		int[] randomAccess = ut.accessArray(e_p_i.length, care.random.nextInt(e_p_i.length));
		wMaxExpectation = randomAccess[0];
		for(int i = 1; i < e_p_i.length;i++) {
			if(e_p_i[randomAccess[i]] > e_p_i[wMaxExpectation]) {
				wMaxExpectation = randomAccess[i];
			}}

	if(e_p_i[wMaxExpectation] != 0 & n_p_i != 0) {
		b_p_i[wMaxExpectation] = 0; //it was 0 already, just making sure
		currentMot = (psi_p*e_p_i[wMaxExpectation] + (1-psi_p)*n_p_i)*iota_p;
		if(care.random.nextDouble() < currentMot) {
			b_p_i[wMaxExpectation] = 1;
			b_p_i_counter[wMaxExpectation]+=1;} 
	}}
	
	
	public void stepForwardStateVariables(){
		h_p_i_1 = Double.valueOf(h_p_i) ;	h_p_i = 0;
		t_p_i_1 = Double.valueOf(t_p_i) ;	t_p_i = 0;
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

	public double get_MeanE() {
		double meanE = 0;
		for (int i=0;i<e_p_i_1.length;i++) {
			meanE += e_p_i_1[i];
		}
		return meanE/e_p_i_1.length;
	}

	/** Method for testing Prioritizatio (allocation policy)
	 * captures the variables that lead to this ordering and sends them to care.test_registerOrder
	 */
	public void testing_order() {
		care.test_registerOrder(p, h_p_i_1, n_p_i, get_MeanE() );
	}
	
	public double getdelta() {
		return delta_p;
	}

	public double getcapN() {
		return capN_p;
	}

	public double getcapE() {
		return capE_p;
	}

	public double getpsi() {
		return psi_p;
	}

	//protected void inactivatePatient() {
	//	active = false;
	//}
	
	private void setMinusOnes() {
		h_p_i = -1;	 h_p_i_1 = -1;
	    t_p_i = -1;	 t_p_i_1=-1;
	    for(int i=0; i< e_p_i.length; i++) {
			e_p_i[i] = -1; e_p_i_1[i] = -1;
			c_p_i[i] = -1; c_p_i_1[i] = -1;
			b_p_i_counter[i] = -1;
			b_p_i[i] = -1;	 b_p_i_1[i]= -1;
	    }
		n_p_i = -1;
	}
	
	/**
	 * Change the arrays that store w-repated information: e,c, and b
	 * @param newW
	 */
	public void increaseWmidway(int W_increase) {
		//double[] e_p_i;	double[] e_p_i_1;
		//check if current array is small in w
			//this is a 0s array btween steps, so just re initialize it with new size
			e_p_i = new double[e_p_i.length+W_increase];

			//create a new transitory array to copy info
			double[] new_e_p_i_1 = increaseSingle_newW(e_p_i_1, W_increase);
			e_p_i_1 = new_e_p_i_1.clone();			// assign (clone) to array
		
			//this is a 0s array btween steps, so just re initialize it with new size
			c_p_i = new int[c_p_i.length+W_increase];
		
			//create a new transitory array to copy info
			int[] new_c_p_i_1 = increaseSingle_newW(c_p_i_1, W_increase);
			c_p_i_1 = new_c_p_i_1.clone();// assign (clone) to array
		
			//this is a 0s array btween steps, so just re initialize it with new size
			b_p_i = new int[b_p_i.length+W_increase];
		
			//create a new transitory array to copy info
			int[] new_b_p_i_1 = increaseSingle_newW(b_p_i_1, W_increase);
			b_p_i_1 = new_b_p_i_1.clone();// assign (clone) to array
		
			//create a new transitory array to copy info
			int[] new_b_p_i_counter = increaseSingle_newW(b_p_i_counter, W_increase);
			b_p_i_counter = new_b_p_i_counter.clone();// assign (clone) to array
	}
	
	private double[] increaseSingle_newW(double[] old_arr, int W_increase) {
		double[] newArr_i = new double[old_arr.length + W_increase];
		for(int i=0; i<old_arr.length;i++) {
			newArr_i[i] = old_arr[i];
		}
		return newArr_i;
	}
	
	private int[] increaseSingle_newW(int[] old_arr, int W_increase) {
		int[] newArr_i = new int[old_arr.length+W_increase];
		for(int i=0; i<old_arr.length;i++) {
			newArr_i[i] = old_arr[i];
		}
		return newArr_i;
	}
	
	/**
	 * This method makes a patient "forget" a provider. Its expactation to this provider is set to 0
	 * I think this makes expectations very relational.
	 * @param w the provider id
	 */
	public void removeExpectations(int w) {
		double erasedExpectation = e_p_i_1[w];
		//erase the expectation from the gone provider
		e_p_i[w] = 0;
		e_p_i_1[w] = 0;
		//pick an existing provider at random and assign them half the lost expectations
		care.providers.shuffle(care.random);
		int otherProviderID = ((Provider)care.providers.get(0)).w;
		e_p_i[otherProviderID] += erasedExpectation/2;
		e_p_i_1[otherProviderID] += erasedExpectation/2;

	}
	
}
