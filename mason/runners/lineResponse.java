package runners;
import patientCare.*;

import java.io.File;
import java.math.*;


public class lineResponse {
	Care basalSim;
	Care interventionSim;
	int varsigma = 500;
	String checkpoint_path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/checkpoints";
	String lineResponse_path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/lineResponse";
	long seed;
	String policy = "basal";
	int checkoutAT = 200;
	
	
	//Patient initializers
	double fixed_delta;
	double fixed_capN;
	double fixed_rho;
	double fixed_eta;
	float fixed_kappa;
	double fixed_capE;
	double fixed_psi;
	
	//Provider initializers
	double fixed_lambda;
	double fixed_tau;
	
	boolean reproduce = true;

	public static void main(String[] args) {
		new lineResponse();
	}
	
	public lineResponse() {
		if (reproduce) {
			reproduceLine();
			runBasal();
			double H = basalSim.observer.getMeanFinalH();
			System.out.println(H);
			runNewPolicyFromCheckpoint();
			H = interventionSim.observer.getMeanFinalH();
		} else {
		for(int i=0;i<10;i++) {
			produceRandomLine();
			policy = "basal";
			runBasal();
			double H = basalSim.observer.getMeanFinalH();
			//System.out.println(H);
			if (H>10) {
				//save it to plot it in python
				System.out.println("orig "+basalSim.observer.getMeanFinalH());
				outputWriter writer_all = new outputWriter(lineResponse_path, (int)(Math.random()*100) , 1, "H");
				writer_all.write(H, basalSim.getParams(), Long.toString(seed)+"_"+policy);
				//re-run it with new policy
				runNewPolicyFromCheckpoint();
			}
			
		}
		}
	}
	
	private void runBasal() {		
		basalSim.start();
		basalSim.startObserver(true, false, false, false, false, false, false, false, false);
		for(int i=0;i<varsigma;i++) {
			if(
				basalSim.schedule.step(basalSim) 

			) {}else{System.out.println("ups! failed Care"); break;}
			if(basalSim.schedule.getSteps() == checkoutAT) {
				basalSim.writeToCheckpoint(new File(checkpoint_path+"/"+Long.toString(seed)+"_"+policy));
			}
		}
		basalSim.finish();
		//double H = basalSim.observer.getMeanFinalH();
		basalSim.kill();
		//return basalSim;
	}
	
	public void produceRandomLine() {
		seed = System.currentTimeMillis();
		basalSim = new Care(seed);
		basalSim.setvarsigma(varsigma);
		basalSim.setOBS_PERIOD(varsigma);
		basalSim.setN(5000);
		basalSim.settotalCapacity(200);
		basalSim.setPi(policy);
		basalSim.setPATIENT_INIT("fixed_capacity");
		basalSim.pat_init = new PatientInitializer(basalSim, "fixed_capacity");
		basalSim.pat_init.fixed_delta = 4.0;
		basalSim.setPROVIDER_INIT("fixed_capacity");
	}
	
	public void runNewPolicyFromCheckpoint() {
		interventionSim = null;
		interventionSim = (Care)interventionSim.readFromCheckpoint(new File(checkpoint_path+"/"+Long.toString(seed)+"_"+policy));
		//System.out.println("System from checkout" + seed + " H: " + Double.toString(interventionSim.observer.getMeanFinalH()));
		interventionSim.prioritize.changePolicy("H_segmented");
		for(int i=checkoutAT;i<varsigma;i++) {
			if(
					interventionSim.schedule.step(interventionSim) 
			) {}else{System.out.println("ups! failed Care"); break;}
		}
		interventionSim.finish();
		System.out.println("chec "+ Double.toString(interventionSim.observer.getMeanFinalH()));
	}
	
	protected void reproduceLine() {
		seed = 1760266891606L;
		basalSim = new Care(seed);
		basalSim.setOBS_PERIOD(100);
		basalSim.setvarsigma(500);
		basalSim.settotalCapacity(200);
		basalSim.W = 14;
		basalSim.N = 5000;
		basalSim.setPi("basal");
		
		basalSim.pat_init = new PatientInitializer(basalSim, "applyFixed");
		basalSim.setPATIENT_INIT("applyFixed"); //for params comparison before simulation compatibility
		basalSim.pat_init.fixed_delta = 4.0;
		basalSim.pat_init.fixed_capN = 0.22215;
		basalSim.pat_init.fixed_lambda = 8.159167;
		basalSim.pat_init.fixed_tau = 9.129899;
		basalSim.pat_init.fixed_rho = 1.097791;
		basalSim.pat_init.fixed_eta = 2.597487;
		basalSim.pat_init.fixed_kappa = (float)0.128903;
		basalSim.pat_init.fixed_capE = 2.959448;
		basalSim.pat_init.fixed_psi = 0.58286;
				
		basalSim.prov_init = new ProviderInitializer(basalSim, "applyFixed");
		basalSim.setPROVIDER_INIT("applyFixed");
		basalSim.prov_init.fixed_lambda = 8.159167;
		basalSim.prov_init.fixed_tau = 9.129899;
		

	}

}
