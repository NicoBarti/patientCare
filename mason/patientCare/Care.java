package patientCare;

import java.util.Map;
import java.util.HashMap;

import sim.engine.*;
import sim.util.*;


public class Care extends SimState {
	
	//control variables (params)
	public int N = 1000;
	public int varsigma = 150; 
	public int W = 2;
	public int totalCapacity = 50;
	public String Pi = "random allocation";
	public String PROVIDER_INIT = "random";
	public String PATIENT_INIT = "random";

	// internals
	public int OBS_PERIOD = 1;
	public Bag providers;
	public Bag patients;
	public Appointer appointer;
	public Prioritizator prioritize;
	Patient patient;
	Provider provider;
	ProviderInitializer prov_init;
	PatientInitializer pat_init;
	ObserveCare observer;
	
	long storedSeed;
	
	public Care(long seed) {
		super(seed);
		storedSeed = seed;
	}
	
	public void startObserver(Boolean obsH, Boolean obsN, Boolean obsC, 
			Boolean obsT, Boolean obsE, Boolean obsB, Boolean simpleC, Boolean simpleE, Boolean simpleB) {
		observer=new ObserveCare(this, OBS_PERIOD, obsH, obsN, obsC, obsT, obsE, obsB, simpleC, simpleE, simpleB);
		schedule.scheduleRepeating(schedule.EPOCH, 0, observer);
	}
	
	public void startObserver() {
		observer = new ObserveCare(this, OBS_PERIOD);
		schedule.scheduleRepeating(schedule.EPOCH, 0, observer);
	}

	public void start() {
		super.start();
		if(pat_init==null) {
			//System.out.println("(JAVA Care.java) Configure patient initialization to "+PATIENT_INIT);
		pat_init = new PatientInitializer(this, PATIENT_INIT);} else {System.out.println("(JAVA Care.java) Patient initialization pre-configured");}
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

		// create and initialize providers
		for(int i =0;i<W;i++) {
			provider = new Provider();
			provider.w = i;
			prov_init.initialize(provider);
			providers.add(provider);
	schedule.scheduleRepeating(schedule.EPOCH,1,provider); //providers are stepped first thing at each step
		}
		
		// create and initialize patients
		for (int i = 0; i < N; i++) {
			patient = new Patient();
			patient.p = i;
			pat_init.initialize(patient);
			patients.add(patient);
	schedule.scheduleOnce(schedule.EPOCH, prioritize.hat_o(patient), patient); //orders 2 to N+2
		}
		
		//create anonymus agent that scheddules patients wit priority hat_o 
		//this agent acts at the end of each state (order N+1)
	schedule.scheduleRepeating(schedule.EPOCH, N+3, new Steppable(){ //copied
				public void step(SimState state) { 
					//System.out.println(schedule.getSteps());
					for(int i=0;i<patients.numObjs;i++) {
	schedule.scheduleOnce((Patient)(patients.objs[i]),prioritize.hat_o(patient)); //orders 2 to N+2
					}}
				});
		
		
		appointer = new Appointer(this);
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
	public void setPi(String val) {Pi = val;}
	public String getPi() {return Pi;}
	public void setPROVIDER_INIT(String val) {PROVIDER_INIT = val;}
	public String getPROVIDER_INIT() {return PROVIDER_INIT;}
	public void setPATIENT_INIT(String val) {PATIENT_INIT = val;}
	public String getPATIENT_INIT() {return PATIENT_INIT;}
	public void setOBS_PERIOD(int val) {OBS_PERIOD = val;}
	public int getOBS_PERIOD() {return OBS_PERIOD;}

	
	public HashMap getParams() {
				
		HashMap<String, String> params = new HashMap();
		//Care level
		params.put("N", Integer.toString(getN()));
		params.put("varsigma", Integer.toString(getvarsigma()));
		params.put("W", Double.toString(getW()));
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
		params.put("OBS_PERIOD", Integer.toString(OBS_PERIOD));
		
		return params;
	}

}
