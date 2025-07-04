package patientCare;

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
		alpha_w(provider);
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
	
	public void alpha_w(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.alpha_w = care.random.nextInt(care.N);
			break;
		default:
			provider.alpha_w = 10;
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
}
