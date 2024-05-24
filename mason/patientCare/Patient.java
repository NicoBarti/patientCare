package patientCare;

import sim.engine.*;


public class Patient implements Steppable{
	private int receivedCare = 0;
	protected double severity;
	protected double baselineMotivation;	
	protected int id;
	private boolean priority = false;
	
	public void step(SimState state) {
		//priority = false;
		Care care = (Care) state;
		this.severity += care.random.nextGaussian()*0.1;
		//only ask for appointment if i´m motivated
		if (baselineMotivation + care.random.nextGaussian() > care.motivationTrheshold) {
		//only ask for appointments if I´m very sick
		if (severity > care.severityTrheshold) {	
		if (care.askAppointment()) {
			this.receivedCare += 1;
			care.provideTreatment();
			// their severity will decrease
			this.severity -= Math.abs(care.random.nextGaussian())*0.5;
			// their motivation will increase
			this.baselineMotivation = this.baselineMotivation + care.random.nextGaussian()*2;
			
			//sometimes, the doctor will aks for a follow up
			if(care.random.nextDouble() < 0.6) {
				setPriority();
				care.schedule.scheduleOnce(this, 0); //scheddule first priority next cicle
				}
		} 
		}
		}
		if(!priority) { //scheddule normal priority for next cycle
			care.schedule.scheduleOnce(this, 1);
		}
	}
	
	public int getReceivedCare() { return receivedCare; }
	
	private void setPriority() {
		priority = true;
	}

}
