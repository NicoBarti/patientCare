package patientCare;
import sim.util.*;


public class Appointer {
	Bag providers;
	Care care;
	myUtil ut = new myUtil();
	
	public Appointer(Care c) {
		providers = c.providers;
		care = c;
	}

	public double[] appoint(int w, int id, double d) { 
		//w: desired provided
		//id: patient identifier
		// returns: provider index if available, or -1 if no provider available; treatment: 
		double[] result = new double[] {-1, 0}; // default result [w,t]
		
		if(((Provider)(providers.objs[w])).isAvailable()) {
			result[0] = w;
			result[1]=((Provider)(providers.objs[w])).interactWithPatient(id, d);
		}
		
		else {
			// avoid w+1 receiving all the overflow from w
			int[] accessArray = ut.accessArray(care.W, care.random.nextInt(care.W));
			for(int rw=0; rw< accessArray.length; rw++) { //try assignment with the order in accessArray
				if(((Provider)(providers.objs[accessArray[rw]])).isAvailable()) {
					result[0] = accessArray[rw];
					result[1]=((Provider)(providers.objs[accessArray[rw]])).interactWithPatient(id, d);
					break;
				}
			}
		}
		return result;
	}
	
}
