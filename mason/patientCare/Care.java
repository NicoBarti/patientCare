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
	public String Pi = "base policy";
	public String PROVIDER_INIT = "random";
	public String PATIENT_INIT = "random";

	
	// internals
	public int OBS_PERIOD = 10;
	public Bag providers;
	public Bag patients;
	public Appointer appointer;
	public Prioritizator prioritize;
	Patient patient;
	Provider provider;
	ProviderInitializer prov_init;
	PatientInitializer pat_init;
	ObserveCare observer;
	
	public Care(long seed) {
		super(seed);
	}

	public void start() {
		super.start();
		
		pat_init = new PatientInitializer(this, PATIENT_INIT);
		prov_init = new ProviderInitializer(this, PROVIDER_INIT);

		prioritize = new Prioritizator(this, Pi);
		observer = new ObserveCare(this, OBS_PERIOD);
		// observer.initialize(1, this);
		
		providers = new Bag(W);
		patients = new Bag(N);
	schedule.scheduleRepeating(schedule.EPOCH, 0, observer);

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
		observer.endObservation();
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

	
	public HashMap getParams() {
				
		HashMap<String, String> params = new HashMap();
		params.put("N", Integer.toString(getN()));
		params.put("varsigma", Integer.toString(getvarsigma()));
		params.put("W", Double.toString(getW()));
		params.put("Pi", getPi());
		params.put("PROVIDER_INIT", getPROVIDER_INIT());
		params.put("PATIENT_INIT", getPATIENT_INIT());
		return params;
	}

}
