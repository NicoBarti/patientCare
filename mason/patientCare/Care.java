package patientCare;
import sim.engine.*;
import sim.util.*;

public class Care extends SimState {
	public int numPatients = 34062;
	public int careValue = 1;	
	public double severityTrheshold =0.7;
	public double motivationTrheshold = 1.8;
	public int capacity = 481;
	
	private int doctorAvailability;
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
		
	public void start() {
		super.start();

		// set initial capacity
		//setCapacity(capacity); //Don´t need this line, try erase it
		resetDoctorAvailability();

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
			public void step(SimState state) {resetDoctorAvailability();}
		});

	}
	
	private void resetDoctorAvailability() {doctorAvailability = capacity;}

	public void finish() {}
		
	public static void main(String[] args) {
		doLoop(Care.class, args);
		System.exit(0);	
	}

}
