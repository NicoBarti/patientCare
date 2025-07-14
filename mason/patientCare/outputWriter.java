package patientCare;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class outputWriter {

	CSVWriter writer2;
	static String path;
	static int start_REP;
	static int end_REP;
	static int max_varsigma;
	static double[][] outputDb;
	static int[][] outputInt;
	String[] stored_values_to_string;
	String dir;

	public outputWriter(String path_value, int start_REP_value, int end_REP_value, int max_varsigma_value, String dir_value) {
		path = path_value;
		start_REP =start_REP_value;
		end_REP=end_REP_value;
		max_varsigma=max_varsigma_value;
		stored_values_to_string= new String[max_varsigma];
		dir = dir_value;
	}
	
	public void write(double[][] output , int number, String id) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(path+"/"+dir+"/"+"varsigma_"+number+"_ex1_runID"+id+".csv"))) {
			String[] header = new String[max_varsigma];
			      for(int timestep =0; timestep<max_varsigma;timestep++) {
			         header[timestep] = Integer.toString(timestep+1);
			        }
			writer.writeNext(header,true);
			
			for(int repetition = start_REP; repetition<end_REP;repetition++) {
				for(int timestep=0;timestep<max_varsigma;timestep++) {
					stored_values_to_string[timestep] =  Double.toString(output[repetition][timestep]);
					}
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			System.exit(0);
		}
	}
	
	
}
