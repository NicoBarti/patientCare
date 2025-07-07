package patientCare;

import java.util.Map;
import java.util.HashMap;

import sim.engine.*;
import sim.util.*;


public class Care extends SimState {
	
	//control variables (params)
	public int N = 1000;
	public int varsigma = 150; 
	public int W = 2;
	public String Pi = "base policy";
	public String PROVIDER_INIT = "base model";
	public String PATIENT_INIT = "base model";

	
	// internals
	public int obsSteps = 50;
	public Bag providers;
	public Bag patients;
	public Appointer appointer;
	public Prioritizator prioritize;
	Patient patient;
	Provider provider;
	ProviderInitializer prov_init;
	PatientInitializer pat_init;
	ObserveCare observer;
	
	public Care(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		System.out.println("BEWARE Starting from Care.java with hard-coded params.");
		doLoop(Care.class, args);
		System.exit(0);
	}

	public void start() {
		super.start();
		
		prioritize = new Prioritizator(this, Pi);
		prov_init = new ProviderInitializer(this, PROVIDER_INIT);
		pat_init = new PatientInitializer(this, PATIENT_INIT);
		observer = new ObserveCare();
		observer.initialize(obsSteps, this);
		
		providers = new Bag(W);
		patients = new Bag(N);
		
		// create and initialize providers
		for(int i =0;i<W;i++) {
			provider = new Provider();
			provider.w = i;
			prov_init.initialize(provider);
			providers.add(provider);
			schedule.scheduleRepeating(schedule.EPOCH,0,provider); //providers are stepped first thing at each step
		}
		
		// create and initialize patients
		for (int i = 0; i < N; i++) {
			patient = new Patient();
			patient.p = i;
			pat_init.initialize(patient);
			patients.add(patient);
			schedule.scheduleOnce(schedule.EPOCH, prioritize.hat_o(patient), patient); 
		}
		
		//create anonymus agent that scheddules patients wit priority hat_o 
		//this agent acts at the end of each state (order N+1)
		schedule.scheduleRepeating(schedule.EPOCH, N+1, new Steppable(){ //copied
				public void step(SimState state) { 
					for(int i=0;i<patients.numObjs;i++) {
						schedule.scheduleOnce((Patient)(patients.objs[i]),prioritize.hat_o(patient));
					}}
				});
		
		schedule.scheduleRepeating(schedule.EPOCH, N+2, observer);
		
		appointer = new Appointer(this);
	}
	
	
	public void finish() {
			}
	
	// setters and getters
	public void setN(int val) {N = val;}
	public int getN() {return N;}
	public void setvarsigma(int val) {varsigma = val;}
	public int getvarsigma() {return varsigma;}
	public void setW(int val) {W = val;}
	public int getW() {return W;}
	public void setPi(String val) {Pi = val;}
	public String getPi() {return Pi;}
	public void setPROVIDER_INIT(String val) {PROVIDER_INIT = val;}
	public String getPROVIDER_INIT() {return PROVIDER_INIT;}
	public void setPATIENT_INIT(String val) {PATIENT_INIT = val;}
	public String getPATIENT_INIT() {return PATIENT_INIT;}

	
//	public HashMap getParams() {
//		HashMap<String, String> params = new HashMap();
//		params.put("capacity", Integer.toString(getCapacity()));
//		
//		params.put("numPatients", Integer.toString(getNumPatients()));
//		params.put("t", Double.toString(gett()));
//		params.put("weeks", Integer.toString(getweeks()));
//		params.put("SEVERITY_ALLOCATION", Double.toString(getSEVERITY_ALLOCATION()));
//		params.put("CONTINUITY_ALLOCATION", Double.toString(getCONTINUITY_ALLOCATION()));
//		params.put("DISEASE_SEVERITY", Double.toString(getDISEASE_SEVERITY()));
//		params.put("LEARNING_RATE", Double.toString(getLEARNING_RATE()));
//		params.put("SUBJECTIVE_INITIATIVE", Double.toString(getSUBJECTIVE_INITIATIVE()));
//		params.put("EXP_POS", Double.toString(getEXP_POS()));
//		params.put("EXP_NEG", Double.toString(getEXP_NEG()));
//		return params;
//	}
//	
//	public int[][] getCs() {
//		int[][] distro = new int[patients.numObjs][weeks+1];
//		for (int i = 0; i < patients.numObjs; i++) {
//			int[] data = ((Patient) (patients.objs[i])).getReceivedCare();
//			for (int ii = 0; ii < weeks+1; ii++) {
//				distro[i][ii] = data[ii];
//			}
//		}
//		return distro;
//	}
//	
//	public int[] getds() {
//		int[] distro = new int[patients.numObjs];
//		for (int i = 0; i < patients.numObjs; i++) {
//			distro[i] = ((Patient) (patients.objs[i])).getd();
//		}
//		return distro;
//	}
//	
//	public double[][] getHs() {
//		double[][] distro = new double[patients.numObjs][weeks+1];
//		for (int i = 0; i < patients.numObjs; i++) {
//			double[] data = ((Patient) (patients.objs[i])).getH();
//			for (int ii = 0; ii < weeks+1; ii++) {
//				distro[i][ii] = data[ii];
//			}
//		}
//		return distro;
//	}
//	
//	public double[][] getexpectations() {
//		double[][] distro = new double[patients.numObjs][weeks+1];
//		for (int i = 0; i < patients.numObjs; i++) {
//			double[] data = ((Patient) (patients.objs[i])).getexpectation();
//			for (int ii = 0; ii < weeks+1; ii++) {
//				distro[i][ii] = data[ii];
//			}
//		}
//		return distro;
//	}
//	
//	public double[][] getTs() {
//		double[][] distro = new double[patients.numObjs][weeks+1];
//		for (int i = 0; i < patients.numObjs; i++) {
//			double[] data = ((Patient) (patients.objs[i])).getT();
//			for (int ii = 0; ii < weeks+1; ii++) {
//				distro[i][ii] = data[ii];
//			}
//		}
////		System.out.println("Ts Distribution obtained ar Care.java");
////		for(int i = 0; i < weeks; i++) {
////			System.out.print(distro[0][i] + " ");
////		}
//		return distro;
//	}
//	
//	public int[][] getBs() {
//
//		int[][] distro = new int[patients.numObjs][weeks+1];
//		for (int i = 0; i < patients.numObjs; i++) {
//			int[] data = ((Patient) (patients.objs[i])).getB();
//			for (int ii = 0; ii < weeks+1; ii++) {
//				distro[i][ii] = data[ii];
//			}
//		}
//		return distro;
//	}	
//	
//	public int[] gettotalProgress() {
//		int[] totalProgress = new int[patients.numObjs];
//		for (int i = 0; i < patients.numObjs; i++) {
//			totalProgress[i] = ((Patient) (patients.objs[i])).gettotalProgress();
//		}
//		return totalProgress;
//		} 

}
