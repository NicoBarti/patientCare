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
		delta(patient);
		capN(patient);
		capE(patient);
		rho(patient);
		eta(patient);
		psi(patient);
		iota(patient); //iota depends on the caps
		progressProbability(patient); //progressProbability depends on delta
		kappa(patient);

		//initialize state variables
		h(patient);
		t(patient);
		e(patient);
		c(patient);
		b(patient);
	}
	
	public void h(Patient patient) {
		switch(strategy) {
		case "random": patient.h_i_1 = care.random.nextDouble()*patient.capN;
			break;
		default: patient.h_i_1 = 0;
		}}
	
	public void t(Patient patient) {
		switch(strategy) {
		case "random": patient.t_i_1 = care.random.nextDouble()*care.tau;
			break;
		default: patient.t_i_1 = 0;
		}}
	
	public void e(Patient patient) {
		patient.e_i_1 = new double[care.W];
		switch(strategy) {
		case "random": 
			for(int i=0;i<patient.e_i_1.length;i++) {
				patient.e_i_1[i] = care.random.nextDouble()*patient.capE;
			}
			break;
		default: 
			for(int i=0;i<patient.e_i_1.length;i++) {
				patient.e_i_1[i] = patient.capE/2;
			}
		}}
	
	public void c(Patient patient) {
		patient.c_i_1 = new int[care.W];
		switch(strategy) {
		case "random": 
			patient.c_i_1[care.random.nextInt(care.W)] = 1;
			break;
		default: // all 0s 
		}}
	
	public void b(Patient patient) {
		patient.b_i_1 = new int[care.W];
		switch(strategy) {
		case "random": 
			patient.b_i_1[care.random.nextInt(care.W)] = 1;
			break;
		default: // all 0s 
		}}

	public void delta(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.delta = care.random.nextDouble()*care.maxDelta;
			break;
		default: 
			patient.delta = 1;}
	}
	
	public void progressProbability(Patient patient) {
			patient.progressProbability = (float)patient.delta/care.weeks;
	}

	public void capN(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.capN = care.random.nextDouble()*care.maxDelta;
			break;
		default: 
			patient.capN = 5;}
	}
	
	public void rho(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.rho = care.random.nextDouble();
			break;
		default: 
			patient.rho = .5;}
	}
	
	public void eta(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.eta = care.random.nextDouble();
			break;
		default: 
			patient.eta = .5;}
	}
	
	public void capE(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.capE = care.random.nextDouble()*care.maxDelta;
			break;
		default: 
			patient.capE = 5;}
	}
	
	public void psi(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.psi = care.random.nextDouble();
			break;
		default: 
			patient.psi = .5;}
	}
	
	public void iota(Patient patient) {
		patient.iota = 1/(patient.capN + patient.psi*(patient.capE - patient.capN));
		
	}
	
	public void kappa(Patient patient) {
		switch(strategy) {
		case "random": 
			patient.kappa = care.random.nextDouble()*3;
			break;
		default: 
			patient.kappa = .01;}
	}
}
