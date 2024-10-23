package patientCare;

//import java.util.ArrayList;
//import java.util.List;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;

public class Care extends SimState {
	//constrains
	public int capacity = 5;
	public int numPatients = 200;
	public int weeks = 52;
	
	// PARAMS:
	public double SEVERITY_ALLOCATION = 0;
	public double CONTINUITY_ALLOCATION = 0;
 	
	// HYPERPARAMETERS:
	public double k = 1;
	
	// internals
	public Doctor doctor;
	public Bag patients = new Bag(numPatients);
	
	// VISUALIZATION
	//I'll implement all the objects for visualization here and control them from here
	//so I don't interfere with the other uses of the simulation, and don't touch patients and docs
	// some methods will only be called if there is a visualization
	public boolean visual = true;
	public Continuous2D center = new Continuous2D(1.0,100,100);
    public Network visualPatients = new Network(false);
    // eventually the Bag patients could be substituted for the Network visualPatients
	

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
		
		// clear the center
        center.clear();
        // clear the visualPatients
        visualPatients.clear();
        
        // should i clear the Bag? I think so, add in the other branches
        patients.clear();
		
		// initialize and add Doctor
		doctor = new Doctor();
		if(visual) { // add doctor to  visualization: TO DO!!
			//visualPatients.addNode(doctor);
			Double2D docPos = new Double2D(center.getWidth() * 0.5,
	        		center.getHeight() * 0.5);
			center.setObjectLocation(doctor, docPos);
		}
		doctor.initializeDoctor(capacity, numPatients);
		schedule.scheduleRepeating(schedule.EPOCH, 0, doctor);

		// initialize and add patients
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.initializePatient(random.nextDouble(), weeks, i);
			patients.add(patient);
			allocationRule(patient.getReceivedCare(), patient.getd(), i);
			//schedule.scheduleOnce(schedule.EPOCH, 1, patient);
			if(visual) { // add this patient for visualization:
				//visualPatients.addNode(patient);
				visualPatients.addEdge(doctor,patient, 1);
			}
		}
		updatePositions(0);
		
		  if (visual) 		// anonymus agent that actualizes network positions
		  {schedule.scheduleRepeating(schedule.EPOCH, 0, new Steppable()
		  {public void step(SimState state) {updatePositions((int)state.schedule.getSteps());}});}
	
	  
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
	

	public void updatePositions(int week) {
		int diameter = 4;
		double doctorMargin = 0.1;
		double movement = 10;
		for(int i = 0; i < numPatients; i++) {
			Patient patient = (Patient) (patients.objs[i]);
			double angle = (2*Math.PI)/numPatients * patient.id;
			double distanceFromDoctor = 1-patient.getcurrentMot();

			//double distanceFromDoctor = Math.max(doctorMargin, (diameter - patient.getcurrentMot())*diameter);
			//double distanceFromDoctor = (diameter - patient.getexpectation()[week]+doctorMargin)*movement;
			Double2D newposition = new Double2D(
					center.getWidth() * 0.5 + Math.cos(angle) * distanceFromDoctor*10,
	        		center.getHeight() * 0.5 + Math.sin(angle)* distanceFromDoctor*10);
			center.setObjectLocation(patient, newposition);
			//System.out.println(newposition);
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
