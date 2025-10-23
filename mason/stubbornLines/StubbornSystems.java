package stubbornLines;
import java.io.File;
import java.util.HashMap;

import patientCare.Care;


/**
 * Run simulations with random draws according to Sensitivity ranges, and see how they respond to
 * h_segmentation. Select the n ones that don't respond
 */
public class StubbornSystems {
	experimentWithProvidedLines experimenter = new experimentWithProvidedLines();
	int n = 100;
	int counter = 0;
	Care FoundSystem = null;

	
	public static void main(String[] args) {
		StubbornSystems finder = new StubbornSystems(args[0]);
	}
	
	public StubbornSystems(int numberOfLines) {
		int found = 0;
		while(found <numberOfLines) {
		Care[] stuborn = findStubborn();
		found+=1;
		}
		System.out.println("Needed "+counter+" trials.");
	}
	
	public StubbornSystems(String path) {
		experimenter.setcheckpoint_path(path);
	}
	
	
	public double[] scaledFoundParams() {
		if (FoundSystem == null) {
			FoundSystem = findStubborn()[1];
		}

		vectorizeParams vectorize = new vectorizeParams();
		return vectorize.toScaledOrderedArray(FoundSystem.getParams());

	}
	
	public double[] rawFoundParams() {
		if (FoundSystem == null) {
			FoundSystem = findStubborn()[1];
		}

		vectorizeParams vectorize = new vectorizeParams();
		return vectorize.toRawOrderedArray(FoundSystem.getParams());

	}
	
	public double foundFit() {
		if (FoundSystem == null) {
			FoundSystem = findStubborn()[1];
		}
		return FoundSystem.observer.getMeanFinalH();
	}
	
	public long seedFound() {
		if (FoundSystem == null) {
			FoundSystem = findStubborn()[1];
		}
		return FoundSystem.getSeed();
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
		if(timedInterventionSim.observer.getMeanFinalH() > basalSim.observer.getMeanFinalH()/2) {
			found = true;
		} else { 
			file = new File(experimenter.getcheckpoint_path()+"/"+Long.toString(basalSim.getSeed())+"_"+"initialization");
			file.delete();
			file = new File(experimenter.getcheckpoint_path()+"/"+Long.toString(basalSim.getSeed())+"_"+"basal");
			file.delete();}
		}
		Care[] result = {basalSim, timedInterventionSim};
		return result;
		
	}
	
	
	
}
