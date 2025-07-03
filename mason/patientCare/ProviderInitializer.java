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
		
	}
	
	public void C(Provider provider) {
		provider.C = new int[care.N][care.weeks];
		switch(strategy) {
		case "random": //pending
			break;
		default: // all 0s 
		}
	}
	
	public void alpha(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.alpha = care.random.nextInt(care.capacity);
			break;
		default:
			provider.alpha = care.capacity;
		}
	}
	public void lambda(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.lambda = care.random.nextDouble()*10;
			break;
		default:
			provider.lambda = care.lambda;}
	}
	
	public void tau(Provider provider) {
		switch(strategy) {
		case "random": 
			provider.tau = care.random.nextDouble()*10;
			break;
		default:
			provider.tau = care.tau;}
	}
}
