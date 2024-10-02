package patientCare;

//import java.util.ArrayList;
//import java.util.List;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	//params
	public int capacity = 500;
	public int numPatients = 34000;
	public int weeks = 52;
	
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
	
	public int[] getCareDistribution() {
		int[] distro = new int[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = 0;
			//distro[i] = ((Patient) (patients.objs[i])).getReceivedCare();
		return distro;
	}
	
	public double[][] getMotivationDistribution() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = 0;
				//distro[i][ii] = ((Patient) (patients.objs[i])).patientMotDist[ii];
			}
		}
		return distro;
	}
	
	public double[][] getSeverityDistribution() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = 0;
				//distro[i][ii] = ((Patient) (patients.objs[i])).patientSevDist[ii];
			}
		}
		return distro;
	}

}
