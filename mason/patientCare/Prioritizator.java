package patientCare;
import sim.engine.*;

public class Prioritizator implements Steppable {
	private static final long serialVersionUID = 1L;
	
	Care care;
	String policy;
	
	//internals
	private int order_basal_policy = 10;
	private int priority;
	
	public void step(SimState state) {};
	
	public Prioritizator(Care c,String p) {
		care = c;
		switch(p){
		case "basal":
			policy = "basal";
			break;
		case "H_segmented":
			policy = "H_segmented";
			break;
		case "patient_centred":
			policy = "patient_centred";
			break;
		case "risk":
			policy = "risk";
			break;
		case "need":
			policy = "need";
			break;
		case "risk_need":
			policy = "risk_need";
			break;
		default:
			policy ="none";
		}}
	
	
	/** Assigns a priority to a patient. The maximum priority (minimum integer) should be 2.
	 * @param patient
	 * @return priority
	 */
	public int hat_o(Patient patient) {
		switch(policy) {
		case "basal":
			priority = basal(patient);
			break;
		case "H_segmented":
			priority = H_segmented(patient);
			break;
		case "patient_centred":
			priority = patient_centred(patient);
			break;
		case "risk":
			priority = risk(patient);
			break;
		case "need":
			priority = need(patient);
			break;
		case "risk_need":
			priority = risk_need(patient);
			break;
		default:
			System.out.println("Java (Prioritizator) Can't set policy to "+policy);
			System.out.println("Exiting");
			System.exit(0);
		}
		return priority;
		}
	
	/** Gives the max priority. This is used to assure that other agents, for instance the observer, come into the simulation after all patients.
	 * @return
	 */
	public int maxPriority() {
		switch(policy) {
		case "default":
			return priority = order_basal_policy;
		case "H_segmented":
			return 10002;
		case "patient_centred":
			return 2002;
		case "risk":
			return 6;
		case "need":
			return (int)((care.pat_init.max_capN *1000)+2);
		case "risk_need":
			return (int)((care.pat_init.max_capN + care.pat_init.max_delta)*1000+2);
		}
		return priority;
	}

	public void changePolicy(String p) {
		policy =p;
	}
	
	public String getPolicy() {return policy;}
	
	private int basal(Patient patient) {
		return order_basal_policy;
		}
	//boolean temporaryTest = false;
	private int H_segmented(Patient patient) {
		//For H between 0 and 1000
		// H 0 gets priority 10001; H 1000 gets priority 1;
		// priority 0 is reserved for 
		//Handle inactive patients first
		if(patient.h_p_i_1 == -1) {return order_basal_policy;}
		//Handle active patients latter
		int factor;
		if(patient.h_p_i_1 > 1000) {
			 factor = 1000;
			System.out.println("Java (Prioritizator) H for a patient exceded 1000! Not good.");
		} else { factor = (int)patient.h_p_i_1*10;}
		int prioriy = 10002 - factor;
		//if(temporaryTest) {
		//System.out.println("(Prioritizator) Giving priority "+ prioriy + " to patient "+patient.p+" with health " + patient.h_p_i_1);}
		return prioriy;
	}
	
	private int patient_centred(Patient patient) {
		//For N between 0 and capN, and E between 0 and capE
		//Npriority = 1002  for N = 0; and 2 for N = capN
		//Epriority = 0 for E = 0; and 1000 for E = capE
		//Final priority is Npriority + Npriority
		//Handle inactive patients first
		if(patient.h_p_i_1 == -1) {return order_basal_policy;}
		//Handle active patients latter
		int Npriority; int Epriority;
		Npriority = (int)(1002 - (patient.n_p_i/patient.capN_p)*1000);
		Epriority = (int)((patient.get_MeanE()/patient.capE_p)*1000);
		//int result = Npriority+Epriority;
		//System.out.println("(Prioritizator) p "+ patient.p+" N = "+patient.n_p_i+" E = "+patient.get_MeanE()+ " yields "+result);
		
		return Npriority+Epriority;
	}
	
	private int risk(Patient patient) {
		if (patient.delta_p <= care.pat_init.max_delta/3) { 
			//System.out.println("(Prioritizator) patient "+patient.p +" delta "+patient.delta_p +" assign 5 (max_delta = "+ care.pat_init.max_delta+")");
			return 5;}
		if (patient.delta_p <= 2*care.pat_init.max_delta/3) { 
			//System.out.println("(Prioritizator) patient "+patient.p +" delta "+patient.delta_p +" assign 4 (max_delta = "+ care.pat_init.max_delta+")" );
			return 4;}
		//System.out.println("(Prioritizator) patient "+patient.p +" delta "+patient.delta_p +" assign 3 (max_delta = "+ care.pat_init.max_delta+ ")");

		return 3;
	}
	
	private int need(Patient patient) {
		//System.out.println("(Prioritizator) Patient "+patient.p+" has n_p_i " + patient.n_p_i  + " and was given priority "+(int)((care.pat_init.max_capN - patient.n_p_i)*1000+2) + 
		//		" care.pat_init.max_capN is "+care.pat_init.max_capN);
		return (int)((care.pat_init.max_capN - patient.n_p_i)*1000+2);
	}
	
	private int risk_need(Patient patient) {
		return (int)((care.pat_init.max_capN + 2 - patient.n_p_i + care.pat_init.max_delta - patient.delta_p)*1000);
	}
	
}
