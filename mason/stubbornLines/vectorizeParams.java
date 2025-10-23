package stubbornLines;

import java.util.HashMap;

public class vectorizeParams {
	
    int norm_N = 5000;
    int norm_W = 50;
    double norm_fixed_delta = 10.0;
    double norm_fixed_capN = 10;
    double norm_fixed_lambda = 10;
    double norm_fixed_tau = 10;
    double norm_fixed_rho = 10;
    double norm_fixed_eta = 10;
    double norm_fixed_kappa = 1;
    double norm_fixed_capE = 10;
    double norm_fixed_psi = 1;
    int norm_totalCapacity = 5000;
	
	public vectorizeParams() {}
	

	
	public double[] toScaledOrderedArray(HashMap<String,String> hash) {
		double[] result = new double[12];
		for(String param: hash.keySet()) {
			if(param == "seed" | param == "varsigma" | param == "OBS_PERIOD"
					| param == "PATIENT_INIT" | param == "Pi" |
					param == "PROVIDER_INIT") {continue;}
			if(position(param)<0) {System.out.print(param + " no encontrado");}
			if(normalize(param, hash.get(param))<0) {System.out.print(param + "no encontrado");}

			result[position(param)] = normalize(param, hash.get(param));
		}
		return result;
	}
	
	public double[] toRawOrderedArray(HashMap<String,String> hash) {
		double[] result = new double[12];
		for(String param: hash.keySet()) {
			if(param == "seed" | param == "varsigma" | param == "OBS_PERIOD"
					| param == "PATIENT_INIT" | param == "Pi" |
					param == "PROVIDER_INIT") {continue;}
			if(position(param)<0) {System.out.print(param + " no encontrado");}

			result[position(param)] = Double.valueOf(hash.get(param));
		}
		return result;
	}

	private double normalize(String param, String value) {		
		double response = -1;
		switch (param){
			case "fixed_lambda":
				response = (Double.valueOf(value)/norm_fixed_lambda);
				break;
			case "fixed_tau":
				response = (Double.valueOf(value)/norm_fixed_tau);
				break;
			case "fixed_kappa":
				response = (Double.valueOf(value)/norm_fixed_kappa);
				break;
			case "fixed_rho":
				response = (Double.valueOf(value)/norm_fixed_rho);
				break;
			case "fixed_eta":
				response = (Double.valueOf(value)/norm_fixed_eta);
				break;
			case "fixed_capN":
				response = (Double.valueOf(value)/norm_fixed_capN);
				break;
			case "fixed_capE":
				response = (Double.valueOf(value)/norm_fixed_capE);
				break;
			case "fixed_psi":
				response = (Double.valueOf(value)/norm_fixed_psi);
				break;
			case "N":
				response = (Double.valueOf(value)/norm_N);
				break;
			case "W":
				response = (Double.valueOf(value)/norm_W);
				break;
			case "fixed_delta":
				response = (Double.valueOf(value)/norm_fixed_delta);
				break;
			case "totalCapacity":
				response = (Double.valueOf(value)/norm_totalCapacity);
				break;
		}
	return response;		
	}
	
	private int position(String param) {		
		int response = -1;
		switch (param){
			case "fixed_lambda":
				response = 0;
				break;
			case "fixed_tau":
				response = 1;
				break;
			case "fixed_kappa":
				response = 2;
				break;
			case "fixed_rho":
				response = 3;
				break;
			case "fixed_eta":
				response = 4;
				break;
			case "fixed_capN":
				response = 5;
				break;
			case "fixed_capE":
				response = 6;
				break;
			case "fixed_psi":
				response = 7;
				break;
			case "N":
				response = 8;
				break;
			case "W":
				response = 9;
				break;
			case "fixed_delta":
				response = 10;
				break;
			case "totalCapacity":
				response = 11;
				break;
		}
	return response;		
	}
	
}
