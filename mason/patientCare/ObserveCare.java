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
	double[][] delta_p_i;
	
	//internals
	int arraysLength;
	int period;
	int counter = 0;
	/**
	 * Window 0 is for initial conditions
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
	double mean_exp;
	
	
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
		if(obsDelta) {obsDelta(loc);}

	}
	
	
	public void endObservation(Care state) {
		observe(windowNumber, state);
	}
	
	public void observeC(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.providers.numObjs;w++) {
				C_p_w_i[patient.p][w][loc] = patient.c_p_i_1[w];
			}}
	}
	
	public void observeSimpleC(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i = 0;
			for(int w = 0; w<care.providers.numObjs;w++) {
				simple_sum_i += patient.c_p_i_counter[w];
			}
			simple_C_p_i[p][loc] = simple_sum_i;
			}
	}
	
	public void observeH(int loc) {
		for(int p = 0; p<care.patients.numObjs ;p++) {
			patient = ((Patient)care.patients.objs[p]);
			H_p_i[patient.p][loc] = patient.h_p_i_1;
	}}
	
	public void observeN(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			N_p_i[patient.p][loc] = patient.n_p_i;}
	}
	
	public void observeT(int loc) {
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			T_p_i[patient.p][loc] = patient.t_p_i_1;}
	}
	
	public void observeE(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.providers.numObjs;w++) {
				E_p_w_i[patient.p][w][loc] = patient.e_p_i_1[w];}
			}
	}
	
	public void observeSimpleE(int loc){
		//gives the mean expectation across providers for a given patient
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
				mean_exp=0;
				for(int w = 1; w<care.providers.numObjs;w++) {
					mean_exp+=patient.e_p_i_1[w];
				}
			simple_E_p_i[patient.p][loc] = mean_exp/care.providers.numObjs;
			} 		
			
	}

	
	public void observeB(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.providers.numObjs;w++) {
				B_p_w_i[patient.p][w][loc] = patient.b_p_i_1[w];
			}}
	}
	
	public void observeSimpleB(int loc){
		for(int p = 0; p<care.patients.numObjs;p++) {
			patient = ((Patient)care.patients.objs[p]);
			simple_sum_i=0;
			for(int w = 0; w<care.providers.numObjs;w++) {
				simple_sum_i += patient.b_p_i_1[w];
			}
			simple_B_p_i[patient.p][loc] = simple_sum_i;
			}
	}
	
	public void obsDelta(int loc) {
		for(int p=0;p<care.patients.numObjs;p++) {
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
	
	public double getMeanFinalH() {
		double FinalH = 0;
		int count=0;
		for (int p = 0; p< care.patients.numObjs; p++) {
			if(H_p_i[p][arraysLength-1] == -1) {continue;}
			FinalH += (H_p_i[p][arraysLength-1]);
			count+=1;
		}
		return FinalH/count;
	}
	
	public double getVarianceFinalH() {
		double FinalVarH = 0;
		int count=0;
		double FinalMeanH = getMeanFinalH();
		for (int p = 0; p< care.patients.numObjs; p++) {
			if(H_p_i[p][arraysLength-1] == -1) {continue;}
			FinalVarH += ((H_p_i[p][arraysLength-1] - FinalMeanH) * (H_p_i[p][arraysLength-1] - FinalMeanH));
			count+=1;
		}
		return FinalVarH/count;
	}
	
	public double getSlopeHFinal() {
		double anteFinalH = 0;
		int count=0;
		for (int p = 0; p< care.patients.numObjs; p++) {
			if(H_p_i[p][arraysLength-2] == -1) {continue;}
			anteFinalH += (H_p_i[p][arraysLength-2]);
			count+=1;
		}
		anteFinalH = anteFinalH/count;
		return (getMeanFinalH()-anteFinalH)/period;
	}
	
	
	/** Reset the observer arrays to accomodate more patients
	 * @param newN The new (increased) number of patients
	 */
	public void increaseNmidway(int newN) {
		// Find which arrays need modification (which is being observed)
		// Create new arrays of the rigth size
		// Copy the info from the old arrays
		
		if(newN <= H_p_i.length) {return;}

		if (obsH) {
			double[][] newH_p_i = increaseDouble_newN(H_p_i, newN);
			H_p_i = newH_p_i.clone();
		}
		if(obsN) {
			double[][] newN_p_i = increaseDouble_newN(N_p_i, newN);
			N_p_i = newN_p_i.clone();
		}
		if(obsC) {
			int[][][] newC_p_w_i = new int[newN][care.providers.numObjs][arraysLength];
		}
		if(obsT) {
			double[][] newT_p_i = increaseDouble_newN(T_p_i, newN);
			T_p_i = newT_p_i.clone();
		}
		if(obsE) {
			double[][][] newE_p_w_i = increaseTriple_newN(E_p_w_i, newN);
			E_p_w_i = newE_p_w_i;
		}
		if(obsB) {
			int[][][] newB_p_w_i = new int[newN][care.providers.numObjs][arraysLength];
		}
		if(obsSimpleC) {
			int[][] newsimple_C_p_i = increaseDouble_newN(simple_C_p_i, newN);
			//simple_C_p_i = new int[newN][arraysLength];
			simple_C_p_i = newsimple_C_p_i.clone();
			
		}
		if(obsSimpleE) {
			double[][] newsimple_E_p_i = increaseDouble_newN(simple_E_p_i, newN);
			simple_E_p_i = newsimple_E_p_i.clone();
		}
		if(obsSimpleB) {
			int[][] newsimple_B_p_i = new int[newN][arraysLength];
		}
		if(obsDelta) {			
			double[][] newdelta_p_i = increaseDouble_newN(delta_p_i , newN);
			delta_p_i  = newdelta_p_i.clone();
		}

		
	}
	
	/** Reset the observer arrays to accomodate more providers
	 * @param newN The new (increased) number of patients
	 */
	public void increaseWmidway(int newW) {


		if(obsC) {
			if(C_p_w_i[0].length < newW) {
			int[][][] newC_p_w_i = increaseTriple_newW(C_p_w_i, newW);
			C_p_w_i = newC_p_w_i.clone();
		}}
		if(obsE) {
			if(E_p_w_i[0].length < newW) {
			double[][][] newE_p_w_i = increaseTriple_newW(E_p_w_i, newW);
			E_p_w_i = newE_p_w_i.clone();
		}}
		if(obsB) {
			if(B_p_w_i[0].length < newW) {
			int[][][] newB_p_w_i = increaseTriple_newW(B_p_w_i, newW);
			B_p_w_i = newB_p_w_i.clone();
		}}

	}

	protected double[][] increaseDouble_newN(double[][] oldArr_p_i, int newN) {
		double[][] newArr_p_i = new double[newN][arraysLength];
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
	
	protected int[][] increaseDouble_newN(int[][] oldArr_p_i, int newN) {
		int[][] newArr_p_i = new int[newN][arraysLength];
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
	
	protected double[][][] increaseTriple_newN(double[][][] oldArr_p_i, int newN) {
		double[][][] newArr_p_w_i = new double[newN][care.providers.numObjs][arraysLength];
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
	
	protected double[][][] increaseTriple_newW(double[][][] oldArr_p_w_i, int newW){
		double[][][] newArr_p_w_i = new double[care.patients.numObjs][newW][arraysLength];
		//copy information from old array into new
		for(int p = 0; p< care.patients.numObjs;p++) {
			for(int w = 0; w<oldArr_p_w_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_w_i[p][w][i];
				}
			}
		}
		//populate the new W´s backwards with -1s
		for(int p = 0; p< care.patients.numObjs;p++) {
			for(int w = oldArr_p_w_i[0].length; w<newW; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}
	
	protected int[][][] increaseTriple_newW(int[][][] oldArr_p_w_i, int newW){
		int[][][] newArr_p_w_i = new int[care.patients.numObjs][newW][arraysLength];
		//copy information from old array into new
		for(int p = 0; p< care.patients.numObjs;p++) {
			for(int w = 0; w<oldArr_p_w_i[0].length; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = oldArr_p_w_i[p][w][i];
				}
			}
		}
		//populate the new W´s backwards with -1s
		for(int p = 0; p< care.patients.numObjs;p++) {
			for(int w = oldArr_p_w_i[0].length; w<newW; w++) {
				for(int i=0; i< windowNumber; i++ ) {
					newArr_p_w_i[p][w][i] = -1;
				}
			}
		}
		return newArr_p_w_i;
	}

}