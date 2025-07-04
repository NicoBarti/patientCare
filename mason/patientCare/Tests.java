package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

public class Tests {
	
	Care care;
	
	@Test
	void d1_calculate_disease_after_treatment() {
	long seed = 10; int varsigma = 1;
	care = new Care(seed);
	care.setN(10); 
	runCare(care,varsigma);
	double h[] = care.patientObserver.geth(care.patients);
	for(int i = 0; i<h.length;i++) {System.out.println(h[i]);}
	
	}
	
	public void runCare(Care care, int varsigma) {
		care.start();
		do{
			if (!care.schedule.step(care)) {break;}
			}
		while (care.schedule.getSteps() < varsigma);
		care.finish();
	}
	

}
