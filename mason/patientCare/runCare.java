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
			this.fileName = name;
		} else {
			this.fileName = this.fileName + name;
		}
	}

	private String getFinalPath() {
		return this.path + "/" + this.fileName + ".csv";
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
		String[] pars = { "capacity", "numPatients", "effectiveness","continuity" };
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
					case "effectiveness":
						simulation.setEffectiveness(Double.valueOf(args[i + 1]));
						break;
					case "continuity":
						simulation.setContinuity(Double.valueOf(args[i + 1]));
						break;
					case "patientCentredness":
						simulation.setPatientCentredness(Double.valueOf(args[i + 1]));
						break;
					}
					run.addName(pars[a] + args[i + 1].replaceAll("[^0-9]", "")); // add parameter to name
				}
			}
		}

		simulation.start();
		do
			if (!simulation.schedule.step(simulation))
				break;
		while (simulation.schedule.getSteps() < 53);
		simulation.finish();
		Utils writter = new Utils();
		String response = writter.saveToCSV(simulation.getCareDistribution(), simulation.getMotivationDistribution(), simulation.getSeverityDistribution(), run.getFinalPath());
		System.out.println(response);
		System.exit(0);
	}

}
