package runners;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import runners.csvReader;

public class testCsv {
	
	
	@Test
	void readBulkLines() {
		String path = "/Users/nicolasbarticevic/Git/testingTrash/bulkRead/";
		csvReader reader = new csvReader(path);
		HashMap<String, HashMap<String, String>> lines =  reader.readPathFinderOutput("percenileLines.csv", true);
		
		assertEquals(lines.get("1760215510572").get("H"), "6.954791645518582");
		assertEquals(lines.get("1760231496213").get("fixed_rho"), "0.5069527187885552");
	}

}
