package patientCare;

//import java.util.ArrayList;
//import java.util.List;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	//constrains
	public int capacity = 1;
	public int numPatients = 5;
	public int weeks = 10;
	
	// PARAMS:
	public double SEVERITY_ALLOCATION = 0;
	public double CONTINUITY_ALLOCATION = 0;
 	
	// HYPERPARAMETERS:
	public double k = 1;
	
	// internals
	public Doctor doctor;
	public Bag patients = new Bag(numPatients);

	public Care(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		System.out.println("BEWARE Starting from Care Java!!");
		doLoop(Care.class, args);
		System.exit(0);
	}

	public void start() {
		super.start();
		
		// initialize and add Doctor
		doctor = new Doctor();
		doctor.initializeDoctor(capacity, numPatients);
		schedule.scheduleRepeating(schedule.EPOCH, 0, doctor);

		// initialize and add patients
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.initializePatient(random.nextDouble(), weeks, i);
			patients.add(patient);
			allocationRule(patient.getReceivedCare(), patient.getd(), i);
			//schedule.scheduleOnce(schedule.EPOCH, 1, patient);
		}

	}
	
	public void allocationRule(int[] C_i, int d, int id) {
		// The schedule priorities are like this:
		// 0 for the doctor to reopen their agendas
		// 1,..,6 for the priorities of patients
		int priority = 6;
		int visit_counter = 0;
		if(random.nextDouble() < SEVERITY_ALLOCATION) {
			priority = priority-d+1; //d 1 doents get promoted, 2 gets one, 3 gets 2 promotions
		}
		if(random.nextDouble() < CONTINUITY_ALLOCATION) {
			
			for (int i =0; i < weeks+1; i++) {
				visit_counter = visit_counter + C_i[i];
			}
			if(visit_counter > 0) {
				priority = priority - 1;
			}
		}
		
		schedule.scheduleOnce((Patient)patients.objs[id],priority);
		//System.out.println("Patient d = "+d+" counter = "+ visit_counter+" priority "+priority);
	}
	

	public void finish() {
	}
	
	// setters and getters
	public void setCapacity(int val) {
		capacity = val;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setNumPatients(int val) {
		numPatients = val;
	}
	public int getNumPatients() {
		return numPatients;
	}
	public void setk(double val) {
		k = val;
	}
	public double getk() {
		return k;
	}
	public void setweeks(int val) {
		weeks = val;
	}
	public int getweeks() {
		return weeks;
	}
	public void setSEVERITY_ALLOCATION(double val) {
		SEVERITY_ALLOCATION = val;
	}
	public double getSEVERITY_ALLOCATION() {
		return SEVERITY_ALLOCATION;
	}
	public void setCONTINUITY_ALLOCATION(double val) {
		CONTINUITY_ALLOCATION = val;
	}
	public double getCONTINUITY_ALLOCATION() {
		return CONTINUITY_ALLOCATION;
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
	
}
