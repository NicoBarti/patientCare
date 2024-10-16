package patientCare;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class Utils {
	int weeks;

	public Utils() {

	}
	
	public String saveToCSV(int weeks,
			//simple variables
			int[] ds, 
			//double array series
			int[][] Cs,
			double[][] Hs,
			double[][] expectations,
			double[][] Ts,
			int[][] Bs,
			String finalPath) 
	{
		setweeks(weeks);
		System.out.println("Saving the distribution to " + finalPath);
		List<String[]> list = new ArrayList<>();
		String[] header = buildHeader();
		list.add(header);
		int arrVars = 5;
		int simpleVars = 1;
		int timeLength = weeks+1;
		for (int i = 0; i < ds.length; i++) {
			String[] a = new String[timeLength*arrVars+simpleVars];
			int p = 0*timeLength+simpleVars;
			int h = timeLength+simpleVars;
			int r = 2*timeLength+simpleVars;
			int q = 3*timeLength+simpleVars;
			int t = 4*timeLength + simpleVars;
			a[0] = Integer.toString(ds[i]);
			for (int ii = 0; ii< timeLength ; ii++) {
//				a[p] = "p";
				a[p] = Integer.toString(Cs[i][ii]);
				a[h] = Double.toString(Hs[i][ii]);
				a[r] = Double.toString(expectations[i][ii]);
				a[q] = Double.toString(Ts[i][ii]);
				a[t] = Integer.toString(Bs[i][ii]);
				h += 1;r += 1;q += 1;t += 1;p +=1;
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
		int arrVars = 5;
		int simpleVars = 1;
		int timeLength = weeks+1;
		String[] head = new String[timeLength*arrVars+simpleVars];
		head[0] = "d";
		int h = timeLength+simpleVars;
		int r = 2*timeLength + simpleVars;
		int q = 3*timeLength + simpleVars;
		int t = 4*timeLength + simpleVars;
		for(int i = 1; i<timeLength + simpleVars ; i++) {
			String currentWeek = Integer.toString(i-1);
			head[i] = "C"+ currentWeek;
			head[h] = "H" + currentWeek;
			head[r] = "exp" + currentWeek;
			head[q] = "T" + currentWeek;
			head[t] = "B" + currentWeek;
			h += 1;
			r += 1;
			q += 1;
			t += 1;
		}
		return head;
	}
	
	public void setweeks(int val) {
		weeks = val;
	}
	
	

}
