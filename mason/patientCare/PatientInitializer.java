package patientCare;
import sim.util.*;


public class PatientInitializer {
	Care care;
	String strategy;
	
	int totalCapacity;
	
	public PatientInitializer(Care _care, String _strategy) {
		care = _care;
		strategy = _strategy;
		if (strategy == "random-basal") {
			care.W =  Math.max(care.random.nextInt(care.N),1);
			totalCapacity = care.random.nextInt(care.N);
		}
	}

	//random-basal strategy: fixed N, varsigma, meanDelta
	
	public void initialize(Patient patient) {
		//initialize control variables first
		delta_p(patient);
		capN_p(patient);
		capE_p(patient);
		rho_p(patient);
		eta_p(patient);
		psi_p(patient);
		iota_p(patient); //iota_p depends on the caps
		progressProbability(patient); //progressProbability depends on delta_p
		kappa_p(patient);

		//initialize state variables
		h(patient);
		t(patient);
		e(patient); //in some strategies depends on capE // depends on W
		c(patient); // depends on W, N
		b(patient); // depends on W, N
	}
	
	
	public void h(Patient patient) {
		switch(strategy) {
		case "random-basal": patient.h_p_i_1 = care.random.nextDouble()*patient.capN_p;
			break;
		default: patient.h_p_i_1 = 0;
		}}
	
	public void t(Patient patient) {
		switch(strategy) {
		default: patient.t_p_i_1 = 0;
		}}
	
	public void e(Patient patient) {
		patient.e_p_i_1 = new double[care.W];
		patient.e_p_i = new double[care.W];
		switch(strategy) {
		case "random-basal": 
			for(int i=0;i<patient.e_p_i_1.length;i++) {
				patient.e_p_i_1[i] = care.random.nextDouble()*patient.capE_p;
			}
			break;
		default: 
			for(int i=0;i<patient.e_p_i_1.length;i++) {
				patient.e_p_i_1[i] = patient.capE_p/2;
			}
		}}
	
	public void c(Patient patient) {
		patient.c_p_i_1 = new int[care.W];
		patient.c_p_i = new int[care.W];
		switch(strategy) {
		case "random-basal": 
			int remainderCapacity = totalCapacity;
			while(remainderCapacity>0) {
				patient.c_p_i_1[care.random.nextInt(care.W)] = 1;
				remainderCapacity -=1;
			}
			break;
		default: // all 0s 
		}}
	
	public void b(Patient patient) {
		patient.b_p_i_1 = new int[care.W];
		patient.b_p_i = new int[care.W];
		switch(strategy) {
		case "random": 
			patient.b_p_i_1[care.random.nextInt(care.W)] = 1;
			break;
		default: // all 0s 
		}}

	public void delta_p(Patient patient) {
		switch(strategy) {
		case "random-basal":
			patient.delta_p = care.random.nextDouble()*10;
		default: 
			patient.delta_p = 1;}
	}
	
	public void progressProbability(Patient patient) {
			patient.progressProbability = (float)(patient.delta_p/care.varsigma);
	}

	public void capN_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.capN_p = care.random.nextDouble()*10;
			break;
		default: 
			patient.capN_p = 5;}
	}
	
	public void rho_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.rho_p = care.random.nextDouble();
			break;
		default: 
			patient.rho_p = .5;}
	}
	
	public void eta_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.eta_p = care.random.nextDouble();
			break;
		default: 
			patient.eta_p = .5;}
	}
	
	public void capE_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.capE_p = care.random.nextDouble()*10;
			break;
		default: 
			patient.capE_p = 5;}
	}
	
	public void psi_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.psi_p = care.random.nextDouble();
			break;
		default: 
			patient.psi_p = .5;}
	}
	
	public void iota_p(Patient patient) {
		patient.iota_p = 1/(patient.capN_p + patient.psi_p*(patient.capE_p - patient.capN_p));
		
	}
	
	public void kappa_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.kappa_p = (float)care.random.nextDouble();
			break;
		default: 
			patient.kappa_p = (float).01;}
	}
	
	public void setdelta(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).delta_p = value;
			progressProbability((Patient)patients.objs[i]); //progressProbability depends on delta_p

		}
	}
	
	public void setpsi(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).psi_p = value;
			iota_p((Patient)patients.objs[i]); //iota_p depends on the caps
		}
	}
	
	public void setcapN(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).capN_p = value;
			iota_p((Patient)patients.objs[i]); //iota_p depends on the caps

		}
	}
	
	public void setcapE(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).capE_p = value;
			iota_p((Patient)patients.objs[i]); //iota_p depends on the caps

		}
	}
	
	public void setrho(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).rho_p = value;
		}
	}
	
	public void seteta(Bag patients, double value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).eta_p = value;
		}
	}
	
	public void setkappa(Bag patients, float value) {
		for(int i=0;i<patients.numObjs;i++) {
			((Patient)patients.objs[i]).kappa_p = value;
		}
	}
	
	public void setexpectations(Bag patients, double value) {
		for(int p=0;p<patients.numObjs;p++) {
			for(int w=0;w<care.W;w++) {
				((Patient)patients.objs[p]).e_p_i_1[w] = value;
			}
		}
	}
	
	public void sethealtstatus(Bag patients, double value) {
		for(int p=0;p<patients.numObjs;p++) {
			((Patient)patients.objs[p]).h_p_i_1 = value;
		}
	}
	
	public void settesting(Bag patients, Boolean value) {
		for(int p=0;p<patients.numObjs;p++) {
			((Patient)patients.objs[p]).testing = value;
		}
	}
	
	
}
