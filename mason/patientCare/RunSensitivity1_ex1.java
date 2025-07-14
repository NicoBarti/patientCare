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
	static String id;

	//internals
	Care care;
	double[][] finalH;
	double[][] storage_H = new double[REPS][max_varsigma]; 
	double[][] storage_normH = new double[REPS][max_varsigma]; 

	double averages;

	
	public static void main(String[] args) {
		max_varsigma = Integer.valueOf(args[0]);
		path = args[1];
		REPS = Integer.valueOf(args[2]);
		id = Long.toString(System.currentTimeMillis()) ;
		new RunSensitivity1_ex1();
	}
	
	public RunSensitivity1_ex1(){
		int fromRep = 0;
		//finalpath = "/Users/nicolasbarticevic/Desktop/CareEngineAnalytics/data/sensitivity_1/normalizedH/"+"varsigma_"+varsigma+"_ex1seed"+seed+".csv";
		System.out.println("Running random simulations with varsigma in [1:"+max_varsigma+"] and "+REPS+" repetitions");

		for(int repetition=0;repetition<REPS;repetition++) {
			for(int timestep = 1;timestep<max_varsigma;timestep++) {
				//System.out.println("Running simulations with varsigma = "+timestep);
					run_ex1(timestep, repetition);
				}
			if(((repetition)%15 ==0 & repetition !=0) | repetition == REPS-1) {
				System.out.println("Saving repetitions "+fromRep+" to "+repetition+" in "+path);
				outputWriter writer1 = new outputWriter(path, fromRep, repetition, max_varsigma, "H");
				writer1.write(storage_H, repetition, id);
				outputWriter writer2 = new outputWriter(path, fromRep, repetition, max_varsigma, "normH");
				writer2.write(storage_normH, repetition, id);
				fromRep = repetition;
			}
		}
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
}
