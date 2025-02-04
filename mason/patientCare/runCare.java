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
		String[] pars = { "capacity", "numPatients", "k","weeks", "CONTINUITY@ALLOCATION", "SEVERITY@ALLOCATION", "DISEASE_VELOCITY", "LEARNING_RATE" };
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
					case "k":
						simulation.setk(Double.valueOf(args[i + 1]));
						break;
					case "weeks":
						simulation.setweeks(Integer.valueOf(args[i + 1]));
						break;
					case "CONTINUITY@ALLOCATION":
						simulation.setCONTINUITY_ALLOCATION(Double.valueOf(args[i +1]));
						break;
					case "SEVERITY@ALLOCATION":
						simulation.setSEVERITY_ALLOCATION(Double.valueOf(args[i+1]));
						break;
					case "DISEASE_VELOCITY":
						simulation.setDISEASE_VELOCITY(Double.valueOf(args[i+1]));
						break;
					case "LEARNING_RATE":
						simulation.setLEARNING_RATE(Double.valueOf(args[i+1]));
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
		while (simulation.schedule.getSteps() < simulation.getweeks());
		simulation.finish();
		
		Utils writter = new Utils();
		writter.buildHeader();
		String response = writter.saveToCSV(simulation.getweeks(), 
				//simple variables
				simulation.getds(),
				//simulation.getCONTINUITY_ALLOCATION(),simulation.getSEVERITY_ALLOCATION(),
				//double array variables
				simulation.getCs(),  simulation.getHs(),
				simulation.getexpectations(), simulation.getTs(), simulation.getBs(), run.getFinalPath());
		System.out.println(response);
		System.exit(0);
	}

}
