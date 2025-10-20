package runners;

import java.io.File;
import java.util.HashMap;

import patientCare.Care;
import patientCare.PatientInitializer;
import patientCare.ProviderInitializer;

public class experimentWithProvidedLines {
	
	String checkpoint_path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/checkpoints";
	int checkoutAT = 200;
	
	public static void main(String[] args) {
		new experimentWithProvidedLines("/Users/nicolasbarticevic/Desktop/simulationOutputs/paper1/500/PercentileLinesDelta4/");
	}
	
	public experimentWithProvidedLines(String path) {
	 csvReader reader = new csvReader(path);
	 HashMap<String, HashMap<String, String>> lines = reader.readPathFinderOutput("percenileLines.csv", true);
	 
	 for (String seed: lines.keySet()) {
		 Care basalSim = parametrizeLine(lines.get(seed));
		 basalSim = basalRun(basalSim, "basal");
		 Care pureInterventionSim = parametrizeLine(lines.get(seed));
		 pureInterventionSim = basalRun(pureInterventionSim, "H_segmented");
		 Care timedInterventionSim = interventionFromCheckpoint(seed, "basal", basalSim.getvarsigma());	
		 System.out.println("Experimenting with seed "+seed);
		 System.out.println("Basal final H: " + basalSim.observer.getMeanFinalH() + 
				 " Intervention final H " + pureInterventionSim.observer.getMeanFinalH() +
				 " Timed-Intervention final H " + timedInterventionSim.observer.getMeanFinalH());
	 }
	 
	 }
	
	public Care parametrizeLine(HashMap<String,String> params) {
	
		Care basalSim = new Care(Long.parseLong(params.get("seeds")));
		basalSim.setOBS_PERIOD(100);
		basalSim.setvarsigma(Integer.parseInt(params.get("varsigma")));
		basalSim.settotalCapacity(Integer.parseInt(params.get("totalCapacity")));
		basalSim.W = Integer.parseInt(params.get("W"));
		basalSim.N = Integer.parseInt(params.get("N"));
		
		basalSim.pat_init = new PatientInitializer(basalSim, "applyFixed");
		basalSim.setPATIENT_INIT("applyFixed"); //for params comparison before simulation compatibility
		basalSim.pat_init.fixed_delta = Double.parseDouble(params.get("fixed_delta"));
		basalSim.pat_init.fixed_capN = Double.parseDouble(params.get("fixed_capN"));

		basalSim.pat_init.fixed_rho = Double.parseDouble(params.get("fixed_rho"));
		basalSim.pat_init.fixed_eta = Double.parseDouble(params.get("fixed_eta"));
		basalSim.pat_init.fixed_kappa = Float.parseFloat(params.get("fixed_kappa"));
		basalSim.pat_init.fixed_capE = Double.parseDouble(params.get("fixed_capE"));
		basalSim.pat_init.fixed_psi = Double.parseDouble(params.get("fixed_psi"));
				
		basalSim.prov_init = new ProviderInitializer(basalSim, "applyFixed");
		basalSim.setPROVIDER_INIT("applyFixed");
		basalSim.prov_init.fixed_lambda = Double.parseDouble(params.get("fixed_lambda"));
		basalSim.prov_init.fixed_tau = Double.parseDouble(params.get("fixed_tau"));
		
		return basalSim;
	}
	
	private Care basalRun(Care basalSim, String policy) {		
		basalSim.setPi(policy);
		basalSim.start();
		basalSim.startObserver(true, false, false, false, false, false, false, false, false);
		basalSim.writeToCheckpoint(new File(checkpoint_path+"/"+Long.toString(basalSim.getSeed())+"_"+"initialization"));
		for(int i=0;i<basalSim.getvarsigma();i++) {
			if( 
				basalSim.schedule.step(basalSim) 

			) {}else{System.out.println("ups! failed Care"); break;}
			if(basalSim.schedule.getSteps() == checkoutAT) {
				basalSim.writeToCheckpoint(new File(checkpoint_path+"/"+Long.toString(basalSim.getSeed())+"_"+policy));
			}
		}
		basalSim.finish();
		return basalSim;
	}
	
	public Care interventionFromCheckpoint(String seed, String policy, int varsigma) {
		Care interventionSim = null;
		interventionSim = (Care)interventionSim.readFromCheckpoint(new File(checkpoint_path+"/"+seed+"_basal"));
		interventionSim.prioritize.changePolicy("H_segmented");
		for(int i=checkoutAT;i<varsigma;i++) {
			if(
					interventionSim.schedule.step(interventionSim) 
			) {}else{System.out.println("ups! failed Care"); break;}
		}
		interventionSim.finish();
		return interventionSim;
	}
	
}
