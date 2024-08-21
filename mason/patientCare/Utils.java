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

	public String saveToCSV(int[] serviceDist, int[] capacityDist, double[] probabilityDist,
			double[][] expectationDist, 
			double[][] satisfactionDist, String finalPath) {
		System.out.println("Saving the distribution to " + finalPath);
		List<String[]> list = new ArrayList<>();
		String[] frequency = buildHeader();
		list.add(frequency);
		for (int i = 0; i < serviceDist.length; i++) {
			String[] a = new String[weeks*2+3];
			int h = weeks+3;
			a[0] = Integer.toString(serviceDist[i]);
			a[1] = Integer.toString(capacityDist[i]);
			a[2] = Double.toString(probabilityDist[i]);
			for (int ii = 3; ii< weeks ; ii++) {
				a[ii] = Double.toString(expectationDist[i][ii]);
				a[h] = Double.toString(satisfactionDist[i][ii]);
				h += 1;		
			}
			list.add(a);
		}
		try (CSVWriter writer = new CSVWriter(new FileWriter(finalPath))) {
			writer.writeAll(list);
		} catch (IOException e) {
			System.out.println("Problem in CS Writer " + e);
			return "Bad";
		}
		return "listo";
	}
	
	
	
	public String[] buildHeader() {
		String[] head = new String[weeks*2+1];
		head[0] = "counts";
		head[1] = "capacity";
		head[2] = "probability";
		int h = weeks+1;
		for(int i = 1; i<weeks+1 ; i++) {
			head[i] = "exp"+ Integer.toString(i);
			head[h] = "sat" + Integer.toString(i);
			h += 1;
		}
		return head;
	}

	

}
