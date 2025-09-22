package patientCare;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class csvReader {
	
	String path;
	String line;
    String delimiter = ",";
    int numberOfSeeds;
    //"/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/care6_correction/allRuns.csv"
	
		public csvReader(String value) {
			path = value;
		}

		public long[] readSeeds() {
			//The csv is a one line per seed, without headings
			numberOfSeeds=0;
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            while((line = br.readLine()) != null) {
	            	numberOfSeeds+=1;
	            }
	        } catch (IOException e) {
	            System.err.println(e.getMessage());
	        }
	        
	        long[] seeds = new long[numberOfSeeds];
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	        	int position = 0;
	            while((line = br.readLine()) != null) {
	            	seeds[position] = Long.valueOf(line.split(delimiter)[0]);
	            	position+=1;
	            }
	        } catch (IOException e) {
	            System.err.println(e.getMessage());
	        }        
	        return seeds;
	        
	    }
		
		
//	    public static void main(String[] args) {
//	        String filePath = "/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/care6_correction/allRuns.csv";
//	        String line;
//	        String delimiter = ",";
//
//	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//	            while((line = br.readLine()) != null) {
//	                String[] values = line.split(delimiter);
//	                for (String value : values) {
//	                    System.out.println(value + " ");
//	                }
//	                System.out.println();
//	            }
//	        } catch (IOException e) {
//	            System.err.println(e.getMessage());
//	        }
//	    }
	
	
	
}

