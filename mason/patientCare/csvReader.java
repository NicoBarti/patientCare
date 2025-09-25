package patientCare;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;


import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class csvReader {
	
	static String path;
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
		
		// Java code to illustrate reading a
		// all data at once
		public static void readAllDataAtOnce(String file)
		{
			 try {

			        // Create an object of filereader
			        // class with CSV file as a parameter.
			        FileReader filereader = new FileReader(path+file);

			        // create csvReader object passing
			        // file reader as a parameter
			        CSVReader csvReader = new CSVReader(filereader);
			        String[] nextRecord;

			        // we are going to read data line by line
			        while ((nextRecord = csvReader.readNext()) != null) {
			            for (String cell : nextRecord) {
			                System.out.print(cell + "\t");
			            }
			            System.out.println();
			        }
			    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		public void readCSVreader(String file) {
			List<List<String>> records = new ArrayList<List<String>>();
			try (CSVReader csvReader = new CSVReader(new FileReader(path+file));) {
			    String[] values = null;
			    while ((values = csvReader.readNext()) != null) {
			        records.add(Arrays.asList(values));
			    }
			}		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		
		public HashMap readPathFinderOutput(String file) {
			String line;
			String delimiter = ",";
			Boolean firstline = true;
			String[] header = {};
			HashMap<String, String> result = new HashMap();

			try (BufferedReader br = new BufferedReader(new FileReader(path+file))) {
			   while((line = br.readLine()) != null) {
			     String[] values = line.split(delimiter);
			     if (firstline) {
			    	 header = new String[values.length];
			    	 for (int i=0; i<header.length; i++) {
			    		 header[i] = values[i];
			    	 }
			    	 firstline = false;
			     } else {
			    	 for (int i=0; i<values.length;i++) {
			    	 result.put(header[i].replace("\"", "") , values[i].replace("\"", ""));
			    	 }
			     }
			     
			   	}
			} catch (IOException e) {
			            System.err.println(e.getMessage());
			        }
		return result;
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

