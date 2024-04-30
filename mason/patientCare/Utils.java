package patientCare;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class Utils {
	
	public Utils() {
		
	}

	public String saveToCSV(int[] distr0, String finalPath) {
		System.out.println("Saving the distribution to " + finalPath);
		List<String[]> list = new ArrayList<>();
		String[] frequency = {"counts"};
		list.add(frequency);
		for (int i = 0; i < distr0.length; i++) {
			String[] a = {Integer.toString(distr0[i])};
			list.add(a);
		}
		try (CSVWriter writer = new CSVWriter (new FileWriter(finalPath))) {
			writer.writeAll(list);
		} catch (IOException e) {
			System.out.println("Problem in CS Writer "+e);
			return "Bad";
		}
		return "listo";
	}
	
}
