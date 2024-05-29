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

	public String saveToCSV(int[] serviceDist, double[][] motivationDist, double[][] severityDist, String finalPath) {
		System.out.println("Saving the distribution to " + finalPath);
		List<String[]> list = new ArrayList<>();
		String[] frequency = buildHeader();
		list.add(frequency);
		for (int i = 0; i < serviceDist.length; i++) {
			String[] a = new String[weeks*2+1];
			int h = weeks+1;
			a[0] = Integer.toString(serviceDist[i]);
			for (int ii = 1; ii< weeks ; ii++) {
				a[ii] = Double.toString(motivationDist[i][ii]);
				a[h] = Double.toString(severityDist[i][ii]);
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
		int h = weeks+1;
		for(int i = 1; i<weeks+1 ; i++) {
			head[i] = "mot"+ Integer.toString(i);
			head[h] = "sev" + Integer.toString(i);
			h += 1;
		}
		return head;
	}

	

}
