package patientCare;

public class PathFinder {
	
	static int varsigma;
	static String path;
	static int TIMES;
	static String id;

	//internals
	double[] storage_H; 
	long[] storage_SEED;
	double averages;
	int storedRepetitions = 0;
	long seed1 = 0L;
	Boolean assignRandomSeed = true;
	//long seed3;long seed4;long seed5;long seed6;long seed7;long seed8;
	Care care1;Care care2;
	//Care care3;Care care4;Care care5;Care care6;Care care7;Care care8;
	Boolean write = true;
	
	//testing
	Boolean testing = false;
	
	public PathFinder(String[] args) {
		argsParser(args);
		//if (TIMES%2 !=0) {System.out.println("TIMES needs to be multiple of 2");System.exit(0);}
		initializer();
		id = Long.toString(System.currentTimeMillis()) ;
		if(!testing) { runRepetitions();}
	}
	
	public void runRepetitions() {
		//long timer = System.currentTimeMillis();

		int fromRep = 0;
		//finalpath = "/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/normalizedH/"+"varsigma_"+varsigma+"_ex1seed"+seed+".csv";
		System.out.println("Running "+TIMES+" random simulations with varsigma "+varsigma);

		//repetitionsDone = 0;
		while(storedRepetitions < TIMES){
			//System.out.println("Simulations run "+repetitionsDone);
			run_Care();
			//System.out.println("storedRepetitions "+storedRepetitions);
			if((storedRepetitions)%16 ==0){System.out.print(".");}
			if(write) {
			if(((storedRepetitions)%64 ==0 & storedRepetitions >=64) | storedRepetitions > TIMES-1) {
				System.out.println("Saving repetitions "+fromRep+" to "+storedRepetitions+" in "+path);
				outputWriter writer1 = new outputWriter(path, fromRep, storedRepetitions, "H");
				writer1.write(storage_H, storedRepetitions, id);
				outputWriter writer2 = new outputWriter(path, fromRep, storedRepetitions, "seeds");
				writer2.write(storage_SEED, storedRepetitions, id);
				fromRep = storedRepetitions;
			}
			}
		}
		
		//System.out.println(System.currentTimeMillis() - timer);
		System.out.println("All done");
		if(!testing) { System.out.println("Exiting");System.exit(0);}
	}
	
	protected void run_Care() {
		if(assignRandomSeed) {
			seed1 = System.currentTimeMillis();}
		
		//seed2 = seed1 + 8732435;

		care1 = new Care(seed1);
		care1.setJob(0);
		configureCare(care1);
		care1.start();
		care1.startObserver(true, false, false, false, false, false, false, false, false);

		
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
		for(int p=0;p<care.observer.getH().length;p++) {
			averages += care.observer.getH()[p][care.observer.getH()[0].length-1];
		}
		storage_H[storedRepetitions] = averages/care.observer.getH().length;
		storage_SEED[storedRepetitions] = seed;
		//System.out.println("saving " +averages/care.observer.getH().length + " in storedRepetitions "+storedRepetitions);
		storedRepetitions+=1;
	}
	
	protected void configureCare(Care care) {
		care.setOBS_PERIOD(varsigma);
		care.setPATIENT_INIT("sensitivity_1");
		care.setPROVIDER_INIT("sensitivity_1");
		care.setvarsigma(varsigma);
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
				break;
			case "seed":
				seed1 = Long.valueOf(args[argNumber+1]);
				break;

			}
		}
		if(seed1!=0) {
			System.out.println("Assigning seed "+seed1);
			assignRandomSeed = false;}
	}
	
	private void initializer() {
		id = Long.toString(System.currentTimeMillis()) ;
		storage_H = new double[TIMES]; 
		storage_SEED = new long[TIMES];
	}
}
