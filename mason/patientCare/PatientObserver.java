package patientCare;
import sim.util.*;

public class PatientObserver {

	Care care;
	Patient patient;
	Bag patients;
	
	public PatientObserver(Care c) {
		care = c;
	}
	
	double[] geth(Bag patients) {
		double[] result = new double[patients.numObjs];
		for (int i = 0; i<patients.numObjs;i++) {
			result[i] = ((Patient)patients.objs[i]).h_p_i_1;
		}
		return result;
	}
	double geth(Patient patient) {
		double result;
		result = patient.h_p_i_1;
		return result;
	}

}
