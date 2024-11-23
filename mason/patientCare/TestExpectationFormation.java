package patientCare;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;

public class TestExpectationFormation extends SimState{
		private static String pth;
		//constrains
		public int capacity = 3;
		public int numPatients = 3;
		public int weeks = 52;
		
		// HYPERPARAMETERS:
		private static double k = 1;
		
		// tests params
		double[] expectations;
		double[] needs;

		// internals
		public Bag patients = new Bag(numPatients);
		
		public TestExpectationFormation(long seed) {
			super(seed);			
		}
		
		public static void main(String[] args) {
			if (args.length == 0) {
				System.out.println("path missing");
				return;
			}
			
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("path")) {
					try {
						pth = args[i+1];

					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Error path not specified");
						return;
					}
				}
				
				if (args[i].equals("k")) {
					k = Double.valueOf(args[i+1]);
				}
				
			}

			//System.out.println("TESTING");
			doLoop(TestExpectationFormation.class, args);
			System.exit(0);
		}
		
		private double[] generateTestArray(int min, int max, double step) {
			double[] arr = new double[(int)((max-min)/step)];
			double interval = 0;
			for(int i = 0; i < (int)((max-min)/step); i++) {
				arr[i] = min + interval;
				interval = interval + step;
			}
			return(arr);
		}

		public void start() {
			super.start();
			
			expectations = generateTestArray(0,6,0.1);
			needs = generateTestArray(0,6,0.5);
			double[][] motivat1 = new double[expectations.length][needs.length];

			
			// initialize and add patients
			Patient patient1 = new Patient();
			patient1.initializePatient(1, weeks, 1);
			patient1.current_week = 1;
			motivat1 = doTestBehaviouralRule(patient1);
			saveToCSV(motivat1, getFinalPath());
			
		}
		
		private double[][] doTestBehaviouralRule(Patient patient) {
			//double[] res = new double[expectations.length*needs.length];
			double[][] res = new double[expectations.length][needs.length];
			for(int e = 0; e < expectations.length; e++) { //for each expectation
				for(int n = 0; n< needs.length; n++) { // test one need
					patient.H[1] = needs[n];
					patient.expectation[1] = expectations[e];
					patient.behaviouralRule(k, 1);
					res[e][n] = patient.currentMot;
				}
			}
			return(res);
		}
		
		
		public void saveToCSV(double[][] motivations, String finalPath) 
		{
			System.out.println("Saving the distribution to " + finalPath);
			List<String[]> list = new ArrayList<>();
			String[] header = {"k", "exp", "H", "motivation"};
			list.add(header);
			for(int e = 0; e < expectations.length; e++) {
				for(int n = 0; n < needs.length; n++) {
					String[] a = new String[4];
					a[0] = Double.toString(k);
					a[1] = Double.toString(expectations[e]);
					a[2] = Double.toString(needs[n]);
					a[3] = Double.toString(motivations[e][n]);
					//System.out.println(a[2]);
					list.add(a);
				}
				
			}

			try (CSVWriter writer = new CSVWriter(new FileWriter(finalPath))) {
				writer.writeAll(list);
			} catch (IOException e) {
				System.out.println("Problem in CS Writer " + e);
				System.out.println("Bad") ;
			}
			System.out.println("listo");
		}
		
		private String getFinalPath() {
			return pth + "/" + "k_" + String.valueOf(k) + ".csv";
		}
		

		
		public void finish() {
		}
		
		
		
}
