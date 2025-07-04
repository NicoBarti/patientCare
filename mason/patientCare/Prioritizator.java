package patientCare;

public class Prioritizator {
	Care care;
	String policy;
	
	private int priority;
	
	public Prioritizator(Care c,String p) {
		care = c;
		switch(p){
		default:
			policy ="default";
		}}
	
	public int hat_o(Patient patient) {
		switch(policy) {
		case "default":
		priority = default_policy(patient);
		}
		return priority;
		}

	public void changePolicy(String p) {}
	
	private int default_policy(Patient patient) {
		return care.N;}
	
	
}
