package patientCare;

//import java.util.ArrayList;
//import java.util.List;
import java.util.Map;
import java.util.HashMap;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;

public class Care extends SimState {
	//system's constrains
	public int capacity = 10;
	public int N = 100;
	public int weeks = 150; //time-steps
	public int W = 1;
	public double maxDelta = 5;
	//enforce delta can't be > weeks, because progressprobability > 1
	
	// PARAMS:
	public double delta = 0;
	public double CONTINUITY_ALLOCATION = 0;
	public double rho = 1;
	public double eta = 1;
	public double DISEASE_SEVERITY = 1;
	public double lambda = 1;
	public double SUBJECTIVE_INITIATIVE = 0.5;
	public double tau = 5; // max treatment effectivennes
	
	// internals
	public Provider provider;
	public Bag doctors = new Bag(W);
	public Bag patients = new Bag(numPatients);
	public int totalTreatment = 0;
	private int d;
	
	public Appointer appointer;
	
	public Care(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		System.out.println("BEWARE Starting from Care Java with hard-coded params.");
		doLoop(Care.class, args);
		System.exit(0);
	}

	public void start() {
		super.start();
		
		appointer = new Appointer(this);
        patients.clear();
		
		// initialize and add Doctor
		doctor = new Doctor();
		doctor.initializeDoctor(capacity, numPatients, t, LEARNING_RATE);
		
		schedule.scheduleRepeating(schedule.EPOCH, 0, doctor);
		// initialize and add patients
		d = 1; // all patients get one disease
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.initializePatient(d, weeks, i, this);
			patients.add(patient);
			schedule.scheduleRepeating(schedule.EPOCH,1,patient);
		}	  
	}
	
	
	public void finish() {
			}
	
	// setters and getters
	public void setCapacity(int val) {capacity = val;}
	public void setNumPatients(int val) {numPatients = val;}
	public void sett(double val) {t = val;}
	public void setweeks(int val) {weeks = val;}
	public void setSEVERITY_ALLOCATION(double val) {SEVERITY_ALLOCATION = val;}
	public void setCONTINUITY_ALLOCATION(double val) {CONTINUITY_ALLOCATION = val;}
	public void setDISEASE_SEVERITY(double val) {DISEASE_SEVERITY = val;}
	public void setLEARNING_RATE(double val) {LEARNING_RATE = val;}
	public void setSUBJECTIVE_INITIATIVE(double val) {SUBJECTIVE_INITIATIVE = val;}
	public void setEXP_POS(double val) {rho = val;}
	public void setEXP_NEG(double val) {eta = val;}	

	public int 	  getCapacity() {return capacity;}
	public int 	  getNumPatients() {return numPatients;}
	public double gett() {return t;}
	public int 	  getweeks() {return weeks;}
	public double getSEVERITY_ALLOCATION() {return SEVERITY_ALLOCATION;}
	public double getCONTINUITY_ALLOCATION() {return CONTINUITY_ALLOCATION;}
	public double getDISEASE_SEVERITY() {return DISEASE_SEVERITY;}
	public double getLEARNING_RATE() {return LEARNING_RATE;}
	public double getSUBJECTIVE_INITIATIVE() {return SUBJECTIVE_INITIATIVE;}
	public double getEXP_POS() {return rho;}
	public double getEXP_NEG() {return eta;} 
	
	public HashMap getParams() {
		HashMap<String, String> params = new HashMap();
		params.put("capacity", Integer.toString(getCapacity()));
		
		params.put("numPatients", Integer.toString(getNumPatients()));
		params.put("t", Double.toString(gett()));
		params.put("weeks", Integer.toString(getweeks()));
		params.put("SEVERITY_ALLOCATION", Double.toString(getSEVERITY_ALLOCATION()));
		params.put("CONTINUITY_ALLOCATION", Double.toString(getCONTINUITY_ALLOCATION()));
		params.put("DISEASE_SEVERITY", Double.toString(getDISEASE_SEVERITY()));
		params.put("LEARNING_RATE", Double.toString(getLEARNING_RATE()));
		params.put("SUBJECTIVE_INITIATIVE", Double.toString(getSUBJECTIVE_INITIATIVE()));
		params.put("EXP_POS", Double.toString(getEXP_POS()));
		params.put("EXP_NEG", Double.toString(getEXP_NEG()));
		return params;
	}
	
	public int[][] getCs() {
		int[][] distro = new int[patients.numObjs][weeks+1];
		for (int i = 0; i < patients.numObjs; i++) {
			int[] data = ((Patient) (patients.objs[i])).getReceivedCare();
			for (int ii = 0; ii < weeks+1; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
	public int[] getds() {
		int[] distro = new int[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++) {
			distro[i] = ((Patient) (patients.objs[i])).getd();
		}
		return distro;
	}
	
	public double[][] getHs() {
		double[][] distro = new double[patients.numObjs][weeks+1];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getH();
			for (int ii = 0; ii < weeks+1; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
	public double[][] getexpectations() {
		double[][] distro = new double[patients.numObjs][weeks+1];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getexpectation();
			for (int ii = 0; ii < weeks+1; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
	public double[][] getTs() {
		double[][] distro = new double[patients.numObjs][weeks+1];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getT();
			for (int ii = 0; ii < weeks+1; ii++) {
				distro[i][ii] = data[ii];
			}
		}
//		System.out.println("Ts Distribution obtained ar Care.java");
//		for(int i = 0; i < weeks; i++) {
//			System.out.print(distro[0][i] + " ");
//		}
		return distro;
	}
	
	public int[][] getBs() {

		int[][] distro = new int[patients.numObjs][weeks+1];
		for (int i = 0; i < patients.numObjs; i++) {
			int[] data = ((Patient) (patients.objs[i])).getB();
			for (int ii = 0; ii < weeks+1; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}	
	
	public int[] gettotalProgress() {
		int[] totalProgress = new int[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++) {
			totalProgress[i] = ((Patient) (patients.objs[i])).gettotalProgress();
		}
		return totalProgress;
		} 

}
