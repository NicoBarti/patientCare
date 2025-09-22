package patientCare;

public class Prioritizator {
	Care care;
	String policy;
	
	//internals
	private int order_basal_policy = 10;
	private int priority;
	
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
		default:
			policy ="none";
		}}
	
	
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
		default:
			System.out.println("Java (Prioritizator) Can't set policy to "+policy);
			System.out.println("Exiting");
			System.exit(0);
		}
		return priority;
		}
	
	public int maxPriority() {
		switch(policy) {
		case "default":
			return priority = order_basal_policy;
		case "H_segmented":
			return 10001;
		case "patient_centred":
			return 2001;
		}
		return priority;
	}

	public void changePolicy(String p) {}
	
	private int basal(Patient patient) {
		return order_basal_policy;
		}
	
	private int H_segmented(Patient patient) {
		//For H between 0 and 1000
		// H 0 gets priority 10001; H 1000 gets priority 1;
		// priority 0 is reserved for 
		int factor;
		if(patient.h_p_i_1 > 1000) {
			 factor = 1000;
			System.out.println("Java (Prioritizator) H for a patient exceded 1000! Not good.");
		} else { factor = (int)patient.h_p_i_1*10;}
		int prioriy = 10001 - factor;
		
		//System.out.println("(Prioritizator) Giving priority "+ prioriy + " to patient "+patient.p+" with health " + patient.h_p_i_1);
		return prioriy;
	}
	
	private int patient_centred(Patient patient) {
		//For N between 0 and capN, and E between 0 and capE
		//Npriority = 1001  for N = 0; and 0 for N = capN
		//Epriority = 0 for E = 0; and 1001 for E = capE
		//Final priority is Npriority + Npriority
		int Npriority; int Epriority;
		Npriority = (int)(1001 - (patient.n_p_i/patient.capN_p)*1000);
		Epriority = (int)((patient.get_MeanE()/patient.capE_p)*1000);
		//int result = Npriority+Epriority;
		//System.out.println("(Prioritizator) p "+ patient.p+" N = "+patient.n_p_i+" E = "+patient.get_MeanE()+ " yields "+result);
		
		return Npriority+Epriority;
	}
	
}
