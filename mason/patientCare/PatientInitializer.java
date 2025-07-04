package patientCare;

public class PatientInitializer {
	Care care;
	String strategy;
	
	public PatientInitializer(Care _care, String _strategy) {
		care = _care;
		strategy = _strategy;
	}

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
		e(patient);
		c(patient);
		b(patient);
	}
	
	public void h(Patient patient) {
		switch(strategy) {
		case "random": patient.h_p_i_1 = care.random.nextDouble()*patient.capN_p;
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
		case "random": 
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
		case "random": 
			patient.c_p_i_1[care.random.nextInt(care.W)] = 1;
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
		case "random": 
			patient.delta_p = care.random.nextDouble()*10;
			break;
		default: 
			patient.delta_p = 1;}
	}
	
	public void progressProbability(Patient patient) {
			patient.progressProbability = (float)patient.delta_p/care.varsigma;
	}

	public void capN_p(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.capN_p = care.random.nextDouble()*10;
			break;
		default: 
			patient.capN_p = 5;}
	}
	
	public void rho_p(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.rho_p = care.random.nextDouble();
			break;
		default: 
			patient.rho_p = .5;}
	}
	
	public void eta_p(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.eta_p = care.random.nextDouble();
			break;
		default: 
			patient.eta_p = .5;}
	}
	
	public void capE_p(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.capE_p = care.random.nextDouble()*10;
			break;
		default: 
			patient.capE_p = 5;}
	}
	
	public void psi_p(Patient patient) {
		switch(strategy) {
		case "random": 
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
		case "random": 
			patient.kappa_p = care.random.nextDouble()*3;
			break;
		default: 
			patient.kappa_p = .01;}
	}
}
