package runners;

import java.util.HashMap;

import patientCare.Care;
import patientCare.PatientInitializer;
import patientCare.ProviderInitializer;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;



public class VarianceDesignExplorer {
		
	/** Vector of parameters (ordered)
	 * 0 fixed_lambda & 1 fixed_tau & 2 fixed_kappa & 3 fixed_rho & 4 fixed_eta 
	 * & 5 fixed_capN & 6 fixed_capE & 7 fixed_psi &  8 N & 9 W & 10 fixed_delta & 11 A & 12 Pi
	 */
	double[] paramVec = new double[12];
	int TIMES = 100;
	int fromRep = 0;
	HashMap[] params = new HashMap[TIMES];
	static String path;
	static long seed;
	
	
	double[] storage_H75 = new double[TIMES];
	double[] storage_H100 = new double[TIMES];
	double[] storage_V75 = new double[TIMES];
	double[] storage_V100 = new double[TIMES];
	int storedRepetitions =0;
	
	public static void main(String[] args) {
		seed = System.currentTimeMillis();
		path = System.getProperty("user.dir"); //Crea subdirectorio en directorio local
		System.out.println(path);
		Path dir = Paths.get(path + "/"+seed);
		try {
		Files.createDirectory(dir);}
		catch (Exception e) {
			System.out.print(e);

		}
		new VarianceDesignExplorer();
	}


	public VarianceDesignExplorer() {
		int varsigma = 100;
		String[] names = {"H75", "H100", "p75", "p100"}; //p is proportion of people at 0
		
		while(true) { //starts eternal loop, system is exit from generateParams();
			if(gridCounter==TIMES) {
				System.out.print("Grid completed");
				System.exit(0);
			};
			
		paramVec = generateParams();
		Care sim1 = new Care(seed);
		configureCare(sim1, varsigma, 25, 10, paramVec);
		sim1.start();
		sim1.startObserver(true, false, false, false, false, false, false, false, false, false,false, false);
		for(int i=0;i<varsigma;i++) {
			if(
			sim1.schedule.step(sim1) //&& 
			//care2.schedule.step(care2) 

			) {}else{System.out.println("ups! failed Care"); break;}

		}
		sim1.finish();//care2.finish();
		System.out.print(storedRepetitions);
		params[storedRepetitions] = sim1.getParams(); //before loca storage that changes storedRepetitions
		params[storedRepetitions].put("id", Integer.toString(storedRepetitions));
		localStorage(sim1); //localStorage(care2, seed2); 
	
		//write to file:
		if((storedRepetitions)%16 ==0){System.out.print(".");}
		if(((storedRepetitions)%64 ==0 & storedRepetitions >=64) | storedRepetitions > TIMES-1) {
			System.out.println("Saving repetitions "+fromRep+" to "+storedRepetitions+" in "+path);
			outputWriter writer_all = new outputWriter(path, fromRep, storedRepetitions, Long.toString(seed));
			writer_all.write(storage_H75,storage_H100,storage_V75,storage_V100, params, storedRepetitions,
					names);
			fromRep = storedRepetitions;
		}
		}
	}
	
	int gridCounter = 0;
	
	protected double[] generateParams() {
		//double[] vec;		//int A = s.random.nextInt(200)+1; //This range avoids overloaded doctors, but check if higher values needed
		//int A = gridCounter*2;
		int[] A = {1,  23,  45,  67,  89, 112, 134, 156, 178, 200};
		//int N = s.random.nextInt(4501)+500;
		//int N = 1500;
		int[] W = {1, 6, 12, 17, 23, 28, 34, 39, 45, 50};
		//int W = s.random.nextInt(50)+1;
		//int W=1;
		int[] N = {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500,5000};
		//double T = s.random.nextDouble(true,true)*2;
		//int T = 2;
		double[] T = {0, 0.22, 0.44, 0.67, 0.89, 1.11, 1.33, 1.56, 1.78, 2};
		for(int a = 0; a<A.length; a++) {
			for(int w =0;w<W.length;w++) {
				for(int n=0;n<N.length;n++) {
					for(int t=0;t<T.length;t++) {
						double[] vec = {0.5, T[t], 0, 1, 1, 10, 10, 0.5, N[n], W[w], 0, A[a], 0}; //Pi is not being parsed by ConfigureCare
					}
				}
			}
		}
		//double[] vec = {0.5, T, 0, 1, 1, 10, 10, 0.5, N, W, 0, A, 0}; //Pi is not being parsed by ConfigureCare
		gridCounter+=1;
		return vec;
	}
	
	protected void configureCare(Care sim, int varsigma, int OBS_PERIOD, double H0, double[] vec) {
		sim.setOBS_PERIOD(OBS_PERIOD);
		sim.setvarsigma(varsigma);
		
		sim.settotalCapacity((int)vec[11]);
		sim.W = (int)vec[9];
		sim.N = (int)vec[8];		
		sim.setPi("basal");
		
		sim.pat_init = new PatientInitializer(sim, "applyFixed");
		sim.setPATIENT_INIT("applyFixed"); //for params comparison before simulation compatibility
		sim.pat_init.fixed_delta = vec[10];
		sim.pat_init.fixed_capN = vec[5];
		sim.pat_init.fixed_rho = vec[3];
		sim.pat_init.fixed_eta = vec[4];
		sim.pat_init.fixed_kappa = (float)vec[2];
		sim.pat_init.fixed_capE = vec[6];
		sim.pat_init.fixed_psi = vec[7];
		sim.pat_init.initial_h = true;
		sim.pat_init.h0_value= H0;
				
		sim.prov_init = new ProviderInitializer(sim, "applyFixed");
		sim.setPROVIDER_INIT("applyFixed");
		sim.prov_init.fixed_lambda = vec[0];
		sim.prov_init.fixed_tau = vec[1];
	}

	
	protected void localStorage(Care care) {
		//average among p and store:
		double average75 = 0;
		double average100 = 0;
			for(int p=0;p<care.observer.getH().length;p++) {
			average75 += care.observer.getH()[p][3]/care.observer.getH().length;
			average100 += care.observer.getH()[p][4]/care.observer.getH().length;
			}
			//When dividing once at the end it doesn't match numpy's 
			storage_H75[storedRepetitions] = average75; //care.observer.getH().length;
			storage_H100[storedRepetitions] = average100; //care.observer.getH().length;
		//variance among p and store:
//		double var75 = 0;
//		double var100 = 0;
//			for(int p=0;p<care.observer.getH().length;p++) {
//				var75 += (care.observer.getH()[p][3] - average75) * (care.observer.getH()[p][3] - average75);
//				var100 += (care.observer.getH()[p][3] - average100) * (care.observer.getH()[p][3] - average100);
//			}
//			var75 = var75/care.observer.getH().length;
//			var100 = var100/care.observer.getH().length;
//			storage_V75[storedRepetitions] = var75; //care.observer.getH().length;
//			storage_V100[storedRepetitions] = var100; //care.observer.getH().length;
			
		int p75 = 0;
		int p100 = 0;
		for(int p=0;p<care.observer.getH().length;p++) {
			p75 = care.observer.getH()[p][3] == 0 ? p75+1: p75;
			p100 = care.observer.getH()[p][3] == 0 ? p75+1: p75;

		}
		storage_V75[storedRepetitions] = p75; //care.observer.getH().length;
		storage_V100[storedRepetitions] = p100; //care.observer.getH().length;
		
			
			storedRepetitions+=1;
			
			
		}

	
}
