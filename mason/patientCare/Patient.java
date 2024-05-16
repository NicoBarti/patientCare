package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare = 0;
	protected double severity;
	protected double motivation;
	private double S_p;

	public void step(SimState state) {
		Care care = (Care) state;
		// excecute PROGRESSION sub-model
		progress(care);

		// excecute SEEK sub-model
		if (seek(care)) {
			// if received care excecute RECEIVE CARE sub-model
			receiveCare(care);
		} 
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