package patientCare;

public class runOneFactorTime {

	static int max_varsigma;
	static int observe_from;
	static String path;
	static int REPS;
	static String id;
	static String freeParam;
	static int n_values;
	

	//internals
	Care care;
	Provider provider;
	double[][] finalH;
	double[][] storage_H = new double[REPS][n_values]; 
	int[][] paramValues = new int[REPS][n_values];
	double sumAcrossPatients ;
	double sumPatient;
	
	public static void main(String[] args) {
		observe_from = Integer.valueOf(args[0]);
		max_varsigma = Integer.valueOf(args[1]);
		path = args[2];
		REPS = Integer.valueOf(args[3]);
		freeParam = "W";
		id = Long.toString(System.currentTimeMillis()) ;
		n_values = 51; //parameter specific
		new runOneFactorTime();
	}
	
	public runOneFactorTime(){
		//long timer = System.currentTimeMillis();
		int fromRep = 0;
		System.out.println("Running simulations up to: "+max_varsigma+" steps and "+REPS+" repetitions for free param: "+freeParam +
				", obseving at each step, avereging from step: "+ observe_from);

		
		for(int repetition=0;repetition<REPS;repetition++) { 
			for(int value = 1; value < n_values; value++) { //start_value needs parametrization
					run(value, repetition);
				}
			if((repetition)%2 ==0){System.out.print(".");}
			if(((repetition)%5 ==0 & repetition !=0) | repetition == REPS-1) {
				System.out.println("Saving repetitions "+fromRep+" to "+repetition+" in "+path);
				outputWriter writer1 = new outputWriter(path, fromRep, repetition, n_values, "W/H");
				//writer1.min_varsigma = min_varsigma;
				writer1.write(storage_H, repetition, id);
				fromRep = repetition;
				if(repetition == REPS-1) {
					outputWriter finalWriter = new outputWriter(path, fromRep, REPS, n_values, "W/H");
					//writer1.min_varsigma = min_varsigma;
					finalWriter.write(storage_H, REPS, id);
				}
			}
		}
		System.out.println("All done");
		//System.out.print(System.currentTimeMillis() - timer);
		System.exit(0);
	}
	
	public void run(int value, int repetition){
		//run a simulation up to max_varsigma
		care = new Care(System.currentTimeMillis());
		care.setJob((long)repetition);
		care.OBS_PERIOD =1;
		care.N = 2000; //parameter specific
		care.totalCapacity = 500; //parameter specific (maybe pass me care and I'll configure it for you)
		care.setW(value); //parameter specific
		care.setPATIENT_INIT("basal");
		care.setPROVIDER_INIT("basal");
		care.setvarsigma(max_varsigma);
		care.start();
		int totalCap = 0;
		for(int w = 0; w<care.W;w++) {
			totalCap+=((Provider)care.providers.objs[w]).A_w;
		}
		if(totalCap < care.totalCapacity) {
			//System.out.println("need to fix capacity by "+(care.totalCapacity-totalCap));
			for(int addCap=0;addCap<(care.totalCapacity-totalCap);addCap++) {
				//System.out.println("fixing "+addCap);

				((Provider)care.providers.objs[addCap]).A_w +=1; 
			}
		}
		
		for(int i=0;i<max_varsigma;i++) {
			care.schedule.step(care);
		}
		care.finish();
		//average among p and store H:
		sumAcrossPatients = 0;
		for(int p=0;p<care.observer.getH().length;p++) {
			for(int obseverIndex = observe_from;obseverIndex<max_varsigma; obseverIndex++) {
				sumAcrossPatients += care.observer.getH()[p][obseverIndex];}
			}
		storage_H[repetition][value] = sumAcrossPatients/(care.N*(max_varsigma-observe_from)); //parameter specific
		care.kill();
	}	
}
