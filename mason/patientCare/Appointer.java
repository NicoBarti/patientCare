package patientCare;
import sim.util.*;
import sim.engine.*;


/**
 * Finds an appointment for a patient. It's instantiated at Care start(). The appoint() method is called by the Patients' "interact" subroutines.
 * When a provider doesn't longer exist, notify the patient to change its expectations regrding that provider.
 */
public class Appointer implements Steppable {
	private static final long serialVersionUID = 1L;
	Bag providers;
	Care care;
	myUtil ut = new myUtil();
	Provider wanted_provider;
	Provider other_provider;
	
	//internals
	Boolean testing = false;
	
	public Appointer(Care c) {
		providers = c.providers;
		care = c;
	}
	
	public void step(SimState state) {}

	/**
	 * @param w The doctor's ID that the patient wants an appointment with.
	 * @param p The patient's ID requesting the appointment
	 * @param h The patien's health status
	 * @return [;] provider index if available, or -1 if no provider available; treatment:
	 */
	public double[] appoint(int w, int p, double h) { 

		double[] result = new double[] {-1, 0}; // default result [w,t]
		
		//localize desired provider		
		wanted_provider = null;
		for(int ow =0;ow<providers.numObjs;ow++) {
			if(((Provider)providers.objs[ow]).w == w) {
				wanted_provider = (Provider)providers.objs[ow];
				break;
			}
		}
		//if your desired provider isn't gone
		if(wanted_provider != null) { 
			if(wanted_provider.isAvailable()) { 
				//and is available
				result[0] = wanted_provider.w;
				result[1]= wanted_provider.interactWithPatient(p, h);
			} else { 
				//not available so interact with other provider
				result = interactWithRandomAvailableProvider(p,h);}
		} else { 
			// provider doesn't exist, notify patient and interact with other available provider
			result = interactWithRandomAvailableProvider(p,h);
			notifyPatientProviderNoLongerExists(p, w);
		}
		return result;
	}
	
	private double[] interactWithRandomAvailableProvider(int p, double h) {
		double[] result = new double[] {-1, 0};
		care.providers.shuffle(care.random);
		for(int ww=0;ww<care.providers.numObjs;ww++) {
			if(((Provider)care.providers.get(ww)).isAvailable()) {
				result = new double[] {((Provider)care.providers.get(ww)).w, ((Provider)care.providers.get(ww)).interactWithPatient(p,h)};	
				break;}
		}
		return result;
	}
	
	private void notifyPatientProviderNoLongerExists(int p, int w) {
		for(int pp=0;pp<care.patients.numObjs;pp++) {
			if(((Patient)care.patients.get(pp)).p == p) {
				((Patient)care.patients.get(pp)).removeExpectations(w);
				break;
			}
		}
	} 
}