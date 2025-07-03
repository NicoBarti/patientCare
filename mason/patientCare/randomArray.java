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
		System.out.println("RANDOM access ARRAY");

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
		
		System.out.println("ACCESS ARRAY");
		len = 10;
		int index = 4;
		int[] accessArray = accessArray(len, index);
		for(int i = 0; i<accessArray.length;i++) {
			System.out.println(accessArray[i]);
		}
		
		System.out.println("Callinf from Util ");

		myUtil ut = new myUtil();
		int[] a = ut.accessArray(10, 2);
		for(int i = 0; i<a.length;i++) {
			System.out.println(a[i]);
		}

		System.out.println("Trying 2 D array");
		//three providers in two timesteps
		int[][] doble = new int[][] {{0,1,1},{1,0,0},{0,0,1},{1}};
		System.out.println("Trying 2 D array");
		System.out.println("w0,t0 "+doble[0][0]);
		System.out.println("w0,t1 "+doble[0][1]);
		System.out.println("w0,t2 "+doble[0][2]);
		System.out.println("w2,t2 "+doble[2][2]);
		System.out.println("w1,t0 "+doble[1][0]);
		System.out.println("lenght_outer "+doble.length);
		System.out.println("lenght_inner "+doble[0].length);
		int counter =0;
		for (int i=0;i<doble[0].length;i++) {
			counter += doble[0][i];
		}
		System.out.println("counter "+counter);

		
		System.out.println("Trying combined condition");
		int r = 3;
		int g = 1;
		if(r  != 2 & g != 1) {
			System.out.println("True ");
		} else {System.out.println("False ");}
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
	
	public int[] accessArray(int len, int index) {
	int[] randomAccessArray = new int[len]; 
	//int index = 0;
	int counter = 0;
	while (counter < len) {
		randomAccessArray[counter] = index;
		index += 1;
		counter += 1;
		if (index == len) {index = 0;}
	}
	return(randomAccessArray);
	}
}
