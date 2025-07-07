package patientCare;

import sim.util.Bag;

public class ProviderInitializer {
	Care care;
	String strategy;
	int rand;
	
	public ProviderInitializer(Care _care, String _strategy) {
		care = _care;
		strategy = _strategy;
	}

	public void initialize(Provider provider) {
		C_w(provider);
		A_w(provider);
		lambda_w(provider);
		
	}
	
	public void C_w(Provider provider) {
		provider.C_w = new int[care.N][care.varsigma];
		switch(strategy) {
		case "random": //pending
			break;
		default: // all 0s 
		}
	}
	
	public void A_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.A_w = care.random.nextInt(care.N);
			break;
		default:
			provider.A_w = 10;
		}
	}
	public void lambda_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.lambda_w = care.random.nextDouble()*10;
			break;
		default:
			provider.lambda_w = 0.5;}
	}
	
	public void tau_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.tau_w = care.random.nextDouble()*10;
			break;
		default:
			provider.tau_w = 2;}
	}
	
	public void settau(Bag providers, double value) {
		for(int i=0;i<providers.numObjs;i++) {
			((Provider)providers.objs[i]).tau_w = value;
		}
	}
	
	public void setlambda(Bag providers, double value) {
		for(int i=0;i<providers.numObjs;i++) {
			((Provider)providers.objs[i]).lambda_w = value;
		}
	}
	
	public void setalpha(Bag providers, int value) {
		for(int i=0;i<providers.numObjs;i++) {
			((Provider)providers.objs[i]).A_w = value;
		}
	}
}
