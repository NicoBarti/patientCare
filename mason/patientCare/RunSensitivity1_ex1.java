package patientCare;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class RunSensitivity1_ex1 {

	static int max_varsigma;
	static String path;
	static int REPS;
	CSVWriter writer;
	
	//internals
	Care care;
	double[][] finalH;
	String finalpath;
	double[][] storage_H = new double[REPS][max_varsigma]; 
	double[][] storage_normH = new double[REPS][max_varsigma]; 

	String[] stored_values_to_string;
	double averages;

	
	public static void main(String[] args) {
		max_varsigma = Integer.valueOf(args[0]);
		path = args[1];
		REPS = Integer.valueOf(args[2]);
		new RunSensitivity1_ex1();
	}
	
	public RunSensitivity1_ex1(){
		System.out.println("Starting writters");
		//finalpath = "/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/normalizedH/"+"varsigma_"+varsigma+"_ex1seed"+seed+".csv";

		stored_values_to_string = new String[max_varsigma];
		
		System.out.println("Running random simulations with varsigma in [1:"+max_varsigma+"] and "+REPS+" each timestep");
		for(int timestep = 1;timestep<max_varsigma;timestep++) {
			System.out.println("Running simulations with varsigma = "+timestep);
			for(int repetition=0;repetition<REPS;repetition++) {
				if((repetition+1)%15 ==0) {System.out.println("run number"+repetition);}
				if(repetition == REPS-1) {System.out.println("last run");}
				run_ex1(timestep, repetition);
				}
			}
		writer_H("/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/");
		writer_normH("/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/");
		System.out.println("All done");
	}
	
	public void run_ex1(int varsigma, int repetition){
		care = new Care(System.currentTimeMillis());
		care.setOBS_PERIOD(varsigma);
		care.setPATIENT_INIT("sensitivity_1");
		care.setPROVIDER_INIT("sensitivity_1");
		care.setvarsigma(varsigma);
		care.start();
		for(int i=0;i<varsigma;i++) {
			care.schedule.step(care);
		}
		care.finish();
		
		//average among p and store H:
		averages = 0;
		for(int p=0;p<care.observer.getH().length;p++) {
			averages += care.observer.getH()[p][care.observer.getH()[0].length-1];
		}
		storage_H[repetition][varsigma] = averages/care.observer.getH().length;
		//average among p and store normH:
		averages = 0;
		for(int p=0;p<care.observer.getH().length;p++) {
			averages += care.observer.getH_norm()[p][care.observer.getH_norm()[0].length-1];
		}
		storage_normH[repetition][varsigma] = averages/care.observer.getH_norm().length;
	}
	
	public void writer_H(String totalpath) {
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(totalpath+"H/"+"max_varsigma"+max_varsigma+"_ex1.csv"))) {
			String[] header = new String[max_varsigma];
			for(int timestep =0; timestep<max_varsigma;timestep++) {
				header[timestep] = Integer.toString(timestep+1);
			}
			writer.writeNext(header,true);
			
			for(int repetition = 0; repetition<REPS;repetition++) {
				for(int timestep=0;timestep<max_varsigma;timestep++) {
					stored_values_to_string[timestep] =  Double.toString(storage_H[repetition][timestep]);
				}
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
		}
	}
	
	public void writer_normH(String totalpath) {
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(totalpath+"normH/"+"max_varsigma"+max_varsigma+"_ex1.csv"))) {
			String[] header = new String[max_varsigma];
			for(int timestep =0; timestep<max_varsigma;timestep++) {
				header[timestep] = Integer.toString(timestep+1);
			}
			writer.writeNext(header,true);
			
			for(int repetition = 0; repetition<REPS;repetition++) {
				for(int timestep=0;timestep<max_varsigma;timestep++) {
					stored_values_to_string[timestep] =  Double.toString(storage_normH[repetition][timestep]);
				}
				writer.writeNext(stored_values_to_string,true);
			}
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
		}
	}
	
	public Boolean saveH(double[][] H, String totalpath) {
		int windows = H[0].length;
		int N = H.length;
		List<String[]> list = new ArrayList<>();
		//build csv head 
		String[] head = new String[windows];
		for(int i = 0; i< windows;i++) {
			head[i] = Integer.toString(i);
		}
		list.add(head);
		String[] p_row = new String[windows];
		for(int p = 0; p<N; p++) { //for each patient
			for(int i = 0; i< windows ;i++) {
				p_row[i] = Double.toString(H[p][i]);
			}
			list.add(p_row);
		}
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(totalpath))) {
			writer.writeAll(list);
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			return false;
		}
		return true;
	}
	
	
}
