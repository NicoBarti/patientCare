package patientCare;

import sim.engine.SimState;

public class randomArray extends SimState{
	
	public randomArray(long seed) {
		super(seed);
	}
	
	public static void main(String[] args) {
		doLoop(randomArray.class, args);
		System.exit(0);
	}
	
	public void start() {
		super.start();
		int len=4;
		double[] expectationsPerProvider = {0.9,3,5.2,5.2};
		int[] randomAccess = randomArray(len); 	
		int indexMaxExpectation = randomAccess[0];
		for(int i = 1; i < len;i++) {
			if(expectationsPerProvider[randomAccess[i]] > expectationsPerProvider[indexMaxExpectation]) {
				indexMaxExpectation = randomAccess[i];
				System.out.println(indexMaxExpectation);

			}
		}
		System.out.println("max:"+expectationsPerProvider[indexMaxExpectation]);
		System.out.println("prev:"+expectationsPerProvider[indexMaxExpectation-1]);


	}
	
	public int[] randomArray(int len) {
	int[] randomAccessArray = new int[len]; // to break ties at random
	int index = this.random.nextInt(len);
	//int index = 0;
	int counter = 0;
	while (counter < len) {
		randomAccessArray[index] = counter;
		index += 1;
		counter += 1;
		if (index == len) {index = 0;}
	}
	return(randomAccessArray);
	}
}
