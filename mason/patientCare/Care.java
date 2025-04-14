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
	//constrains
	public int capacity = 10;
	public int numPatients = 100;
	public int weeks = 150;
	
	// PARAMS:
	public double SEVERITY_ALLOCATION = 0;
	public double CONTINUITY_ALLOCATION = 0;
	public double EXP_POS = 1;
	public double EXP_NEG = 1;
	public double DISEASE_SEVERITY = 1;
	public double LEARNING_RATE = 1;
	public double SUBJECTIVE_INITIATIVE = 0.5;
 	
	// HYPERPARAMETERS:
	public double t = 5; // max treatment effectivennes
	public int[] nDiseases = {1,1,1};
	
	// internals
	public Doctor doctor;
	public Bag patients = new Bag(numPatients);
	public int totalInteractions = 0;
	public int totalTreatment = 0;
	private int d;
	
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
		doctor.initializeDoctor(capacity, numPatients, t, LEARNING_RATE);
		schedule.scheduleRepeating(schedule.EPOCH, 0, doctor);
		// initialize and add patients
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			if(i < numPatients/3) {d = nDiseases[0];} 
			else if (i < 2*numPatients/3) {d = nDiseases[1];}
			else {d = nDiseases[2];}
			patient.initializePatient(d, weeks, i, this);
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
		int diameter = 7;
		double doctorMargin = 0.1;
		//double movement = 10;
		for(int i = 0; i < numPatients; i++) {
			Patient patient = (Patient) (patients.objs[i]);
			double angle = (2*Math.PI)/numPatients * patient.id;
			double distanceFromDoctor = doctorMargin+(5-patient.getExpectativas())*diameter;

			//double distanceFromDoctor = Math.max(doctorMargin, (diameter - patient.getcurrentMot())*diameter);
			//double distanceFromDoctor = (diameter - patient.getexpectation()[week]+doctorMargin)*movement;
			Double2D newposition = new Double2D(
					center.getWidth() * 0.5 + Math.cos(angle) * distanceFromDoctor,
	        		center.getHeight() * 0.5 + Math.sin(angle)* distanceFromDoctor);
			center.setObjectLocation(patient, newposition);
			//System.out.println(newposition);
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
	public void setEXP_POS(double val) {EXP_POS = val;}
	public void setEXP_NEG(double val) {EXP_NEG = val;}
	public void setnDiseases(int[] n) {
		nDiseases[0] = n[0];
		nDiseases[1] = n[1];
		nDiseases[2] = n[2];
		}
	

	public int 	  getCapacity() {return capacity;}
	public int 	  getNumPatients() {return numPatients;}
	public double gett() {return t;}
	public int 	  getweeks() {return weeks;}
	public double getSEVERITY_ALLOCATION() {return SEVERITY_ALLOCATION;}
	public double getCONTINUITY_ALLOCATION() {return CONTINUITY_ALLOCATION;}
	public double getDISEASE_SEVERITY() {return DISEASE_SEVERITY;}
	public double getLEARNING_RATE() {return LEARNING_RATE;}
	public double getSUBJECTIVE_INITIATIVE() {return SUBJECTIVE_INITIATIVE;}
	public double getEXP_POS() {return EXP_POS;}
	public double getEXP_NEG() {return EXP_NEG;} 
	public int[] getnDiseases() {return nDiseases;}
	
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
