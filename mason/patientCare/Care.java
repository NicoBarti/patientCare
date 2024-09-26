package patientCare;

import java.util.ArrayList;
import java.util.List;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	//params
	public int capacity = 500;
	public int numPatients = 34000;
	public short weeks = 52;

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

	

	private int doctorAvailability;
	public Bag patients = new Bag(numPatients);

	public Care(long seed) {
		super(seed);
	}

	public boolean askAppointment() {
		if (this.doctorAvailability > 0) {
			provideTreatment();
			return (true);
		}
		return (false);
	}

	public void provideTreatment() {
		this.doctorAvailability -= 1;
	}

	public int[] getCareDistribution() {
		int[] distro = new int[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = ((Patient) (patients.objs[i])).getReceivedCare();
		return distro;
	}
	
	public double[][] getMotivationDistribution() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = ((Patient) (patients.objs[i])).patientMotDist[ii];
			}
		}
		return distro;
	}
	
	public double[][] getSeverityDistribution() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = ((Patient) (patients.objs[i])).patientMotDist[ii];
			}
		}
		return distro;
	}

	public void start() {
		super.start();

		// add anonymus agent that resets doctor availability at each week
		schedule.scheduleRepeating(schedule.EPOCH, 0, new Steppable() {
			public void step(SimState state) {
				resetDoctorAvailability();
			}
		});

		// initialize patients
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.initializePatient(random.nextDouble());
			//patient.motivation = 1;
			patients.add(patient);
			schedule.scheduleRepeating(schedule.EPOCH, 1, patient);
		}

	}

	private void resetDoctorAvailability() {
		doctorAvailability = capacity;
	}

	public void finish() {
	}

	public static void main(String[] args) {
		doLoop(Care.class, args);
		System.exit(0);
	}

}
