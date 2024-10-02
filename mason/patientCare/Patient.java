package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare = 0;
	protected int d;
	private double expectation;
	private double healthStatus;
	private int progress;
	private int Id;
	private double B;
	protected double[] patientMotDist = new double[53];
	protected double[] patientSevDist = new double[53];
	

	public void step(SimState state) {
		Care care = (Care) state;
		// excecute PROGRESSION sub-model
		progress(care);

		// excecute SEEK sub-model
		if (seek(care)) {
			// if received care excecute RECEIVE CARE sub-model
			receiveCare(care);
		} 
		
		//save current motivation and severity
		if(care.schedule.getSteps() < 53) {
			/*
			 * patientMotDist[(int)care.schedule.getSteps()] = motivation;
			 * patientSevDist[(int)care.schedule.getSteps()] = severity;
			 */
		} else {System.out.println("52 weeks reached, patients are not registering their severity and motivation any longer");}
		
	}
	
	public void initializePatient(double severity, int id) {
		if(severity < 0.33) {d = 1;} 
		else if(severity < 0.66) {d = 2;}
		else {d = 3;}
		healthStatus = 0;
		progress = 0;
		Id = id;
		B = 0.5;
		expectation = 0.5;
	}

	private void progress(Care care) {
		if(care.random.nextDouble()<d/52) {
			progress = 1;
		} else {progress = 0;}
		healthStatus = healthStatus + progress;
	}
	
	private int behaviouralRule(Care care) {
		final double m = 
		if(care.random.nextDouble() < (expectation + healthStatus)){
			return 1;
		} else {return 0;}
	}
	
	private void expectationFormation(boolean receivedT) {
		if(receivedT) {
			expectation = expectation/2 + 0.5;
		} else {
			expectation = expectation/2 - 0.5;
		}
	}

	private boolean seek(Care care) {
		// compute the patient`s seek probability
		//S_p = ((1 - care.patientCentredness) * severity + care.patientCentredness * motivation)/2;
		// a random draw from a Bernoully (its CDF) to determine seek behaviour
		if(care.random.nextDouble() < S_p) {
			if (care.askAppointment()) {
				return true;
			} else return false;
		} return false;
	}

	private void receiveCare(Care care) {
		receivedCare += 1;
		healthStatus = 
		severity -= care.effectivennes * care.random.nextDouble();
		motivation += care.continuity * care.random.nextDouble();
	}

	public int getReceivedCare() {
		return receivedCare;
	}
	

}