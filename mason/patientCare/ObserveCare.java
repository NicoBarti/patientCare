package patientCare;

import sim.engine.SimState;
import sim.util.*;

import sim.engine.Steppable;

/**
 * This is an "agent" that stores the state variables at given intervals (windows).
 * Should be initialized from Care, after calling care.start() with the care.startObserver() method.
 * The observer agent is scheduled at the beginning of each time step, before any other agent.
 * The observer should be called at the end of the simulation from care.finish() to store the final state.
 * The window = 0 is for initial conditions 
 */
public class ObserveCare implements Steppable{
	/**
	 * H for patient p at window i
	 */
	double[][] H_p_i;
	/**
	 * Visit for patient p with provider w at window i
	 */
	int[][][] C_p_w_i;
	/**
	 * Seeking of patient p with provider w ar window i
	 */
	int[][][] B_p_w_i;
	/**
	 * Need for patient p at window i
	 */
	double[][] N_p_i;
	/**
	 * Treatment for patient p at time i
	 */
	double[][] T_p_i;
	/**
	 * Expectation of patient p for provider w at window i
	 */
	double[][][] E_p_w_i;
	/**
	 * Seeking of patient p at window i (1 if sought care with any provider, 0 if didn't)
	 */
	int[][] simple_B_p_i;
	/**
	 * Visit for patient p at window i (1 if saw a provider, 0 if didn't)
	 */
	int[][] simple_C_p_i;
	/**
	 * Average expectation across providers for patient p at window i
	 */
	double[][] simple_E_p_i;
	/**
	 * Delta for patient p 
	 */
	double[][] delta_p_i;
	
	//internals
	int arraysLength;
	int period;
	int counter = 0;
	int windowNumber = 0;
	int[] windows; // array to export the timestep of each window

	boolean obsH = false;
	boolean obsN = false;
	boolean obsC= false;
	boolean obsT= false;
	boolean obsE= false;
	boolean obsB= false;
	boolean obsSimpleC = false;
	boolean obsSimpleE = false;
	boolean obsSimpleB = false;
	boolean obsDelta = false;
	
	Care care;
	Patient patient;
	
	boolean testing = false;
	
	int simple_sum_i;
	double mean_exp;
	
	
	//this constructor for observing everything
	public ObserveCare(Care sim, int value) {
		care = sim;
		set_arrays_length(value);
		H_p_i = new double[care.N][arraysLength];obsH=true;obsH = true;
		simple_C_p_i = new int[care.N][arraysLength];obsSimpleC=true;
		simple_B_p_i = new int[care.N][arraysLength];obsSimpleB=true;
		N_p_i = new double[care.N][arraysLength];obsN=true;
		T_p_i = new double[care.N][arraysLength];obsT=true;
		simple_E_p_i = new double[care.N][arraysLength];obsSimpleE=true;
	}
	
	//this constructor for observing only the specified state variables
	public ObserveCare(Care sim, int value, Boolean H, Boolean N, Boolean C, 
			Boolean T, Boolean E, Boolean B, Boolean simple_C, Boolean simple_E,
			Boolean simple_B) {
		care = sim;
		set_arrays_length(value);
		if(H) {obsH = true; H_p_i = new double[care.N][arraysLength];}
		if(N) {obsN = true; N_p_i = new double[care.N][arraysLength];}
		if(C) {obsC = true; C_p_w_i = new int[care.N][care.W][arraysLength];}
		if(T) {obsT = true; T_p_i = new double[care.N][arraysLength];}
		if(E) {obsE = true; E_p_w_i = new double[care.N][care.W][arraysLength];}
		if(B) {obsB = true; B_p_w_i = new int[care.N][care.W][arraysLength];}
		
		if(simple_C) {obsSimpleC = true; simple_C_p_i = new int[care.N][arraysLength];}
		if(simple_E) {obsSimpleE = true; simple_E_p_i = new double[care.N][arraysLength];}
		if(simple_B) {obsSimpleB = true; simple_B_p_i = new int[care.N][arraysLength];}
	}

	//provisional constructor, exporting delta per patient. For a class example
	//this constructor for observing only the specified state variables
	public ObserveCare(Care sim, int value, Boolean H, Boolean N, Boolean C, 
			Boolean T, Boolean E, Boolean B, Boolean simple_C, Boolean simple_E,
			Boolean simple_B, Boolean delta) {
		care = sim;
		set_arrays_length(value);
		if(H) {obsH = true; H_p_i = new double[care.N][arraysLength];}
		if(N) {obsN = true; N_p_i = new double[care.N][arraysLength];}
		if(C) {obsC = true; C_p_w_i = new int[care.N][care.W][arraysLength];}
		if(T) {obsT = true; T_p_i = new double[care.N][arraysLength];}
		if(E) {obsE = true; E_p_w_i = new double[care.N][care.W][arraysLength];}
		if(B) {obsB = true; B_p_w_i = new int[care.N][care.W][arraysLength];}
		
		if(simple_C) {obsSimpleC = true; simple_C_p_i = new int[care.N][arraysLength];}
		if(simple_E) {obsSimpleE = true; simple_E_p_i = new double[care.N][arraysLength];}
		if(simple_B) {obsSimpleB = true; simple_B_p_i = new int[care.N][arraysLength];}
		
		if(delta) {obsDelta = true; delta_p_i =  new double[care.N][arraysLength];} 
	}
	
	
	private void set_arrays_length(int value) {
		period = value;
		int nWindows = (int)(care.varsigma/period);
		int remainder = 0;
		if(nWindows*period<care.varsigma) {
			remainder = 1;}
		arraysLength = 1 + nWindows +remainder; //initial conditions - observation windows - reminder
		windows = new int[arraysLength];
	}
	
	public void step(SimState state) {
		if(counter == 0) { //record initial conditions
			observe(windowNumber,(Care)state);
			windowNumber+=1;
		}
		if(counter == period) {
			observe(windowNumber,(Care)state);
			windowNumber+=1;
			counter=0;
		}
		counter+=1;
	}
	
	public void observe(int loc, Care state) {
		windows[loc] =(int)care.schedule.getSteps();

		if(obsC) {observeC(loc);}
		if(obsH) {observeH(loc);}
		if(obsB) {observeB(loc);} 
		if(obsN) {observeN(loc);}
		if(obsT) {observeT(loc);}
		if(obsE) {observeE(loc);}
		if(obsSimpleC) {observeSimpleC(loc);}
		if(obsSimpleE) {observeSimpleE(loc);}
		if(obsSimpleB) {observeSimpleB(loc);}
		if(obsDelta) {obsDelta(loc);}

	}
	
	
	public void endObservation(Care state) {
		observe(windowNumber, state);
	}
	
	public void observeC(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				C_p_w_i[patient.p][w][loc] = patient.c_p_i_1[w];
			}}
	}
	
	public void observeSimpleC(int loc) {
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i = 0;
			for(int w = 0; w<care.W;w++) {
				simple_sum_i += patient.c_p_i_counter[w];
			}
			simple_C_p_i[p][loc] = simple_sum_i;
			}
	}
	
	public void observeH(int loc) {
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			H_p_i[patient.p][loc] = patient.h_p_i_1;
	}}
	
	public void observeN(int loc) {
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			N_p_i[patient.p][loc] = patient.n_p_i;
	}}
	
	public void observeT(int loc) {
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			T_p_i[patient.p][loc] = patient.t_p_i_1;
	}}
	
	public void observeE(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				E_p_w_i[patient.p][w][loc] = patient.e_p_i_1[w];
			}}
	}
	
	public void observeSimpleE(int loc){
		//gives the mean expectation across providers for a given patient
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			mean_exp=0;
			for(int w = 1; w<care.W;w++) {
				mean_exp+=patient.e_p_i_1[w];
			}
			simple_E_p_i[patient.p][loc] = mean_exp/care.W;
			}
	}

	
	public void observeB(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				B_p_w_i[patient.p][w][loc] = patient.b_p_i_1[w];
			}}
	}
	
	public void observeSimpleB(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i=0;
			for(int w = 0; w<care.W;w++) {
				simple_sum_i += patient.b_p_i_1[w];
			}
			simple_B_p_i[patient.p][loc] = simple_sum_i;
			}
	}
	
	public void obsDelta(int loc) {
		for(int p=0;p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			delta_p_i[patient.p][loc] = patient.delta_p;
		}
		
	}
	
	public int[][][] getC(){return C_p_w_i;}
	public double[][] getH(){return H_p_i;}
	public int[][][] getB(){return B_p_w_i;}
	public double[][] getN(){return N_p_i;}
	public double[][] getT(){return T_p_i;}
	public double[][][] getE(){return E_p_w_i;}
	public double[][] getSimpleE(){return simple_E_p_i;}
	public int[][] getSimpleC(){return simple_C_p_i;}
	public int[][] getSimpleB(){return simple_B_p_i;}
	public double[][] getDelta(){return delta_p_i;} 

	
	public int getarraysLengthreturn() {return arraysLength;}
	public int[] getWindows() {return windows;};
	

	
}