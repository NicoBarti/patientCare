package patientCare;

public class runCare {
	private String path;
	private String fileName;
	static private String pth;

	public runCare(String path) {
		this.path = path;
		this.fileName = "";
	}

	private void addName(String name) {
		if (this.fileName.equals("")) {
			this.fileName = name + "_";
		} else {
			this.fileName = this.fileName + name + "_";
		}
	}

	private String getFinalPath() {
		return this.path + "/" + this.fileName.substring(0, this.fileName.length() - 1) + ".csv";
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("path missing");
			return;
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("path")) {
				try {
					pth = args[i + 1];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Error path not specified");
					return;
				}
			}
		}

		runCare run = new runCare(pth);

		Care simulation = new Care(System.currentTimeMillis());

		// pass parameters to simulation
		// create list of parameters
		String[] pars = { "capacity", "numPatients", "E", "RCpos", "RCneg", 
				"Gd", "RLow", "RUp", "CLow", "CUp" };
		for (int a = 0; a < pars.length; a++) {
			for (int i = 0; i < args.length; i++) { // loops through args to find parameters
				if (args[i].equals(pars[a])) {
					try {
						double tr = Double.valueOf(args[i + 1]); // handles exeption of invalid parameter
					} catch (NumberFormatException e) {
						System.out.println("Invalid parameter " + pars[a]);
						return;
					}
					switch (pars[a]) { // adds the parameters to simulation
					case "capacity":
						simulation.setCapacity(Integer.valueOf(args[i + 1]));
						break;
					case "numPatients":
						simulation.setNumPatients(Integer.valueOf(args[i + 1]));
						break;
					case "E":
						simulation.setE(Double.valueOf(args[i + 1]));
						break;
					case "RCpos":
						simulation.setRCpos(Double.valueOf(args[i + 1]));
						break;
					case "RCneg":
						simulation.setRCneg(Double.valueOf(args[i + 1]));
						break;
					case "Gd":
						simulation.setGd(Integer.valueOf(args[i + 1]));
						break;
					case "RLow":
						simulation.setRLow(Double.valueOf(args[i + 1]));
						break;
					case "RUp":
						simulation.setRUp(Double.valueOf(args[i + 1]));
						break;
					case "CLow":
						simulation.setCLow(Integer.valueOf(args[i + 1]));
						break;
					case "CUp":
						simulation.setCUp(Integer.valueOf(args[i + 1]));
						break;
					}
					run.addName(pars[a] + "_" + args[i + 1].replaceAll("[^0-9]", "")); // add parameter to name
				}
			}
		}

		simulation.start();
		//System.out.println("iniciando");
		do			{
		//	System.out.println(simulation.schedule.getSteps());
			if (!simulation.schedule.step(simulation)) {
				System.out.println("algo falso en schedule.step");
				break;}
		}
		while (simulation.schedule.getSteps() < 53);
		simulation.finish();
		Utils writter = new Utils();
		writter.buildHeader();
		String response = writter.saveToCSV(
				simulation.getCareDistribution(), //serviceDist
				simulation.getCapacityDistribution(), //capacityDist
				simulation.getProbabilityDistribution(), // probabilityDist
				simulation.getExpectationDistribution(), //expectationDist
				simulation.getSatisfactionDistribution(), //satisfactionDist
				run.getFinalPath());
		System.out.println(response);
		System.exit(0);
	}

}
