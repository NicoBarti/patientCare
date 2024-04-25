package patientCare;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	public int numPatients = 34062;
	public int careValue = 1;	
	private int doctorAvailability;
	public double severityTrheshold = 0.85;
	public double motivationTrheshold = 1.8;
	
	private int capacity = 481;
	public Bag patients = new Bag(numPatients);
	
	public Care(long seed) {
		super(seed);
	}
	
	public boolean askAppointment() {
		if (this.doctorAvailability > 0) {return(true);}
		return(false);
	}
	
	public void provideTreatment() {
		this.doctorAvailability -= 1;
	}
	
	public void setCapacity(int val) {if (val >= 0) {capacity = val;}}
	public int getCapacity() {return capacity;}
	public void setNumPatients(int val) {if (val >= 0) numPatients = val; }  
	public int getNumPatients() { return numPatients; }
	public void setSeverityTrheshold(double val) {if (val >= 0) severityTrheshold = val; }  
	public double getSeverityTrheshold() { return severityTrheshold; }
	public void setMotivationTrheshold(double val) {if (val >= 0) {motivationTrheshold = val;}}
	public double getMotivationTrheshold() {return motivationTrheshold;}

	public int[] getCareDistribution() {
		int[] distro = new int[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++)
			distro[i] = ((Patient)(patients.objs[i])).getReceivedCare();
		return distro;
	}
	
	public int[] getTreatedDistribution() { //gets rids of 0s
		int[] allCare = new int[patients.numObjs]; // first extracts all the received care
		int nonzerocounter = 0; //keeps track of non-zeros
		for (int i = 0; i < patients.numObjs; i++) {
			allCare[i] = ((Patient)(patients.objs[i])).getReceivedCare();
			if (allCare[i] > 0) { nonzerocounter += 1; }
		}
		int[] distro = new int[nonzerocounter]; //creates definitive array to be returned
		int ii = 0;
		for (int i = 0; i < patients.numObjs; i++) {//this is a manual filter on allCare array
			if (allCare[i] > 0) {
				distro[ii] = allCare[i];
				ii += 1;
			}
		}
		return(distro);
	}
	
	
	private void resetCapacity() {
		doctorAvailability = capacity;
	}
	
	public void start() {
		super.start();
		//System.out.println("starting");
		//System.out.println("Number of patients: "+patients.numObjs);
		// set initial capacity
		setCapacity(capacity);
		resetCapacity();
		
		//add patients
		for(int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.severity = random.nextDouble();
			patient.baselineMotivation = random.nextDouble()*2;
			patients.add(patient);
			schedule.scheduleRepeating(schedule.EPOCH, 1, patient);
		}
		
		//add anonymus agent that resets availability to 1
		schedule.scheduleRepeating(schedule.EPOCH, 2, new Steppable() {
			public void step(SimState state) {resetCapacity();}
		});

	}
	
	public void finish() {
		String path = "/Users/Nico/Desktop/proyectos_P/PUENTE/data/sim6.csv";
		System.out.println("Saving the distributions to " + path);
		
		int[] distr0 = getCareDistribution();		
		List<String[]> list = new ArrayList<>();
		String[] frequency = {"counts"};
		list.add(frequency);
		for (int i = 0; i < distr0.length; i++) {
			String[] a = {Integer.toString(distr0[i])};
			list.add(a);
		}
		try (CSVWriter writer = new CSVWriter (new FileWriter(path))) {
			writer.writeAll(list);
		} catch (IOException e) {
			System.out.println("Problem in CS Writer "+e);
		}
	}
	
		
	public static void main(String[] args) {
		doLoop(Care.class, args);
		System.out.println("Finalizando con " + args[0] + " " + args[1]);
		System.exit(0);	
	}

}
