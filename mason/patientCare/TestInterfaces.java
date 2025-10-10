package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;
import java.util.HashMap;



public class TestInterfaces {
	RunWithParams runWithParams;
	Care simulation;
	Set<String> files;

	
	@Test
	void run_PathFinder_recoverWith_RunWithParams() {
		int varsigma = 480;
		String path = "/Users/nicolasbarticevic/Git/testingTrash";
		HashMap<String, String> params;
		
		for (int i =0;i<5; i++) {
		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "path", path, 
				"dontExit", "true","N", "500", "totalCapacity", "100", "fixedDelta", "3"});
		double[][] observedH = pathfinder.care1.observer.getH();
		double averageHPathfinder = 0;
		for (int p = 0; p < observedH.length; p++) {
			averageHPathfinder += observedH[p][1];
		}
		averageHPathfinder = averageHPathfinder/observedH.length;
		params = pathfinder.care1.getParams();
		params.put("reproduce_line", "true");
		params.put("obsH", "true");

		RunWithParams reproduction = new RunWithParams(convertToParamString(params));
		reproduction.runSimulation();
		
		double[][] observedH2 = reproduction.getSimulation().observer.getH();
		double averageRunWithParams = 0;
		for (int p = 0; p < observedH2.length; p++) {
			averageRunWithParams += observedH2[p][1];
		}
		averageRunWithParams = averageRunWithParams/observedH2.length;
		assertEquals(averageHPathfinder, averageRunWithParams);
		}
	}
	
	@Test
	void run_PathFinder_and_RecoverLineFromCSV() {
		// if this test takes long, the path has too many .csv files
		int varsigma = 480;
		String path = "/Users/nicolasbarticevic/Git/testingTrash/recoverPathFinder";
		//String path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/pathFinderOutputs/care6_consistentSeed/500_fixCap200N5000Delta3";
		//String path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/pathFinderOutputs/care6_consistentSeed/debug";
		


		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "path", path, 
				"dontExit", "true", "alltogether", "true"});
		pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "path", path, 
				"dontExit", "true", "alltogether", "true", "N", "500", "totalCapacity", "100"});
		
		try (Stream<Path> stream = Files.list(Paths.get(path+"/H_all"))) {
	        files = stream
	          .filter(file -> !Files.isDirectory(file))
	          .map(Path::getFileName)
	          .map(Path::toString)
	          .filter(file -> isCSV(file))
	          .collect(Collectors.toSet());
	    } catch (IOException e) {
			System.out.println("Problem reding directory " + e);
			System.exit(0);
		}
		
		//Read each file
		Iterator value = files.iterator();
		HashMap<String,String> params_and_outputs = new HashMap();
		RunWithParams reproduction;

		csvReader reader = new csvReader(path+"/H_all/");
		//Extract the params and outputs for each file (PathFinder run)
		while (value.hasNext()) {
			String file = (String)value.next();
			params_and_outputs = reader.readPathFinderOutput(file);
			// build the params line for the RunWithParams
			params_and_outputs.put("reproduce_line", "true");
			params_and_outputs.put("obsH", "true");
		
			reproduction = new RunWithParams(convertToParamString(params_and_outputs));
			reproduction.runSimulation();
			//System.out.println("(Going to get observer)");

			double[][] replicatedHs = reproduction.getSimulation().observer.getH();
			double replicatedAverageH = 0; 
			for(int p = 0;p<replicatedHs.length;p++) {
				replicatedAverageH = replicatedAverageH + replicatedHs[p][1];
			}
			replicatedAverageH = replicatedAverageH/replicatedHs.length;
					
			assertEquals(params_and_outputs.get("H"), String.valueOf(replicatedAverageH));			//TODO try to have care to recover the seed before after starting the simulation for the recover case
		}
		
		
	}
	
	//@Test
	public void debug() {
		String path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/pathFinderOutputs/care6_consistentSeed/debug";
		csvReader reader = new csvReader(path+"/H_all/");
		HashMap<String,String> params_and_outputs = new HashMap();
		params_and_outputs = reader.readPathFinderOutput("try.csv");
		params_and_outputs.put("reproduce_line", "true");
		params_and_outputs.put("obsH", "true");
		RunWithParams reproduction;

		reproduction = new RunWithParams(convertToParamString(params_and_outputs));
		reproduction.runSimulation();
		

		double[][] replicatedHs = reproduction.getSimulation().observer.getH();
		double replicatedAverageH = 0; 
		for(int p = 0;p<replicatedHs.length;p++) {
			replicatedAverageH = replicatedAverageH + replicatedHs[p][1];
		}
		replicatedAverageH = replicatedAverageH/replicatedHs.length;
		System.out.println("__ _ DEBUG H "+replicatedAverageH);
	}

	public boolean isCSV(String filename) {
		if (filename == null) {
		    return false;
		}
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex >= 0) {
		    return filename.substring(dotIndex + 1).equals("csv");
		}
		return false;

	}
	
	public String convertToParamString(HashMap<String,String> values) {
		String args = "{";
		for(String key : values.keySet()) {
			args = args + "\""+ key +"\":"+ " [" + values.get(key) + "], ";
			//System.out.println(args);
		}
		args = args+"}";
		return args;
	}
	

}
