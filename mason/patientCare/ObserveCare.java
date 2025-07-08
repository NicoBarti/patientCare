package patientCare;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ObserveCare implements Steppable{
	double[][] H_p_i;
	int[][][] C_p_w_i;
	int[][][] B_p_w_i;

	Care care;
	Patient patient;
	
	//internals
	protected int counter = 0;
	protected int freq = 1;
	
	
	
	public void step(SimState state) {
		care = (Care)state;
		//if(care.schedule.getSteps() ==0 | care.schedule.getSteps() == care.varsigma-1) {
		observe();
		//}
		//count();
	}
	
	public void observe() {
		observeC();
		observeH();
		observeB();
	}
	
	public void observeC(){
		for(int p = 0; p<care.N;p++) {patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				C_p_w_i[p][w][(int)care.schedule.getSteps()] = patient.c_p_i_1[w];
			}}
	}
	
	public void observeH() {
		for(int p = 0; p<care.N;p++) {patient = ((Patient)care.patients.objs[p]);
			H_p_i[p][(int)care.schedule.getSteps()] = patient.h_p_i_1;
			//System.out.println(H_p_i[p][(int)care.schedule.getSteps()]);
	}}
	
	public void observeB(){
		for(int p = 0; p<care.N;p++) {patient = ((Patient)care.patients.objs[p]);
			for(int w = 0; w<care.W;w++) {
				B_p_w_i[p][w][(int)care.schedule.getSteps()] = patient.b_p_i_1[w];
			}}
	}
	
	public int[][][] getC(){return C_p_w_i;}
	public double[][] getH(){return H_p_i;}
	public int[][][] getB(){return B_p_w_i;}

	
	public void initialize(int value, Care care) {
		freq = value;
		C_p_w_i= new int[care.N][care.W][care.varsigma];
		H_p_i = new double[care.N][care.varsigma];
		B_p_w_i= new int[care.N][care.W][care.varsigma];

	}
	
	protected void count() {
		if (counter  == freq) {
			observe();
			counter = 0;}
		counter +=1;
	}
	
}