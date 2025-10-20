package patientCare;
import sim.util.*;
import sim.engine.*;


/**
 * Finds an appointment for a patient. It's instantiated at Care start(). The appoint() method is called by the Patients' "interact" subroutines.
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
	 * @return provider index if available, or -1 if no provider available; treatment:
	 */
	public double[] appoint(int w, int p, double h) { 

		double[] result = new double[] {-1, 0}; // default result [w,t]
		
		//localize desired provider
		for(int ow =0;ow<providers.numObjs;ow++) {
			if(((Provider)providers.objs[ow]).w == w) {
				//System.out.println("Provider localized:" +w);
				wanted_provider = (Provider)providers.objs[ow];
			}
		}

		if(wanted_provider.isAvailable()) {
			result[0] = wanted_provider.w;
			result[1]= wanted_provider.interactWithPatient(p, h);
		}
		
		else {
			// avoid w+1 receiving all the overflow from w // but this step might be unecesary because Bag is unordered
			int[] accessArray = ut.accessArray(care.W, care.random.nextInt(care.W));
			for(int rw=0; rw< accessArray.length; rw++) { //try assignment with the order in accessArray
				other_provider = (Provider)(providers.objs[accessArray[rw]]);
				if(other_provider.isAvailable()) {
					result[0] = other_provider.w;
					result[1]=other_provider.interactWithPatient(p,h);
					break;
				}
			}
		}

		return result;
	}
	
}
