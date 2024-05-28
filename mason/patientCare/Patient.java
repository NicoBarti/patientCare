package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare = 0;
	protected double severity;
	protected double motivation;
	private double S_p;
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
			patientMotDist[(int)care.schedule.getSteps()] = motivation;
			patientSevDist[(int)care.schedule.getSteps()] = severity;
		} else {System.out.println("52 weeks reached, patients are not registering their severity and motivation any longer");}
		
	}

	private void progress(Care care) {
		this.severity += care.random.nextGaussian() * 0.01;
		this.motivation += care.random.nextGaussian() * 0.01;
	}

	private boolean seek(Care care) {
		// compute the patient`s seek probability
		S_p = ((1 - care.patientCentredness) * severity + care.patientCentredness * motivation)/2;
		// a random draw from a Bernoully (its CDF) to determine seek behaviour
		if(care.random.nextDouble() < S_p) {
			if (care.askAppointment()) {
				return true;
			} else return false;
		} return false;
	}

	private void receiveCare(Care care) {
		receivedCare += 1;
		severity -= care.effectivennes * care.random.nextDouble();
		motivation += care.continuity * care.random.nextDouble();
	}

	public int getReceivedCare() {
		return receivedCare;
	}
	
	public double getCurrentMotivation() {
		return motivation;
	}
	
	public double getCurrentSeverity(){
		return severity;
	}

}