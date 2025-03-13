package patientCare;

import sim.util.Bag;

public class AisoletedMechanisms {
	
	public AisoletedMechanisms() {}
	
	double[] runAisolatedBiologicalMechanism(int weeks, double DISEASE_VELOCITY, 
			int d, int numPatients, int seed) {
		
		final long long_seed;
		
		if(seed == 0) {
			long_seed =  System.currentTimeMillis();
		} else {
			long_seed = (long) seed;
		}
		
		Care care = new Care(long_seed);
		Bag patients = new Bag(numPatients);

		care.setweeks(weeks);
		care.setDISEASE_VELOCITY(DISEASE_VELOCITY);
		care.setNumPatients(0); //a very light simulation to not waste computation
		care.start();
		
		for (int i = 0; i < numPatients; i++) {
			Patient patient = new Patient();
			patient.initializePatient(d, weeks, i, care);
			patients.add(patient);

		}
		for(int iiii = 0; iiii < weeks; iiii++) {
			for(int ii = 0; ii < patients.numObjs; ii++) {
				((Patient)(patients.objs[ii])).current_week = (int)care.schedule.getSteps() +1;
				((Patient)(patients.objs[ii])).biologicalMechanism(care);
			}
			care.schedule.step(care);
		}
		double[] Ns = new double[patients.numObjs];
		for (int i = 0; i < patients.numObjs; i++) {
			Ns[i] = ((Patient)(patients.objs[i])).getNecesidades();
		}
		return Ns;
	}

}
