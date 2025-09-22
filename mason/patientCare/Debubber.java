package patientCare;
import sim.util.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;


public class Debubber {
	Care care;
	Patient onePatient;
	Provider oneProvider;
	Provider provider_1;
	Provider provider_2;
	long seed = 19382109;
	
	
	@Test
	void check_health_segmentation() {
		long currentSeed = System.currentTimeMillis();
		double lambda; double tau; double healthStatus;
		care = new Care(currentSeed);int N = 10; int W = 4; int varsigma = 1000; 
		//care.setPi("basal");
		care.setW(W);
		care.setN(N);
		care.setPi("H_segmented");
		care.setvarsigma(varsigma);
		care.PATIENT_INIT = "basal";
		care.start();
		care.order = new long[N];
		care.H_at_Order = new double[N];
		care.pat_init.settesting(care.patients, true);
		
		for(int i=0; i< varsigma; i++) {
			care.schedule.step(care);
			
			for (int p = 0 ; p< care.patients.numObjs-1; p++) {
				Patient patient_p = (Patient)care.patients.objs[p];
				Patient patient_p1 = (Patient)care.patients.objs[p+1];
				//if(patient_p.testing_get_previousH() == patient_p1.testing_get_previousH()) 
				if(care.H_at_Order[p] == care.H_at_Order[p+1])
				{
					continue;
				}
				if(care.order[p] > care.order[p+1]) { // p was schedduled with less priority that p+1
					System.out.println("(before) patient "+p +" has order "+care.order[p] + " and healh " + care.H_at_Order[p]);
					System.out.println("(before) patient " +p +"+1 has order "+care.order[p+1]+" and health "+ care.H_at_Order[p+1]);
					assertTrue( patient_p.care.H_at_Order[p] < patient_p1.care.H_at_Order[p+1], "Patient "+p+" had health "+patient_p.care.H_at_Order[p]+
							" but came before the consecutive patient that had health " +patient_p1.care.H_at_Order[p+1]);

				}
				if(care.order[p] < care.order[p+1]) { // p was schedduled with more priority that p+1
					System.out.println("(after) patient " + p + " has order "+care.order[p] + " and healh " + care.H_at_Order[p]);
					System.out.println("(after) patient " + p + " +1 has order "+care.order[p+1]+" and health "+ care.H_at_Order[p+1]);
					assertTrue( patient_p.care.H_at_Order[p] > patient_p1.care.H_at_Order[p+1], "Patient "+p+" has health "+patient_p.care.H_at_Order[p]+
							" but came after patient the consecutive patient that has health " +patient_p1.care.H_at_Order[p+1]);


				
				}
				
			}

		}

	}

	
}
