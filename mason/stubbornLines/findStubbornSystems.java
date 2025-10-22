package stubbornLines;
import java.io.File;

import patientCare.Care;


/**
 * Run simulations with random draws according to Sensitivity ranges, and see how they respond to
 * h_segmentation. Select the n ones that don't respond
 */
public class findStubbornSystems {
	experimentWithProvidedLines experimenter = new experimentWithProvidedLines();
	int n = 100;
	int counter = 0;

	
	public static void main(String[] args) {
		findStubbornSystems finder = new findStubbornSystems();
	}
	
	public findStubbornSystems() {
		int found = 0;
		while(found <501) {
		Care[] stuborn = findStubborn();
		found+=1;
		}
		System.out.println("Needed "+counter+" trials.");
	}
	
	private Care[] findStubborn() {
		boolean found = false;
		Care basalSim = null;
		Care timedInterventionSim = null;
		File file;
		while(!found) {
		long seed;
		int varsigma = 500;
		seed = System.currentTimeMillis();
		basalSim = new Care(seed);

		basalSim.setOBS_PERIOD(varsigma);
		basalSim.setPATIENT_INIT("sensitivity_1");
		basalSim.setPROVIDER_INIT("sensitivity_1");
		basalSim.setvarsigma(varsigma);
		basalSim = experimenter.basalRun(basalSim, "basal");
		
		timedInterventionSim = experimenter.interventionFromCheckpoint(Long.toString(seed), 
				"basal", basalSim.getvarsigma());	
		counter+=1;
//		System.out.println("trials "+counter);
//		System.out.println("Timed intervention: "+timedInterventionSim.observer.getMeanFinalH()+ " | basal: "+basalSim.observer.getMeanFinalH());
//		System.out.println("Policy basal: "+basalSim.prioritize.getPolicy()+ " | policy intervention: "+timedInterventionSim.prioritize.getPolicy());
//		System.out.println("varsigma: "+basalSim.getvarsigma());
		if(timedInterventionSim.observer.getMeanFinalH() > basalSim.observer.getMeanFinalH()/2) {
			found = true;
		} else { 
			file = new File(experimenter.getcheckpoint_path()+"/"+Long.toString(basalSim.getSeed())+"_"+"initialization");
			file.delete();
			file = new File(experimenter.getcheckpoint_path()+"/"+Long.toString(basalSim.getSeed())+"_"+"basal");
			file.delete();}
		}
		Care[] result = {basalSim, timedInterventionSim};
		return  result;
		
	}
	
	
	
}
