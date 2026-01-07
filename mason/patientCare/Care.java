package patientCare;

import java.util.Map;
import java.util.HashMap;

import sim.engine.*;
import sim.util.*;

/**
 * Main class that holds all the agents and context.
 */
/**
 * 
 */
/**
 * 
 */
/**
 * 
 */
public class Care extends SimState {
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Number of patients. CONTROL VARIABLE.
	 */
	public int N = 1000;
	/**
	 * Number of steps. CONTROL VARIABLE.
	 */
	public int varsigma = 150; 
	/**
	 * Number of doctors. CONTROL VARIABLE.
	 */
	public int W = 2;
	/**
	 * Total available appointments at any given step. CONTROL VARIABLE.
	 */
	public int totalCapacity = 50;
	/**
	 * Resource allocation policy. CONTROL VARIABLE.
	 */
	public String Pi = "basal";
	/**
	 * How providers are initialized. CONTROL VARIABLE.
	 */
	public String PROVIDER_INIT = "random";
	/**
	 * How patients are initialized
	 */
	public String PATIENT_INIT = "random";

	// internals
	/**
	 * Frequency, in steps, for state observations.
	 */
	public int OBS_PERIOD = 1;
	public Bag providers;
	public Bag patients;
	public Appointer appointer;
	public Prioritizator prioritize;
	Patient patient;
	Patient pat;
	Provider provider;
	public ProviderInitializer prov_init;
	public PatientInitializer pat_init;
	public ObserveCare observer;
	
	long storedSeed;
	
	
	public Care(long seed) {
		super(seed);
		storedSeed = seed;
	}
	
	
	/** 
	 * Generates an observer for the specified variables and scheddules it with priority 0
	 * @param obsH
	 * @param obsN
	 * @param obsC
	 * @param obsT
	 * @param obsE
	 * @param obsB
	 * @param simpleC
	 * @param simpleE
	 * @param simpleB
	 */
	public void startObserver(boolean obsH, boolean obsN, boolean obsC, 
			boolean obsT, boolean obsE, boolean obsB, boolean simpleC, boolean simpleE, boolean simpleB,
			boolean disease, boolean expNoise, boolean instExp) {
		observer=new ObserveCare(this, OBS_PERIOD, obsH, obsN, obsC, obsT, obsE, obsB, simpleC, simpleE, simpleB, disease, expNoise, instExp);
		schedule.scheduleRepeating(schedule.EPOCH, 0, observer);
	}

	
	/**
	 * Generate an observer for all state variables and scheddule it with priority 0
	 */
	public void startObserver() {
		observer = new ObserveCare(this, OBS_PERIOD);
		schedule.scheduleRepeating(schedule.EPOCH, 0, observer);
	}

	/**
	 * Initializes initializers (if not previously done). Initializes the Prioritizator.
	 * SEED: It re-sets the seed after initializers configuration to keep consistency of replication. Its re-set again after agents initialization.
	 * Creates and initializes and schedules providers and patients.
	 * Creates anonymous agent that implements the policy. This is the last agent to called at the end of each step.
	 */
	public void start() {
		super.start();
		if(pat_init==null) {
			//System.out.println("(JAVA Care.java) Configure patient initialization to "+PATIENT_INIT);
		pat_init = new PatientInitializer(this, PATIENT_INIT);} 
			//else {System.out.println("(JAVA Care.java) Patient initialization pre-configured");}
		if(prov_init==null) {
			//System.out.println("(JAVA Care.java) Configure provider initialization to "+PROVIDER_INIT);
			prov_init = new ProviderInitializer(this, PROVIDER_INIT);
		}  else {
			//System.out.println("(JAVA Care.java) Provider initialization pre-configured");
			}

		prioritize = new Prioritizator(this, Pi);
		if(OBS_PERIOD == 0) {OBS_PERIOD = varsigma;}
		
				
		providers = new Bag(W);
		patients = new Bag(N);
		
		//FOR CONSISTENCY AMONG IMPLEMENTATIONS, THE SEED IS RESET BEFORE
		//INITIALIZING AGENTS (configuring the initializers sometimes uses random number generator)
		this.random.setSeed(storedSeed+100);
		// create and initialize providers
		for(int i =0;i<W;i++) {
			provider = new Provider();
			prov_init.initialize(provider);
			providers.add(provider);
			schedule.scheduleRepeating(schedule.EPOCH,1,provider); //providers are stepped first thing at each step
		}
		// assure capacity is exact
		prov_init.adjustCapacity(providers, totalCapacity);
		
		// create and initialize patients
		for (int i = 0; i < N; i++) {
			patient = new Patient();
			pat_init.initialize(patient);
			patients.add(patient);
	//schedule.scheduleOnce(schedule.EPOCH, prioritize.hat_o(patient), patient); //orders 2 to N+2
		}
		//store the maximum severity and capN, used for priority allocation
		pat_init.computeMaxs(patients);
		// scheddule patients for the first run: (they allocate themselfes afterwards)
		for (int i = 0; i< patients.numObjs; i++) {
			patient = (Patient)patients.get(i);
			schedule.scheduleOnce(schedule.EPOCH, prioritize.hat_o(patient), patient); //orders 2 to N+2

		}
		//System.out.println("(Care) Just finished schedduling all patients");
		
		//create anonymus agent that scheddules patients wit priority hat_o 
		//this agent acts at the end of each state ( max_priority+3)
	schedule.scheduleRepeating(schedule.EPOCH, prioritize.maxPriority() +3, new Steppable(){
				private static final long serialVersionUID = 1L;
				public void step(SimState state) { 
					for(int i=0;i<patients.numObjs;i++) {
				//boolean scheduled = false;
						pat = (Patient)(patients.objs[i]);
						if(! schedule.scheduleOnce(pat,prioritize.hat_o(pat))) {
							System.out.println("Failed scheduing agent! FATAL ERROR. STOPPING HERE.");
							System.out.println("The seed was "+storedSeed);
							System.exit(0);
						}
					}}
				});
		
		
		appointer = new Appointer(this);
		//FOR CONSISTENCY AMONG IMPLEMENTATIONS, THE SEED IS RESET AFTER
		//INITIALIZING AGENTS, BEFORE STEPPING THE SIMULATION
		this.random.setSeed(storedSeed);
	}
	
	
	public void finish() {
		observer.endObservation(this);
			}
	
	// setters and getters
	public void setN(int val) {N = val;}
	public int getN() {return N;}
	public void setvarsigma(int val) {varsigma = val;}
	public int getvarsigma() {return varsigma;}
	public void setW(int val) {W = val;}
	public int getW() {return W;}
	
	/** Sets the assignation policy. Must be a policy contained in Prioritizator
	 * @param val basal, H_segmented, patient_centred, risk, need, risk_need
	 */
	public void setPi(String val) {
		if(val == "basal" || val == "H_segmented" || val == "patient_centred" || val == "risk" || val == "need" || val == "risk_need"){
			Pi = val;
		} else {
			System.out.println("(Java CARE) Error! Unexistant policy: "+val);
			System.exit(0);
		}
		}
	
	public String getPi() {return Pi;}
	public void setPROVIDER_INIT(String val) {PROVIDER_INIT = val;}
	public String getPROVIDER_INIT() {return PROVIDER_INIT;}
	public void setPATIENT_INIT(String val) {PATIENT_INIT = val;}
	public String getPATIENT_INIT() {return PATIENT_INIT;}
	public void setOBS_PERIOD(int val) {OBS_PERIOD = val;}
	public int getOBS_PERIOD() {return OBS_PERIOD;}
	public void settotalCapacity(int val) {totalCapacity = val;}
	public int gettotalCapacity() {return totalCapacity;}
	public long getSeed() {return storedSeed;}
	
	//to access the observer 
	//public 

	
	public HashMap<String, String> getParams() {
				
		HashMap<String, String> params = new HashMap();
		//Care level
		params.put("N", Integer.toString(getN()));
		params.put("varsigma", Integer.toString(getvarsigma()));
		params.put("W", Integer.toString(getW()));
		params.put("totalCapacity", Integer.toString(totalCapacity));
		params.put("Pi", getPi());
		params.put("PROVIDER_INIT", getPROVIDER_INIT());
		params.put("PATIENT_INIT", getPATIENT_INIT());
		params.put("OBS_PERIOD", Integer.toString(getOBS_PERIOD()));
		//Patient level
		params.put("fixed_delta", Double.toString(pat_init.fixed_delta));
		params.put("fixed_capN", Double.toString(pat_init.fixed_capN));
		params.put("fixed_lambda", Double.toString(pat_init.fixed_lambda));
		params.put("fixed_tau", Double.toString(pat_init.fixed_tau));
		params.put("fixed_rho", Double.toString(pat_init.fixed_rho));
		params.put("fixed_eta", Double.toString(pat_init.fixed_eta));
		params.put("fixed_kappa", Float.toString(pat_init.fixed_kappa));
		params.put("fixed_capE", Double.toString(pat_init.fixed_capE));
		params.put("fixed_psi", Double.toString(pat_init.fixed_psi));
		//Provider level
		params.put("fixed_lambda", Double.toString(prov_init.fixed_lambda));
		params.put("fixed_tau", Double.toString(prov_init.fixed_tau));
		//hyperparams
		params.put("seed", Long.toString(storedSeed));
		
		return params;
	}

	
	public void change_N_midwaytrhough(int newN) {
		if (newN == N) {return;}
		//1:
		//method to increase
		if(newN>N) {
		//add n patients, and initialize them with PATIENT_INIT
		//patients.resize(N-newN);
		for (int i=N;i<newN;i++) {
			patient = new Patient();
			pat_init.initialize(patient);
			patients.add(patient);
			schedule.scheduleOnce(patient, prioritize.hat_o(patient)); //orders 2 to N+2
		}
		//modify the observer
		observer.increaseNmidway(newN-N);
		//modify the provider's memory of interaction
		for(int p = 0;p<providers.numObjs;p++) {
			((Provider)providers.get(p)).increaseNmidway(newN-N);
		}
		}
		//2:
		//method to decrease
		if(N>newN) {
		//eliminate patients at random
		patients.shuffle(random);
		
		for(int i =0;i<N-newN;i++) {
			patient = (Patient)patients.pop();
			observer.unobservePatient(patient.p);

		}
		}
		//finally, update N
		N = newN;
	}
	
	public void change_W_midwaytrhough(int newW) {
		if (newW == W) {return;}
		//1. Method to increase
		if(newW>W) {
		//add n providers, and initialize them with PROVIDER_INIT
		//providers.resize(W-newW);
		for(int i =W;i<newW;i++) {
		provider = new Provider();
		prov_init.initialize(provider);
		providers.add(provider);
		schedule.scheduleRepeating(provider,1); //providers are stepped first thing at each step
		}
		prov_init.adjustCapacity(providers, totalCapacity);

		observer.increaseWmidway(newW-W);
		for(int p=0; p<patients.numObjs;p++) {
			((Patient)patients.get(p)).increaseWmidway(newW-W);
		}
		
		}
		//2. Method to decrease
		if(newW<W) {
			//eliminate providers at random
			providers.shuffle(random);
			//System.out.print("(Care cange_w_midway) elminated providers:");
			for (int i = 0; i < W - newW; i++) {
				provider = (Provider)providers.pop();
			//	System.out.print(" "+provider.w+ " - ");
				observer.unobserveProvider(provider.w);
			//	System.out.println("(Care) Observer contains: observer.B_p_w_i[5][goneProdiver][25]"+observer.B_p_w_i[5][provider.w][25]);
			}
			//System.out.println();
			prov_init.adjustCapacity(providers, totalCapacity);

		}
		W = newW;
		
	}
	
	//testing:
	boolean testing = false;
	long[] order;
	int patientOrder = 0;
	double[] H_at_Order;
	double[] NE_at_Order;
	double[] N_at_Order;
	
	/**
	 * Special method only used for testing. A bit convoluted.
	 * Only way I came up with to see the order that the scheduler assigned to agents with same priority.
	 * You need to restart patientOrder to 0 before each step (from testing class)
	 */
	public void test_registerOrder(int p, double H, double N, double E) {
		order[p] = patientOrder;
		H_at_Order[p] = H;
		NE_at_Order[p] = N-E;
		N_at_Order[p] = N;
		patientOrder+=1;
	}
}
