package patientCare;

import sim.engine.*;

public class Prioritizator implements Steppable {
	private static final long serialVersionUID = 1L;

	Care care;
	String policy;

	// internals
	private int order_basal_policy = 10;
	private int priority;
	private double granularity = 10;

	public boolean basalGranularity = true;

	public void step(SimState state) {
	};

	public Prioritizator(Care c, String p) {
		care = c;
		switch (p) {
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
				policy = "none";
		}
	}

	/**
	 * Returns the priority of a patient. The maximum priority (minimum integer)
	 * should be 2.
	 * 
	 * @param patient
	 * @return priority
	 */
	public int hat_o(Patient patient) {
		switch (policy) {
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
				System.out.println("Java (Prioritizator) Can't set policy to " + policy);
				System.out.println("Exiting");
				System.exit(0);
		}
		return priority;
	}

	/**
	 * Returns the max priority. This is used to assure that other agents, for
	 * instance the observer, come into the simulation after all patients.
	 * 
	 * @return
	 */
	public int maxPriority() {
		switch (policy) {
			case "default":
				return priority = order_basal_policy;
			case "H_segmented":
				return 10002;
			case "patient_centred":
				return 2002;
			case "risk":
				int M_max_risk = care.patients != null ? care.patients.numObjs : 0;
				return M_max_risk + 2;
			case "need":
				int M_max = care.patients != null ? care.patients.numObjs : 0;
				return M_max + 2;
			// return (int)((care.pat_init.max_capN *1000)+2);
			case "risk_need":
				return (int) ((care.pat_init.max_capN + care.pat_init.max_delta) * 1000 + 2);
		}
		return priority;
	}

	public void changePolicy(String p) {
		policy = p;
	}

	public double getGranularity() {
		if (basalGranularity) {
			if (policy != null && policy.equals("risk")) {
				return 1;
			} else {
				return 10;
			}
		}
		return granularity;
	}

	public void setGranularity(double g) {
		if (g >= 0 && g <= 10) {
			granularity = g;
			basalGranularity = false;
		} else {
			System.out.println("Java (Prioritizator) Granularity must be between 0 and 10");
		}
	}

	public String getPolicy() {
		return policy;
	}

	private int basal(Patient patient) {
		return order_basal_policy;
	}

	// boolean temporaryTest = false;
	private int H_segmented(Patient patient) {
		// For H between 0 and 1000
		// H 0 gets priority 10001; H 1000 gets priority 1;
		// priority 0 is reserved for
		// Handle inactive patients first
		if (patient.h_p_i_1 == -1) {
			return order_basal_policy;
		}
		// Handle active patients latter
		int factor;
		if (patient.h_p_i_1 > 1000) {
			factor = 1000;
			System.out.println("Java (Prioritizator) H for a patient exceded 1000! Not good.");
		} else {
			factor = (int) patient.h_p_i_1 * 10;
		}
		int prioriy = 10002 - factor;
		// if(temporaryTest) {
		// System.out.println("(Prioritizator) Giving priority "+ prioriy + " to patient
		// "+patient.p+" with health " + patient.h_p_i_1);}
		return prioriy;
	}

	private int patient_centred(Patient patient) {
		// For N between 0 and capN, and E between 0 and capE
		// Npriority = 1002 for N = 0; and 2 for N = capN
		// Epriority = 0 for E = 0; and 1000 for E = capE
		// Final priority is Npriority + Npriority
		// Handle inactive patients first
		if (patient.h_p_i_1 == -1) {
			return order_basal_policy;
		}
		// Handle active patients latter
		int Npriority;
		int Epriority;
		Npriority = (int) (1002 - (patient.n_p_i / patient.capN_p) * 1000);
		Epriority = (int) ((patient.get_MeanE() / patient.capE_p) * 1000);
		// int result = Npriority+Epriority;
		// System.out.println("(Prioritizator) p "+ patient.p+" N = "+patient.n_p_i+" E
		// = "+patient.get_MeanE()+ " yields "+result);

		return Npriority + Epriority;
	}

	private int risk(Patient patient) {
		double max_delta = care.pat_init.max_delta;
		int M = care.patients != null ? care.patients.numObjs : 0;
		if (M <= 0) {
			return 2;
		}

		// Number of bins scales exponentially in base 3: 3^0 = 1, 3^1 = 3, 3^2 = 9...
		int B = (int) Math.pow(3, basalGranularity ? 1 : granularity);

		// Calculate bin index in [0, B - 1]
		double fraction = max_delta > 0 ? patient.delta_p / max_delta : 0.0;
		int binIndex = (int) Math.min(B - 1, Math.floor(fraction * B));

		// Normalize to [0.0, 1.0]. If B == 1 (granularity == 0), map to 1.0 (equal
		// priority 2 for all)
		double normalizedRisk = (B > 1) ? (double) binIndex / (B - 1) : 1.0;

		int range = Math.max(1, M - 1);
		return 2 + (int) ((1.0 - normalizedRisk) * range);
	}

	private int need(Patient patient) {
		// System.out.println("(Prioritizator) Patient "+patient.p+" has n_p_i " +
		// patient.n_p_i + " and was given priority "+(int)((care.pat_init.max_capN -
		// patient.n_p_i)*1000+2) +
		// " care.pat_init.max_capN is "+care.pat_init.max_capN);
		// return (int)((care.pat_init.max_capN - patient.n_p_i)*1000+2);

		double max_capN = care.pat_init.max_capN;
		int M = care.patients != null ? care.patients.numObjs : 0;
		if (M <= 0) {
			return 2;
		}

		// Number of bins scales exponentially in base 3: 3^0 = 1, 3^1 = 3, 3^2 = 9...
		int B = (int) Math.pow(3, basalGranularity ? 10 : granularity);

		// Calculate bin index in [0, B - 1]
		double fraction = max_capN > 0 ? patient.n_p_i / max_capN : 0.0;
		int binIndex = (int) Math.min(B - 1, Math.floor(fraction * B));

		// Normalize to [0.0, 1.0]. If B == 1 (granularity == 0), map to 1.0 (equal
		// priority 2 for all)
		double normalizedNeed = (B > 1) ? (double) binIndex / (B - 1) : 1.0;

		int range = Math.max(1, M - 1);
		return 2 + (int) ((1.0 - normalizedNeed) * range);

	}

	private int risk_need(Patient patient) {
		return (int) ((care.pat_init.max_capN + 2 - patient.n_p_i + care.pat_init.max_delta - patient.delta_p) * 1000);
	}

}
