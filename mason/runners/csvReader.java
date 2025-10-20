package runners;
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

/**
 * A basic file reader whit functionality specific to this project. (Actually, some files are not necessarily csv).
 */
public class csvReader {
	
	static String path;
	String line;
    String delimiter = ",";
    int numberOfSeeds;
    //"/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/care6_correction/allRuns.csv"
	
		/** 
		 * @param value The complete path to the csv. 
		 */
		public csvReader(String value) {
			path = value;
		}

		/** Reads a file that has one seed per line. No headings allowed. 
		 * @return an array of seeds 
		 */
		public long[] readSeeds() {
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
		
		/** Reads a PathFinder generated csv that contains a simulation's results.
		 * Contains a header with variable names (i.e. Seeds, H, SimpleE), and one line with one simulation.
		 * @param file The file name (it's path was passed in the constructor).
		 * @return A hash map with the recovered simulation output
		 */
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

		/** Reads a PathFinder generated csv that contains several simulation's results.
		 * Contains a header with variable names (i.e. Seeds, H, SimpleE), and one line with one simulation.
		 * @param file The file name (it's path was passed in the constructor).
		 * @return A hash map with the recovered simulation outputs
		 */
		public HashMap<String, HashMap<String, String>> readPathFinderOutput(String file, boolean severaLines) {
			String line;
			String delimiter = ",";
			Boolean firstline = true;
			String[] header = {};
			HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> innerHash = new HashMap<String, String>();
			
			int lineNumber = 0;
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
			    	 innerHash = innerHash(header, values);
			    	 result.put(innerHash.get("seeds"), innerHash);
			    	 lineNumber+=1;
			     }
			     
			   	}
			} catch (IOException e) {
			            System.err.println(e.getMessage());
			        }
		return result;
		}
		
		private HashMap<String,String> innerHash(String[] header, String[] values){
			HashMap<String, String> innerHash = new HashMap<>();
			for (int i=0; i<values.length;i++) {
		    	 innerHash.put(header[i].replace("\"", "") , values[i].replace("\"", ""));
		    	 }
			return innerHash;
		}
		
		
}

