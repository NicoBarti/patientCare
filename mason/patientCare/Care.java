package patientCare;

//import java.util.ArrayList;
//import java.util.List;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	//params
	public int capacity = 1;
	public int numPatients = 1;
	public int weeks = 10;
	
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
			schedule.scheduleRepeating(schedule.EPOCH, 1, patient);
		}

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
	

	
	public int[][] getCs() {
		int[][] distro = new int[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			int[] data = ((Patient) (patients.objs[i])).getReceivedCare();
			for (int ii = 0; ii < weeks; ii++) {
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
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getH();
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
	public double[][] getexpectations() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getexpectation();
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
	public double[][] getTs() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			double[] data = ((Patient) (patients.objs[i])).getT();
			for (int ii = 0; ii < weeks; ii++) {
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
		int[][] distro = new int[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			int[] data = ((Patient) (patients.objs[i])).getB();
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = data[ii];
			}
		}
		return distro;
	}
	
}
