package patientCare;

import sim.engine.SimState;
import sim.util.*;

import sim.engine.Steppable;

public class ObserveCare implements Steppable{
	double[][] H_p_i;
	int[][][] C_p_w_i;
	int[][][] B_p_w_i;
	double[][] H_norm_p_i;

	
	//internals
	int arraysLength;

	Care care;
	Patient patient;
	
	//internals
	int period;
	int counter = 0;
	int windowNumber = 0;
	
	Boolean testing = false;
	
	//Time setep 0 is for initial conditions (the observe() method should be called from Care start()
	//At the end of each time step (order N+1), the observer evaluates if it needs to save the state variables.
	
	public ObserveCare(Care sim, int value) {
		care = sim;
		period = value;
		int nWindows = (int)(care.varsigma/period);
		int remainder = 0;
		if(nWindows*period<care.varsigma) {
			remainder = 1;}
		arraysLength = 1 + nWindows +remainder; //initial conditions - observation windows - reminder
		
		H_p_i = new double[care.N][arraysLength];
		C_p_w_i = new int[care.N][care.W][arraysLength];
		B_p_w_i = new int[care.N][care.W][arraysLength];
		H_norm_p_i = new double[care.N][arraysLength];
		
	}
	
	public void step(SimState state) {
		if(counter == 0) { //record initial conditions
			observe(windowNumber);
			windowNumber+=1;
		}
		if(counter == period) {
			observe(windowNumber);
			windowNumber+=1;
			counter=0;
		}
		counter+=1;
	}
	
	public void observe(int loc) {
		observeC(loc);
		observeH(loc);
		observeB(loc);
		observeH_norm(loc);
	}
	
	
	public void endObservation() {
		observe(windowNumber);
	}
	
	public void observeC(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				C_p_w_i[patient.p][w][loc] = patient.c_p_i_1[w];
			}}
	}
	
	public void observeH(int loc) {
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			H_p_i[patient.p][loc] = patient.h_p_i_1;
	}}
	
	public void observeH_norm(int loc) {
		//Normalize the current disease evolution by the maximum possible evolution: (disease severity/52)*time_steps
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			H_norm_p_i[patient.p][loc] = patient.h_p_i_1/(patient.delta_p);
	}}
	
	
	public void observeB(int loc){
		for(int p = 0; p<care.N;p++) {
			patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				B_p_w_i[patient.p][w][loc] = patient.b_p_i_1[w];
			}}
	}
	
	public int[][][] getC(){return C_p_w_i;}
	public double[][] getH(){return H_p_i;}
	public int[][][] getB(){return B_p_w_i;}
	public int getarraysLengthreturn() {return arraysLength;}
	public double[][] getH_norm(){return H_norm_p_i;}
	
//	public double[] getFinal_H_norm() {
//		int last_array_cell = H_p_i[0].length-1;
//		Bag patients = care.patients;
//		double[] H_norm = new double[patients.numObjs];
//		for(int orderedPatient=0;orderedPatient<H_p_i.length;orderedPatient++) {
//			for(int bagPatient = 0; bagPatient<patients.numObjs;bagPatient++) {
//				if(((Patient)patients.objs[bagPatient]).p == orderedPatient) {
//					patient = (Patient)patients.objs[bagPatient];
//				}
//			}
//			H_norm[orderedPatient] = H_p_i[orderedPatient][last_array_cell]/patient.delta_p;
//		}
//		return H_norm;
//	}

	

	
}