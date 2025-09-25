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
	
	//@Test
	//TODO maybe only test if the retreived params are ok, and the corresponding arrays exist
	//In a pathfinder run you specify a varsigma, and some outcome to look; usually only the final result
	//It's called a path finder because you get several repetitions keeping only varsigma stable and all the 
	//other params move at random.
	//The output is the params, and the seed, so you can reproduce it.
	//Also, a pathFinder with seed reproduces the call (but maybe this behaviour should be dealt with by a 
	//standard run with params.
	void try_a_pathFinder_run_Run_With_Params() {
		//runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [1752865141452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"]}");
		//runWithParams.runSimulation();
		//runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [1752871014452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"], \"obsSimpleE\": [\"t\"]}");
		runWithParams = new RunWithParams("{\"varsigma\": [500], \"OBS_PERIOD\": [1], \"seed\": [15286512341452], \"pathfinder\": [\"t\"], \"obsH\": [\"t\"], \"obsSimpleE\": [\"t\"]}");

		System.out.println("SimpleE in care observer: "+runWithParams.simulation.observer.obsSimpleE);
		runWithParams.runSimulation();
		double [][] E_p_i = runWithParams.simulation.observer.getSimpleE();
		for(int p = 0; p<runWithParams.simulation.N;p++) {
			for(int i = 0; i<runWithParams.simulation.varsigma;i++) {
				System.out.println(E_p_i[p][i]);
			}
		}
	}
	
	@Test
	void run_PathFinder_and_RecoverLine() {
		int varsigma = 480;
		String path = "/Users/nicolasbarticevic/Git/testingTrash/recoverPathFinder";
		
		//Clean the directory
//		boolean deleteDirectory(File directoryToBeDeleted) {
//		    File[] allContents = directoryToBeDeleted.listFiles();
//		    if (allContents != null) {
//		        for (File file : allContents) {
//		            deleteDirectory(file);
//		        }
//		    }
//		    return directoryToBeDeleted.delete();
//		}
//		
		
		PathFinder pathfinder = new PathFinder(new String[] {
				"varsigma", String.valueOf(varsigma), "TIMES", "1", "path", path, 
				"dontExit", "true", "alltogether", "true"});
		
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
			String args = "{";
			for(String key : params_and_outputs.keySet()) {
				args = args + "\""+ key +"\":"+ " [" + params_and_outputs.get(key) + "], ";
				//System.out.println(args);
			}
			args = args+"}";
			reproduction = new RunWithParams(args);
			reproduction.runSimulation();
			System.out.println("(Going to get observer)");

			double[][] replicatedHs = reproduction.getSimulation().observer.getH();
			double replicatedAverageH = 0; 
			for(int p = 0;p<replicatedHs.length;p++) {
				replicatedAverageH = replicatedAverageH + replicatedHs[p][1];
			}
			replicatedAverageH = replicatedAverageH/replicatedHs.length;
					
			assertEquals(params_and_outputs.get("H"), String.valueOf(replicatedAverageH));			//TODO try to have care to recover the seed before after starting the simulation for the recover case
		}
		
		
	}
	
	public boolean isCSV(String filename) {
		if (filename == null) {
		    return false;
		}
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex >= 0) {
		    return filename.substring(dotIndex + 1) == "csv";
		}
		return false;

	}
	

}