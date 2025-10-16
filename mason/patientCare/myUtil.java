package patientCare;

public class myUtil {
	
	public myUtil() {}

	/** Generate an array of sequential indexes
	 * @param len lenght of the array
	 * @param index with which index to start
	 * @return an array of sequential (circular) indexes
	 */
	protected int[] accessArray(int len, int index) {
	int[] randomAccessArray = new int[len]; 
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
