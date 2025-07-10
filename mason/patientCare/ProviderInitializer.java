package patientCare;

import sim.util.Bag;

public class ProviderInitializer {
	Care care;
	String strategy;
	int rand;
	
	int alpha_w;
	
	
	//for Fixed strategies
	double fixed_lambda;
	double fixed_tau;
	
	public ProviderInitializer(Care _care, String _strategy) {
		care = _care;
		strategy = _strategy;
		
		if (strategy == "sensitivity_1") {
			//initialize fixed params
			fixed_lambda = care.random.nextDouble(true,true)*10;
			fixed_tau = care.random.nextDouble(true,true)*10;
			strategy = "apply_fixed";

		}

	}

	public void initialize(Provider provider) {
		SumC_w(provider);
		A_w(provider);
		lambda_w(provider);
		tau_w(provider);
	}
	
	public void SumC_w(Provider provider) {
		provider.SumC_w = new int[care.N];
		switch(strategy) {
		case "random-basal": //pending
			break;
		default: 
			for(int i = 0; i<care.N;i++) {provider.SumC_w[i] = 0;}
			break;

		}
	}
	
	public void A_w(Provider provider) {
		switch(strategy) {
		default:
			provider.A_w = (int)(care.totalCapacity/care.W);
			break;

		}
	}
	public void lambda_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.lambda_w = care.random.nextDouble()*3;
			break;
		case "apply_fixed":
			provider.lambda_w = fixed_lambda;
			break;
		default:
			provider.lambda_w = 0.5;}
	}
	
	public void tau_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.tau_w = care.random.nextDouble()*3;
			break;
		case "apply_fixed":
			provider.tau_w = fixed_tau;
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
	
	public void settau(Bag providers, int value) {
		for(int i=0;i<providers.numObjs;i++) {
			((Provider)providers.objs[i]).tau_w = value;
		}
	}
	
	public void settesting(Bag providers, Boolean value) {
		for(int w=0;w<providers.numObjs;w++) {
			((Provider)providers.objs[w]).testing = value;
		}
	}
}
