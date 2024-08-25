package patientCare;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class Utils {
	int weeks = 52;

	public Utils() {

	}

	public String saveToCSV(int[] serviceDist, int[] capacityDist, //double[] probabilityDist,
			int[] capUse,
			double[][] expectationDist, 
			double[][] satisfactionDist, double[][] PDist,
			String finalPath) {
		System.out.println("Saving the distribution to " + finalPath);
		List<String[]> list = new ArrayList<>();
		String[] frequency = buildHeader();
		list.add(frequency);
		for (int i = 0; i < serviceDist.length; i++) {
			String[] a = new String[weeks*3+2];
			int h = weeks+2;
			int r = 2*weeks+2;
			a[0] = Integer.toString(serviceDist[i]);
			a[1] = Integer.toString(capacityDist[i]);
			//a[2] = Double.toString(probabilityDist[i]);
			for (int ii = 0; ii< weeks ; ii++) {
				a[ii+2] = Double.toString(expectationDist[i][ii]);
				a[h] = Double.toString(satisfactionDist[i][ii]);
				a[r] = Double.toString(PDist[i][ii]);
				h += 1;		
				r += 1;
			}
			list.add(a);
		}
		// add last line with used capacity
		String[] capacityReport = new String[weeks*3+2];
		for(int i = 0; i < weeks*3+2; i++) {
			if(i < weeks) {
			capacityReport[i] = Integer.toString(capUse[i]);}
			else {
				capacityReport[i] = "0";
			}
		}
		// add capacity report to the last row of dataframe
		list.add(capacityReport);
		System.out.println("LAST LINE OF FRAME IS CAPACITY USE");
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(finalPath))) {
			writer.writeAll(list);
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			return "Bad";
		}
		return "listo";
	}
	

	public String[] buildHeader() {
		String[] head = new String[weeks*3+2];
		head[0] = "counts";
		head[1] = "patient_cap";
		//head[2] = "probability";
		int h = weeks+2;
		int r = 2*weeks + 2;
		for(int i = 2; i<weeks+2 ; i++) {
			head[i] = "exp"+ Integer.toString(i-1);
			head[h] = "sat" + Integer.toString(i-1);
			head[r] = "P" + Integer.toString(i-1);
			h += 1;
			r += 1;
		}
		return head;
	}
	
	

}
