package patientCare;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import sim.util.Bag;

class PatientTest {
	
	
	@Test
	void passParameters_runsAuisolatedBiological_checksTotalProgression() {
		AisoletedMechanisms mec = new AisoletedMechanisms();
		double[] run1 = mec.runAisolatedBiologicalMechanism(150, 0.5, 1, 300, 123);
		double total1 = 0;
		for(int i = 0; i < run1.length; i++) {
			total1 = total1 + run1[i];
		}
		assertEquals((double) 146, total1);
		double[] run2 = mec.runAisolatedBiologicalMechanism(1500, 1, 2, 300, 555);
		double total2 = 0;
		for(int i = 0; i < run1.length; i++) {
			total2 = total2 + run2[i];
		}
		assertEquals((double) 539, total2);
	}

	@Test
	void passParameters_runInContextBiological_checksTotalProgression() {
		Care care = new Care(123);
		care.setweeks(150);
		care.setDISEASE_VELOCITY(1);
		care.setNumPatients(1000);
		care.setCapacity(0);
		care.setSUBJECTIVE_INITIATIVE(1);
		care.setLEARNING_RATE(1);
		int[] nDiseases = {1,2,3};
		care.setnDiseases(nDiseases);
		care.start();
		do{
			if (!care.schedule.step(care)) {
				System.out.println("algo falso en schedule.step");
				break;}
		}
		while (care.schedule.getSteps() < care.getweeks());
		care.finish();
		double[][] Hs = new double[care.getNumPatients()][care.getweeks()];
		Hs = care.getHs();
		double totalNeeds = 0;
		double[] totalHsPat = new double[care.getNumPatients()];
		for(int pat = 0; pat < care.getNumPatients(); pat++) {
			totalHsPat[pat] = Hs[pat][care.getweeks()];
			totalNeeds = totalNeeds + totalHsPat[pat];
		}
		assertEquals((double) 1892, totalNeeds);	
	}
	
}






