package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class debugTesting {
	RunWithParams runWithParams;
	Care simulation;
	Set<String> files;

	@Test
	void run_PathFinder_and_RecoverLineFromCSV() {
		// if this test takes long, the path has too many .csv files
		int varsigma = 480;
		//String path = "/Users/nicolasbarticevic/Git/testingTrash/recoverPathFinder";
		//String path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/pathFinderOutputs/care6_consistentSeed/500_fixCap200N5000Delta3";
		String path = "/Users/nicolasbarticevic/Desktop/simulationOutputs/pathFinderOutputs/care6_consistentSeed/debug";
		RunWithParams reproduction;
		csvReader reader = new csvReader(path+"/H_all/");
		HashMap<String,String> params = reader.readPathFinderOutput("varsigma_1_ex1_runID1760109818597.csv");
		//String params = "{ \"seed\": [1760013435014], \"fixed_kappa\": [0.9576125], \"fixed_delta\":[3.0], \"obsSimpleB\": [false], \"obsSimpleC\": [false], \"varsigma\": [500], \"obsSimpleE\": [false], \"N\": [5000], \"fixed_lambda\": [8.50411660147144], \"fixed_tau\": [3.6615757199395778], \"fixed_eta\": [2.225585511663347], \"totalCapacity\": [200], \"W\": [47.0], \"PATIENT_INIT\": [\"applyFixed\"], \"Pi\": [basal], \"PROVIDER_INIT\": [\"applyFixed\"], \"fixed_capE\": [3.2762107976170176], \"fixed_capN\": [8.237656000063648], \"obsT\": [false], \"fixed_psi\": [0.915296333501237], \"obsC\": [false], \"obsB\": [false], \"reproduce_line\": [\"true\"], \"OBS_PERIOD\": [100], \"obsE\": [false], \"obsH\": [true], \"fixed_rho\": [3.1520996615960217], \"obsN\": [false] }";
		params.put("reproduce_line", "true");
		params.put("obsH", "true");
		params.put("OBS_PERIOD", "100");
		params.put("fixed_eta", "1.993876591234712");
		params.put("fixed_psi", "0.0140045400220996");

		reproduction = new RunWithParams(convertToParamString(params));
		reproduction.runSimulation();
		//System.out.println("(Going to get observer)");

		double[][] replicatedHs = reproduction.getSimulation().observer.getH();
		double replicatedAverageH = 0; 
		for(int p = 0;p<replicatedHs.length;p++) {
			replicatedAverageH = replicatedAverageH + replicatedHs[p][5]/replicatedHs.length;
		}
		//replicatedAverageH = replicatedAverageH/replicatedHs.length;
		System.out.println("reprudicint FROM LITERAL STRING H = "+ replicatedAverageH);
		HashMap<String,String> recoveredParams = reproduction.getSimulation().getParams();
		for(String value : recoveredParams.keySet()) {
			System.out.println(value + " " + recoveredParams.get(value));
		}
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
