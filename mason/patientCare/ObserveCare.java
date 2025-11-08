package patientCare;

import sim.engine.SimState;
import sim.util.*;

import sim.engine.Steppable;

/**
 * This is an "agent" that stores the state variables at given intervals (windows).

 * Should be initialized from Care, after calling care.start() with the care.startObserver() method.
 * The observer agent is scheduled at the beginning of each time step, before any other agent, so it has priority = 0.
 * The observer should be called at the end of the simulation from care.finish() to store the final state of the system.
 * The window = 0 is for initial conditions.
 * 
 * The X_"p" arrays capture patient states. If a patient is no longer part of the system, -1 are recorded from the time it was no longer seen, but previous information is preserved. 
 * Note that an X_p array is agnostic of the global state of the system. For instance, a patient might still have expectations E_p_w for a provider that doesn't exist.
 * 
 */
/**
 * 
 */
public class ObserveCare implements Steppable{
	private static final long serialVersionUID = 1L;
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
	//double[][] delta_p_i;
	
	//internals
	int arraysLength;
	int period;
	int counter = 0;
	/**
	 * The window for current observation. Window 0 is for initial conditions
	 */
	int windowNumber = 0; 
	
	/**
	 * Array that exports the timestep that corresponds to each window (for plotting with matplotlib)
	 */
	int[] windows;

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
	double sum_exp;
	
	
	//this constructor for observing everything
	public ObserveCare(Care sim, int value) {
		obsH = true;obsN = true;obsC = true;obsT = true;obsE = true;obsB = true;
		obsSimpleC = true;obsSimpleE = true;obsSimpleB = true;
		care = sim;
		set_arrays_length(value);
		H_p_i = new double[care.N][arraysLength];obsH=true;obsH = true;
		simple_C_p_i = new int[care.N][arraysLength];obsSimpleC=true;
		simple_B_p_i = new int[care.N][arraysLength];obsSimpleB=true;
		N_p_i = new double[care.N][arraysLength];obsN=true;
		T_p_i = new double[care.N][arraysLength];obsT=true;
		simple_E_p_i = new double[care.N][arraysLength];obsSimpleE=true;
		E_p_w_i = new double[care.N][care.W][arraysLength];
		B_p_w_i = new int[care.N][care.W][arraysLength];
		C_p_w_i = new int[care.N][care.W][arraysLength];
		//delta_p_i = new double[care.N][arraysLength];
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
	
	
	private void set_arrays_length(int value) {
		period = value;
		int nWindows = (int)(care.varsigma/period);
		int remainder = 0;
		if(nWindows*period<care.varsigma) {
			remainder = 1;}
		arraysLength = 1 + nWindows +remainder; //initial conditions - observation windows - reminder
		windows = new int[arraysLength];
	}
	
	/**
	 * Records state variables. 
	 * First, records initial conditions
	 * At each spep checks if its time to observe (obs_peridod).
	 * Once simulation is over gets called by care.finish() to record final states.
	 */
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
		
	}
	
	
	public void endObservation(Care state) {
		observe(windowNumber, state);
	}
	
	
	
	/**
	 * Populates C_p_w_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeC(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<patient.c_p_i_1.length;w++) {
				C_p_w_i[patient.p][w][loc] = patient.c_p_i_1[w];
			}}
	}
	
	/**
	 * Populates simple_C_p_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeSimpleC(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i = 0;
			for(int w = 0; w<patient.c_p_i_counter.length;w++) {
				simple_sum_i += patient.c_p_i_counter[w];
			}
			simple_C_p_i[p][loc] = simple_sum_i;
			}
	}

	/**
	 * Populates H_p_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeH(int loc) {
		for(int p = 0; p<care.patients.numObjs ;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			H_p_i[patient.p][loc] = patient.h_p_i_1;
	}}
	
	/**
	 * Populates N_p_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeN(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			N_p_i[patient.p][loc] = patient.n_p_i;}
	}
	
	/**
	 * Populates T_p_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeT(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			T_p_i[patient.p][loc] = patient.t_p_i_1;}
	}
	
	/**
	 * Populates E_p_w_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeE(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) { //observe only existing patients
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<patient.e_p_i_1.length;w++) {
				E_p_w_i[patient.p][w][loc] = patient.e_p_i_1[w];}
			}
	}
	
	/**
	 * Populates B_p_w_i by observing the internal representation of p (if p exists)
	 * @param loc the windowNumber
	 */
	public void observeB(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<patient.b_p_i_1.length;w++) {
				B_p_w_i[patient.p][w][loc] = patient.b_p_i_1[w];
			}}
	}
	
	/** Populates simple_E_p_i: the mean expectation across providers for each patient. Only for patients that exist. 
	 * It includes expectations = 0, which may correspond to non-existent providers. 
	 * 
	 * @param loc the windowNumber
	 */
	public void observeSimpleE(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
				sum_exp=0;
				for(int w = 1; w<patient.e_p_i_1.length;w++) {
					sum_exp+=patient.e_p_i_1[w];
				}
			simple_E_p_i[patient.p][loc] = sum_exp/patient.e_p_i_1.length;
			} 		
			
	}

	/** Populates simple_B_p_i: the sum of behavioural results across providers for each patient. Only for patients that exist. 
	 * It includes expectations = 0, which may correspond to non-existent providers. 
	 * 
	 * @param loc the windowNumber
	 */
	public void observeSimpleB(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i=0;
			for(int w = 0; w<patient.b_p_i_1.length;w++) {
				simple_sum_i += patient.b_p_i_1[w];
			}
			simple_B_p_i[patient.p][loc] = simple_sum_i;
			}
	}
	
//	public void obsDelta(int loc) {
//		for(int p=0;p<care.patients.numObjs;p++) {
//			patient = ((Patient)care.patients.objs[p]);
//			delta_p_i[patient.p][loc] = patient.delta_p;
//		}
//		
//	}
	
	public int[][][] getC(){return C_p_w_i;}
	public double[][] getH(){return H_p_i;}
	public int[][][] getB(){return B_p_w_i;}
	public double[][] getN(){return N_p_i;}
	public double[][] getT(){return T_p_i;}
	public double[][][] getE(){return E_p_w_i;}
	public double[][] getSimpleE(){return simple_E_p_i;}
	public int[][] getSimpleC(){return simple_C_p_i;}
	public int[][] getSimpleB(){return simple_B_p_i;}
	//public double[][] getDelta(){return delta_p_i;} 

	
	public int getarraysLengthreturn() {return arraysLength;}
	public int[] getWindows() {return windows;};
	
	public double getMeanFinalH() {
		double sumH = 0;
		for (int p = 0; p< care.patients.numObjs; p++) {
			patient = ((Patient)care.patients.objs[p]);
			sumH += (H_p_i[patient.p][arraysLength-1]);
		}
		return sumH/care.patients.numObjs;
	}
	
	public double getVarianceFinalH() {
		double FinalVarH = 0;
		double FinalMeanH = getMeanFinalH();
		for (int p = 0; p< care.patients.numObjs; p++) {
			patient = ((Patient)care.patients.objs[p]);
			FinalVarH += ((H_p_i[patient.p][arraysLength-1] - FinalMeanH) * (H_p_i[patient.p][arraysLength-1] - FinalMeanH));
		}
		return FinalVarH/care.patients.numObjs;
	}
	
	public double getSlopeHFinal() {
		double anteFinalH = 0;
		for (int p = 0; p< care.patients.numObjs; p++) {
			patient = ((Patient)care.patients.objs[p]);
			anteFinalH += (H_p_i[patient.p][arraysLength-2]);
		}
		anteFinalH = anteFinalH/care.patients.numObjs;
		return (getMeanFinalH()-anteFinalH)/period;
	}
	
	
	/** Reset the observer arrays to accomodate more patients.Find which arrays need modification (which is being observed).Copy the info from the old arrays.
	 * @param N_increase The number of patients to be added
	 */
	public void increaseNmidway(int N_increase) {
		
		if (obsH) {
			double[][] newH_p_i = increaseDual_newArr(H_p_i, N_increase);
			H_p_i = newH_p_i.clone();
		}
		if(obsN) {
			double[][] newN_p_i = increaseDual_newArr(N_p_i, N_increase);
			N_p_i = newN_p_i.clone();
		}
		if(obsC) {
			//if(C_p_w_i.length < newN) {
			int[][][] newC_p_w_i = increaseTriple_newArr(C_p_w_i, N_increase);
			C_p_w_i = 	newC_p_w_i.clone();
		}
		if(obsT) {
			//if(T_p_i.length < newN) {
			double[][] newT_p_i = increaseDual_newArr(T_p_i, N_increase);
			T_p_i = newT_p_i.clone();
		}
		if(obsE) {
			//if(E_p_w_i.length < newN) {
			double[][][] newE_p_w_i = increaseTriple_newArr(E_p_w_i, N_increase);
			E_p_w_i = newE_p_w_i.clone();
		}
		if(obsB) {
			//if(B_p_w_i.length < newN) {
			int[][][] newB_p_w_i = increaseTriple_newArr(B_p_w_i, N_increase);
			B_p_w_i = newB_p_w_i.clone();
		}
		if(obsSimpleC) {
			//if(simple_C_p_i.length < newN) {
			int[][] newsimple_C_p_i = increaseDual_newArr(simple_C_p_i, N_increase);
			simple_C_p_i = newsimple_C_p_i.clone();
			
		}
		if(obsSimpleE) {
			//if(simple_E_p_i.length < newN) {
			double[][] newsimple_E_p_i = increaseDual_newArr(simple_E_p_i, N_increase);
			simple_E_p_i = newsimple_E_p_i.clone();
		}
		if(obsSimpleB) {
			//if(simple_B_p_i.length < newN) {
			int[][] newsimple_B_p_i = increaseDual_newArr(simple_B_p_i, N_increase);
			simple_B_p_i = newsimple_B_p_i.clone();
		}
	}
	
	/** Reset the observer arrays to accomodate more providers
	 * @param newN The new (increased) number of patients
	 */
	public void increaseWmidway(int W_increase) {


		if(obsC) {
			int[][][] newC_p_w_i = increaseTriple_newW(C_p_w_i, W_increase);
			C_p_w_i = newC_p_w_i.clone();
		}
		if(obsE) {
			double[][][] newE_p_w_i = increaseTriple_newW(E_p_w_i, W_increase);
			E_p_w_i = newE_p_w_i.clone();
		}
		if(obsB) {
			int[][][] newB_p_w_i = increaseTriple_newW(B_p_w_i, W_increase);
			B_p_w_i = newB_p_w_i.clone();
		}
	}

	protected double[][] increaseDual_newArr(double[][] oldArr_p_i, int N_increase) {
		double[][] newArr_p_i = new double[oldArr_p_i.length+N_increase][arraysLength];
		//copy previous information
		for(int p = 0; p< oldArr_p_i.length;p++) {
			for(int i=0; i< oldArr_p_i[0].length; i++ ) {
				newArr_p_i[p][i] = oldArr_p_i[p][i];
			}
		}
		//delete information previous to creation
		for(int p =  oldArr_p_i.length; p< newArr_p_i.length;p++) {
			for(int i=0; i< windowNumber; i++ ) {
				newArr_p_i[p][i] = -1;
			}
		}
		return newArr_p_i;
	}
	
	protected int[][] increaseDual_newArr(int[][] oldArr_p_i, int N_increase) {
		int[][] newArr_p_i = new int[oldArr_p_i.length+N_increase][arraysLength];
		for(int p = 0; p< oldArr_p_i.length;p++) {
			for(int i=0; i< oldArr_p_i[0].length; i++ ) {
				newArr_p_i[p][i] = oldArr_p_i[p][i];
			}
		}
		//delete information previous to creation
		for(int p =  oldArr_p_i.length; p< newArr_p_i.length;p++) {
			for(int i=0; i< windowNumber; i++ ) {
				newArr_p_i[p][i] = -1;
			}
		}
		return newArr_p_i;
	}
	
	protected double[][][] increaseTriple_newArr(double[][][] oldArr_p_i, int N_increase) {
		double[][][] newArr_p_w_i = new double[oldArr_p_i.length+N_increase][oldArr_p_i[0].length][arraysLength];
		//copy old array into new
		for(int p = 0; p< oldArr_p_i.length;p++) {
			for(int w = 0; w<oldArr_p_i[0].length; w++) {
				for(int i=0; i< oldArr_p_i[0][0].length; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_i[p][w][i];
				}
			}
		}
		//populate backwards with -1s
		for(int p = oldArr_p_i.length; p< newArr_p_w_i.length;p++) {
			for(int w = 0; w<oldArr_p_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}
	
	protected int[][][] increaseTriple_newArr(int[][][] oldArr_p_i, int N_increase) {
		int[][][] newArr_p_w_i = new int[oldArr_p_i.length+N_increase][oldArr_p_i[0].length][arraysLength];
		//copy old array into new
		for(int p = 0; p< oldArr_p_i.length;p++) {
			for(int w = 0; w<oldArr_p_i[0].length; w++) {
				for(int i=0; i< oldArr_p_i[0][0].length; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_i[p][w][i];
				}
			}
		}
		//populate backwards with -1s
		for(int p = oldArr_p_i.length; p< newArr_p_w_i.length;p++) {
			for(int w = 0; w<oldArr_p_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}
	
	protected double[][][] increaseTriple_newW(double[][][] oldArr_p_w_i, int W_increase){
		double[][][] newArr_p_w_i = new double[oldArr_p_w_i.length][oldArr_p_w_i[0].length+W_increase][arraysLength];
		//copy information from old array into new
		for(int p = 0; p< oldArr_p_w_i.length;p++) {
			for(int w = 0; w<oldArr_p_w_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_w_i[p][w][i];
				}
			}
		}
		//populate the new W´s backwards with -1s
		for(int p = 0; p< oldArr_p_w_i.length;p++) {
			for(int w = oldArr_p_w_i[0].length; w<oldArr_p_w_i[0].length+W_increase; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}
	
	protected int[][][] increaseTriple_newW(int[][][] oldArr_p_w_i, int W_increase){
		int[][][] newArr_p_w_i = new int[oldArr_p_w_i.length][oldArr_p_w_i[0].length+W_increase][arraysLength];
		//copy information from old array into new
		for(int p = 0; p< oldArr_p_w_i.length;p++) {
			for(int w = 0; w<oldArr_p_w_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_w_i[p][w][i];
				}
			}
		}
		//populate the new W´s backwards with -1s
		for(int p = 0; p< oldArr_p_w_i.length;p++) {
			for(int w = oldArr_p_w_i[0].length; w<oldArr_p_w_i[0].length+W_increase; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}
	
	/**
	 * Records that a patient is gone from the system by setting all future observations to -1 in X_p arrays
	 * @param id
	 */
	public void unobservePatient(int id) {
		// Double arrays
			for(int i = windowNumber; i< arraysLength; i++) {
				if(obsH) {H_p_i[id][i] = -1;}
				if(obsN) {N_p_i[id][i] = -1;}
				if(obsT) {T_p_i[id][i] = -1;}
				if(obsSimpleB) {simple_B_p_i[id][i] = -1;}
				if(obsSimpleC) {simple_C_p_i[id][i] = -1;}
				if(obsSimpleE) {simple_E_p_i[id][i] = -1;}

		}
		// Triple arrays
			for(int w = 0; w<B_p_w_i[id].length ; w++) {
				for(int i = windowNumber; i< arraysLength; i++) {
					if(obsB) {B_p_w_i[id][w][i] = -1;}
					if(obsC) {C_p_w_i[id][w][i] = -1;}
					if(obsE) {E_p_w_i[id][w][i] = -1;}
				}
			}
	}
	
	/**
	 * To be implemented. Not needed. Not observing the provider's perspective so far (there aren't any X_w arrays)
	 * @param w
	 */
	public void unobserveProvider(int w) {}
	
	

}