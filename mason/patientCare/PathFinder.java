package patientCare;
import java.util.HashMap;


public class PathFinder {
	
	static int varsigma;
	static String path;
	static int TIMES;
	static String id;
	String csvSeedsPath = null;

	//internals
	double[] storage_H; 
	long[] storage_SEED;
	HashMap[] params;
	double averages;
	int storedRepetitions = 0;
	long seed1 = 0L;
	long[] seedsFromCSV;
	String observed = "H";
	Boolean assignRandomSeed = true;
	Boolean useSeedFromCSV = false;
	int CSVseedsCounter = 0;
	//long seed3;long seed4;long seed5;long seed6;long seed7;long seed8;
	Care care1;//Care care2;
	//Care care3;Care care4;Care care5;Care care6;Care care7;Care care8;
	Boolean write = true;
	Boolean alltogether = false; //for combine output into one file
	//Fixed capacity strategy
	int totCap = -1;
	int fixN = -1;
	double fixDelta = -1;
	String fix_initialization = "";
	//boolean fixed_capacity_initialization = false;
	//boolean fixed_capacity_delta_initialization = false;
	
	//testing
	Boolean testing = false;
	Boolean dontExit = false;
	
	public PathFinder(String[] args) {
		argsParser(args);
		//if (TIMES%2 !=0) {System.out.println("TIMES needs to be multiple of 2");System.exit(0);}
		initializer();
		id = Long.toString(System.currentTimeMillis()) ;
		if(!testing) { runRepetitions();}
	}
	
	private void initializer() {
		id = Long.toString(System.currentTimeMillis()) ;

		if(seed1 !=0 & csvSeedsPath != null) {
			System.out.println("Too many parameters: seed and seeds_csv");
			System.exit(0);
		}
		if(seed1!=0) {
			System.out.println("Assigning seed "+seed1);
			assignRandomSeed = false;
			TIMES = 1;}
		if (csvSeedsPath != null) {
			csvReader reader = new csvReader(csvSeedsPath);
			seedsFromCSV = reader.readSeeds();
			useSeedFromCSV = true;
			System.out.println("(Java)PathFinder will use seeds in "+csvSeedsPath);
			TIMES = seedsFromCSV.length;
		}
		if (fixN >-1 & totCap >-1 & fixDelta < 0) {
			System.out.println("Will use fixed capacity with N "+fixN+" and totalCap "+totCap);
			//fixed_capacity_initialization = true;
			fix_initialization = "fix_capacity";
		}
		if (fixN >-1 & totCap >-1 & fixDelta > -1) {
			System.out.println("Will use fixed capacity with N "+fixN+", totalCap "+totCap +
					" and fixDelta "+fixDelta);
			//fixed_capacity_delta_initialization = true;
			fix_initialization = "fix_capacity_delta";

		}
		params = new HashMap[TIMES];
		storage_H = new double[TIMES]; 
		storage_SEED = new long[TIMES];
	}
	
	public void runRepetitions() {
		//long timer = System.currentTimeMillis();

		int fromRep = 0;
		//finalpath = "/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/normalizedH/"+"varsigma_"+varsigma+"_ex1seed"+seed+".csv";
		System.out.println("Running "+TIMES+" random simulations with varsigma "+varsigma + " observing "+ observed);

		//repetitionsDone = 0;
		while(storedRepetitions < TIMES){
			//System.out.println("Simulations run "+repetitionsDone);
			run_Care();
			//System.out.println("storedRepetitions "+storedRepetitions);
			if((storedRepetitions)%16 ==0){System.out.print(".");}
			if(write) {
			if(((storedRepetitions)%64 ==0 & storedRepetitions >=64) | storedRepetitions > TIMES-1) {
				System.out.println("Saving repetitions "+fromRep+" to "+storedRepetitions+" in "+path);

				if (alltogether) {
					outputWriter writer_all = new outputWriter(path, fromRep, storedRepetitions, observed);
					writer_all.write(storage_H, params, storedRepetitions, id);
					fromRep = storedRepetitions;
				}
				else { //for old implementation two output files
				outputWriter writer1 = new outputWriter(path, fromRep, storedRepetitions, observed);
				writer1.write(storage_H, storedRepetitions, id);
				outputWriter writer2 = new outputWriter(path, fromRep, storedRepetitions, "seeds");
				writer2.write(storage_SEED, storedRepetitions, id);
				fromRep = storedRepetitions;}
			}
			}
		}
		
		//System.out.println(System.currentTimeMillis() - timer);
		System.out.println("All done");
		if(!testing & !dontExit) { System.out.println("Exiting");System.exit(0);}
	}
	
	protected void run_Care() {
		if(assignRandomSeed) {
			seed1 = System.currentTimeMillis();}
		
		if(useSeedFromCSV) {
			seed1 = seedsFromCSV[CSVseedsCounter];
			CSVseedsCounter+=1;
		}

		care1 = new Care(seed1);
		care1.setJob(0);
		switch(fix_initialization) {
		case "fix_capacity":
			configureCare(care1, "fixed_capacity");
			break;
		case "fix_capacity_delta":
			configureCare(care1, "fixed_capacity", fixDelta);
			break;
		default:
			configureCare(care1);
			break;
		}
		//if(fixed_capacity_initialization) {
		//	configureCare(care1, "fixed_capacity");
		//} else {
		//configureCare(care1);}
		care1.start();
		if(observed.equals("H")) {
			care1.startObserver(true, false, false, false, false, false, false, false, false);}
		
		if(observed.equals("SimpleE")) {
			care1.startObserver(false, false, false, false, false, false, false, true, false);
		}

		
//		 care2 = new Care(seed2);
//		 care2.setJob(1);
//		configureCare(care2);
		
		
		for(int i=0;i<varsigma;i++) {
			if(
			care1.schedule.step(care1) //&& 
			//care2.schedule.step(care2) 

			) {}else{System.out.println("ups! failed Care"); break;}

		}
		care1.finish();//care2.finish();
		
		localStorage(care1, seed1); //localStorage(care2, seed2); 
//		localStorage(care3, seed3);
//		localStorage(care4, seed4); localStorage(care5, seed5); localStorage(care6, seed6);
//		localStorage(care7, seed7); localStorage(care8, seed8);
		
	}
	
	protected void localStorage(Care care, long seed) {
		//average among p and store H:
		averages = 0;
		if(observed.equals("H")) {
			for(int p=0;p<care.observer.getH().length;p++) {
			averages += care.observer.getH()[p][care.observer.getH()[0].length-1]/care.observer.getH().length;
			}
			//When dividing once at the end it doesn't match numpy's 
			//storage_H[storedRepetitions] = averages/care.observer.getH().length;
		}
		if(observed.equals("SimpleE")) {
			for(int p=0;p<care.observer.getSimpleE().length;p++) {
			averages += care.observer.getSimpleE()[p][care.observer.getSimpleE()[0].length-1]/care.observer.getSimpleE().length;
			}
			//When dividing once at the end it doesn't match numpy's 
			//storage_H[storedRepetitions] = averages/care.observer.getSimpleE().length;
		}
		storage_SEED[storedRepetitions] = seed;
		params[storedRepetitions] = care.getParams();
		//System.out.println("saving " +averages/care.observer.getH().length + " in storedRepetitions "+storedRepetitions);
		storedRepetitions+=1;
	}
	
	protected void configureCare(Care care) {
		care.setOBS_PERIOD(varsigma);
		care.setPATIENT_INIT("sensitivity_1");
		care.setPROVIDER_INIT("sensitivity_1");
		care.setvarsigma(varsigma);
	}
	
	protected void configureCare(Care care, String init) {
		care.setvarsigma(varsigma);
		care.setOBS_PERIOD(varsigma);
		care.setN(fixN);
		care.settotalCapacity(totCap);
		care.setPATIENT_INIT(init);
		care.setPROVIDER_INIT(init);
	}
	
	protected void configureCare(Care care, String init, Double delta) {
		care.setvarsigma(varsigma);
		care.setOBS_PERIOD(varsigma);
		care.setN(fixN);
		care.settotalCapacity(totCap);
		care.setPATIENT_INIT(init);
		care.pat_init = new PatientInitializer(care, init);
		care.pat_init.fixed_delta = delta;
		care.setPROVIDER_INIT(init);
	}
	
	public static void main(String[] args) {
		new PathFinder(args);
	}
	
	
	private void argsParser(String[] args) {
		for(int argNumber =0;argNumber<args.length;argNumber++) {
			switch (args[argNumber]) {
			case "varsigma":
				varsigma = Integer.valueOf(args[argNumber+1]);
				if(varsigma == 0) {System.out.println("varsigma cant be 0");System.exit(0);}
				break;
			case "path":
				path = args[argNumber+1];
				break;
			case "TIMES":
				TIMES = Integer.valueOf(args[argNumber+1]);
				break;
			case "testing":
				testing = Boolean.valueOf(args[argNumber+1]);
				dontExit = true;
				break;
			case "seed":
				seed1 = Long.valueOf(args[argNumber+1]);
				break;
			case "CSV_seeds":
				csvSeedsPath = args[argNumber+1];
				break;
			case "observe":
				observed = args[argNumber+1];
				break;
			case "alltogether":
				alltogether = true;
				break;
			case "dontExit":
				dontExit = true;
				break;
			case "totalCapacity":
				totCap = Integer.valueOf(args[argNumber+1]);
				break;
			case "N":
				fixN = Integer.valueOf(args[argNumber+1]);
				break;
			case "fixedDelta":
				fixDelta = Integer.valueOf(args[argNumber+1]);
				break;
	
			}
		}
	}
	

}
