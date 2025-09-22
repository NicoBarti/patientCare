package patientCare;

import org.junit.jupiter.api.Test;

public class testCsv {
	
	@Test
	void readCSV() {
		csvReader reader = new csvReader("/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/care6_correction/onlySeeds.csv");
		long[] result = reader.readSeeds();
		for (int i =0; i< result.length; i++) {
			System.out.println("reader " +result[i]);
		}
		
	}
	

}
