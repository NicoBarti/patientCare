package patientCare;
import sim.util.*;


public class PatientInitializer {
	Care care;
	String strategy;
	
	//for Fixed strategies
	double fixed_delta;
	double fixed_capN;
	double fixed_lambda;
	double fixed_tau;
	double fixed_rho;
	double fixed_eta;
	float fixed_kappa;
	double fixed_capE;
	double fixed_psi;	
	
	public PatientInitializer(Care _care, String _strategy) {
		care = _care;
		strategy = _strategy;
		switch(strategy) {
			case  "random-basal":
				care.W =  Math.max(care.random.nextInt(care.N),1);
				care.totalCapacity = care.random.nextInt(care.N);
			break;
			
			case "sensitivity_1":
				//initialize Care level (maybe this can be handled elsewhere)
				care.totalCapacity = care.random.nextInt(4999)+1;
				care.N = care.random.nextInt(4999)+1;
				care.W = care.random.nextInt(49)+1;
				//initialize fixed params
				fixed_delta = care.random.nextDouble()*10;
				fixed_capN = care.random.nextDouble()*10;
				fixed_lambda = care.random.nextDouble()*10;
				fixed_tau = care.random.nextDouble()*10;
				fixed_rho = care.random.nextDouble()*10;
				fixed_eta = care.random.nextDouble()*10;
				fixed_kappa = care.random.nextFloat(true, true);
				fixed_capE = care.random.nextDouble(true,true)*10;
				fixed_psi = care.random.nextDouble();
				strategy = "apply_fixed";
			break;
				
			case "basal":
				fixed_delta = 3;
				fixed_capN = 5;
				fixed_rho = 1;
				fixed_eta = 1;
				fixed_kappa = 0.1F;
				fixed_capE = 5;
				fixed_psi = 0.5;				
				strategy = "apply_fixed";
			break;	
		
		}
	}

	//random-basal strategy: fixed N, varsigma, meanDelta
	
	public void initialize(Patient patient) {
		//initialize control variables first; pay attention to dependency
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
		case "random-basal": patient.h_p_i_1 = care.random.nextDouble()*patient.capN_p;break;
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
			int remainderCapacity = care.totalCapacity;
			while(remainderCapacity>0) {
				patient.c_p_i_1[care.random.nextInt(care.W)] = 1;
				remainderCapacity -=1;
			}
			break;
		default: 
			for(int w = 0; w < care.W; w++) {
				patient.c_p_i_1[w] = 0;
			}
		}}
	
	public void b(Patient patient) {
		patient.b_p_i_1 = new int[care.W];
		patient.b_p_i = new int[care.W];
		switch(strategy) {
		case "random": 
			patient.b_p_i_1[care.random.nextInt(care.W)] = 1;
			break;
		default: 
			for(int w = 0; w < care.W; w++) {
				patient.c_p_i_1[w] = 0;
			}
		}}

	public void delta_p(Patient patient) {
		switch(strategy) {
		case "random-basal":
			patient.delta_p = care.random.nextDouble()*10;
			break;
		case "apply_fixed":
			patient.delta_p = fixed_delta;
			break;
		default: 
			patient.delta_p = 1;}
	}
	
	public void progressProbability(Patient patient) {
			patient.progressProbability = (float)(patient.delta_p/52);
	}

	public void capN_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.capN_p = care.random.nextDouble()*10;
			break;
		case "apply_fixed":
			patient.capN_p = fixed_capN;
			break;
		default: 
			patient.capN_p = 5;}
	}
	
	public void rho_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.rho_p = care.random.nextDouble();
			break;
		case "apply_fixed":
			patient.rho_p = fixed_rho;
			break;
		default: 
			patient.rho_p = .5;}
	}
	
	public void eta_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.eta_p = care.random.nextDouble();
			break;
		case "apply_fixed":
			patient.eta_p = fixed_eta;
			break;
		default: 
			patient.eta_p = .5;}
	}
	
	public void capE_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.capE_p = care.random.nextDouble()*10;
			break;
		case "apply_fixed":
			patient.capE_p = fixed_capE;
			break;
		default: 
			patient.capE_p = 5;}
	}
	
	public void psi_p(Patient patient) {
		switch(strategy) {
		case "random-basal": 
			patient.psi_p = care.random.nextDouble();
			break;
		case "apply_fixed":
			patient.psi_p = fixed_psi;
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
		case "apply_fixed":
			patient.kappa_p = fixed_kappa;
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
