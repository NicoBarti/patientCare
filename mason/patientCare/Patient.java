package patientCare;

import sim.engine.*;

public class Patient implements Steppable {
	private int receivedCare = 0;
	protected double severity;
	protected double motivation;

	public void step(SimState state) {
		Care care = (Care) state;
		// excecute PROGRESSION sub-model
		progress(care);

		// excecute SEEK sub-model
		if (seek(care)) {
			// if received care update patient visit counter
			this.receivedCare += 1;
			// if received care excecute RECEIVE CARE sub-model
			receiveCare(care);
		}
	}

	private void progress(Care care) {
		this.severity += care.random.nextGaussian() * 0.1;
		this.motivation += care.random.nextGaussian() * 0.1;
	}

	private boolean seek(Care care) {
		// check threshold
		if ((1 - care.patientCentredness) * severity + care.patientCentredness * motivation > care.serviceTrheshold) {
			// check doctor availability
			if (care.askAppointment()) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	private void receiveCare(Care care) {
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