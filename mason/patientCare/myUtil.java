package patientCare;

public class myUtil {
	
	public myUtil() {}

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
