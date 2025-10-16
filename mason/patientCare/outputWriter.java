package patientCare;

import java.io.FileWriter;
import java.util.HashMap;

import java.io.IOException;

import com.opencsv.CSVWriter;

/**
 * Methods to write simulation outputs files
 */
public class outputWriter {

	static String path;
	static int start_REP;
	static int end_REP;
	static int length_array;
	static double[][] outputDb;
	static int[][] outputInt;
	String[] stored_values_to_string;
	String dir;
	
	public outputWriter(String path_value, int start_REP_value, int end_REP_value, int length_array_value, String dir_value) {
		path = path_value;
		start_REP =start_REP_value;
		end_REP=end_REP_value;
		length_array=length_array_value;
		stored_values_to_string= new String[length_array];
		dir = dir_value;
	}
	
	public outputWriter(String path_value, int start_REP_value, int end_REP_value, String dir_value) {
		path = path_value;
		start_REP =start_REP_value;
		end_REP=end_REP_value;
		length_array=1;
		stored_values_to_string= new String[length_array];
		dir = dir_value;
	}
	
	public void write(double[] output , int number, String id) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(path+"/"+dir+"/"+"time_"+number+"pathFinder_"+id+".csv"))) {
			String[] header = new String[] {dir};
					int headerIndex = 0;
			writer.writeNext(header,true);
			
			for(int repetition = start_REP; repetition<end_REP;repetition++) {
				//int storedIndex = 0;
				stored_values_to_string[0] = Double.toString(output[repetition]);
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			System.exit(0);
		}
	}
	
	public void write(long[] output , int number, String id) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(path+"/"+dir+"/"+"time_"+number+"pathFinder_"+id+".csv"))) {
			String[] header = new String[] {dir};
					int headerIndex = 0;
			writer.writeNext(header,true);
			
			for(int repetition = start_REP; repetition<end_REP;repetition++) {
				//int storedIndex = 0;
				stored_values_to_string[0] = Long.toString(output[repetition]);
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			System.exit(0);
		}
	}
	
	public void write(double[][] output , int number, String id) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(path+"/"+dir+"/"+"varsigma_"+number+"_ex1_runID"+id+".csv"))) {
			String[] header = new String[length_array];
					int headerIndex = 0;
			      for(int width =0; width<length_array;width++) {
			         header[headerIndex] = Integer.toString(width+1);
			         headerIndex+=1;
			        }
			writer.writeNext(header,true);
			
			for(int repetition = start_REP; repetition<end_REP;repetition++) {
				//int storedIndex = 0;
				for(int width=0;width<length_array;width++) {
					stored_values_to_string[width] =  Double.toString(output[repetition][width]);
				//	storedIndex+=1;
					}
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			System.exit(0);
		}
	}
	
	//To write the seed, params, and data alltogether. Seeds is in Hash
	public void write(double[] output, HashMap[] params, int number, String id) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(path+"/"+dir+"_all/"+"varsigma_"+number+"_ex1_runID"+id+".csv"))) {
			String[] header = new String[params[0].size()+1];
			header[0] = dir;
			for(int i =1;i<params[0].size()+1;i++) {
				header[i] = (String)params[0].keySet().toArray()[i-1];
			}
			writer.writeNext(header,true);			
			for(int repetition = start_REP; repetition<end_REP;repetition++) {
				//int storedIndex = 0;
				String[] line = new String[params[0].size()+1];
				line[0] = Double.toString(output[repetition]);
				for(int par =1;par<params[0].size()+1;par++) {
					line[par] = (String)params[repetition].get(header[par]);
				}
							
				writer.writeNext(line,true);
			}

		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			System.exit(0);
		}
	}
	
	
}






