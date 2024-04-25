package patientCare;

import sim.engine.*;


public class Patient implements Steppable{
	private int receivedCare = 0;
	protected double severity;
	protected double baselineMotivation;	
	
	public void step(SimState state) {
		Care care = (Care) state;
		this.severity += care.random.nextDouble()*0.02;
		//this.severity += care.random.nextDouble()*0.05;
		//only ask for appointment if i´m motivated
		if (baselineMotivation + care.random.nextDouble() > care.motivationTrheshold) {
		//only ask for appointments if I´m very sick
		if (severity > care.severityTrheshold) {	
		//System.out.println(receivedCare);
		if (care.askAppointment()) {
			this.receivedCare += care.careValue;
			care.provideTreatment();
			// their severity will decrease
			this.severity -= 0.01;
			// their motivation will increase
			this.baselineMotivation = this.baselineMotivation + 0.1;
			
		} 
		}
		}
	}
	
	public int getReceivedCare() { return receivedCare; }

}
