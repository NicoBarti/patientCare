package patientCare;
import sim.util.*;


public class Appointer {
	Bag providers;
	Care care;
	
	public Appointer(Care c) {
		providers = care.doctors;
		care = c;
	}

	public double[] appoint(int w, int id, double d) { 
		//w: desired provided
		//id: patient identifier
		// returns: provider index if available, or -1 if no provider available; treatment: 
		//result: [w,t]
		double[] result = new double[] {-1, 0}; // default result
		
		if(((Provider)(providers.objs[w])).isAvailable()) {
			result[0] = w;
			result[1]=((Provider)(providers.objs[w])).interactWithPatient(id, d);
		}
		
		else {
			//int[] accessArray = randomArray(care); // avoid w+1 receiving all the overflow from w
			myUtil ut = new myUtil();
			int[] accessArray = ut.accessArray(care.W, care.random.nextInt(care.W));
			for(int rw=0; rw< accessArray.length; rw++) { 
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
