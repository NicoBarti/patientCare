package patientCare;

//import java.util.ArrayList;
//import java.util.List;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	public int capacity = 25;
	public int numPatients = 1000;
	public double E = 0;
	public double RCpos = 0;
	public double RCneg = 0;
	public int weeks;
	public double Gd = 0;
	public double k = 1;
	public double m0 = 0;
	public int[] capUse;
	private double stratInitializer;
	private int strat3 = 0;
	private int strat2 = 0;
	private int doctorAvailability;
	public Bag patients = new Bag(numPatients);

	public Care(long seed) {
		super(seed);
	}

	public int askAppointment(double strat) {

		if (this.doctorAvailability > 0) {
			provideTreatment();
			return (1);
		} else {
			return (0);
		}	
	}

	public void provideTreatment() {
		this.doctorAvailability -= 1;
	}

	
	public void start() {
		if(weeks < 1) {
			System.out.println("!! weeks parameter: " + weeks);
		}
		
		super.start();
		
		//initialize capacity counter
		capUse = new int[weeks];

		// add anonumus agent that resets doctor availability at each week
		schedule.scheduleRepeating(schedule.EPOCH, 0, new Steppable() {
			public void step(SimState state) {
				capUse[(int)schedule.getSteps()] = doctorAvailability;
				resetDoctorAvailability();
			}
		});

		// initialize patients
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			if (i < numPatients/3) { //first stratum
				stratInitializer = Gd;
				} else 
			if (i < 2*numPatients/3) { // second stratum
				stratInitializer = Gd*2;
			} else { //third stratum
				stratInitializer = Gd*3;
				}
			patient.initialize(weeks, stratInitializer);
			patient.R = random.nextGaussian()/100;
			patient.S = random.nextGaussian()/100;
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
		System.out.println("BEWARE Starting from Care Java!!");
		doLoop(Care.class, args);
		System.exit(0);
	}
	
	public void setGd(double val) {
		Gd = val;
	}
	
	public double getGd() {
		return Gd;
	}
	
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

	public void setE(double val) {
		E = val;
	}

	public double getE() {
		return E;
	}

	public void setRCpos(double val) {
		RCpos = val;
	}

	public double getRCpos() {
		return RCpos;
	}
	
	public void setRCneg(double val) {
		RCneg = val;
	}

	public double getRCneg() {
		return RCneg;
	}
	
//	public void setRUp(double val) {
	//		RUp = val;
	//	}
	//	public double getRUp() {
	//		return RUp;
	//}
	//public void setRLow(double val) {
	//		RLow = val;
	//}
	//public double getRLow() {
	//		return RLow;
	//}
	//public void setCLow(int val) {
	//		CLow = val;
	//}
	//public int getCLow() {
	//		return CLow;
	//}
	//public void setCUp(int val) {
	//		CUp = val;
	//}
	//public int getCUp() {
	//		return CUp;
	//}
	public void setk(double val) {
		k = val;
	}
	public double getk() {
		return k;
	}
	public void setm0(double val) {
		m0 = val;
	}
	public double getm0() {
		return m0;
	}
	public int[] getcapUse() {
		return capUse;
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
			distro[i] = ((Patient) (patients.objs[i])).getReceivedCare();
		return distro;
	}
	
	public double[] getProgressionDistribution() {
		double[] distro = new double[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = ((Patient) (patients.objs[i])).getProgression();
		return distro;
	}
	
	public double[] getStratDistribution() {
		double[] distro = new double[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = ((Patient) (patients.objs[i])).getStrat();
		return distro;		
	}
	
	
	//	public int[] getCapacityDistribution() {
	//		int[] distro = new int[patients.numObjs];
	//		for (int i = 0; i < patients.numObjs; i++)
	//			distro[i] = ((Patient) (patients.objs[i])).C;
	//		return distro;		
	//	}
	
	public double[] getProbabilityDistribution() {
		double[] distro = new double[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = ((Patient) (patients.objs[i])).P;
		return distro;		
	}
	
	public double[][] getExpectationDistribution() {
	double[][] distro = new double[patients.numObjs][weeks];
	for (int i = 0; i < patients.numObjs; i++) {
		for (int ii = 0; ii < weeks; ii++) {
			distro[i][ii] = ((Patient) (patients.objs[i])).patientExpDist[ii];
		}
	}
	return distro;
	}
	
	public double[][] getSatisfactionDistribution() {
	double[][] distro = new double[patients.numObjs][weeks];
	for (int i = 0; i < patients.numObjs; i++) {
		for (int ii = 0; ii < weeks; ii++) {
			distro[i][ii] = ((Patient) (patients.objs[i])).patientSatDist[ii];
		}
	}
	return distro;	
	
	}
	
	public double[][] getPDistribution() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = ((Patient) (patients.objs[i])).patientPDist[ii];
			}
		}
		return distro;	
		}

	public double[][] getPatientProgress() {
		double[][] distro = new double[patients.numObjs][weeks];
		for (int i = 0; i < patients.numObjs; i++) {
			for (int ii = 0; ii < weeks; ii++) {
				distro[i][ii] = ((Patient) (patients.objs[i])).patientProgress[ii];
			}
		}
		return distro;	
		}


}
