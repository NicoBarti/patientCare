package runners;
import patientCare.Care;
import java.io.File;

public class runChecpointAndRecover {
	//Care simulation;
	
	public static void main(String[] args) {
		Care simulation = new Care(System.currentTimeMillis());
		simulation.start();
		simulation.varsigma = 1000;
		simulation.startObserver();
		for(int i=0;i<100;i++) {
			simulation.schedule.step(simulation);
		}
		simulation.finish();
		simulation.writeToCheckpoint(new File("checkpointTry1"));
		System.out.println("checkpointed");
		
		Care recover = null;
		
		recover = (Care)recover.readFromCheckpoint(new File("checkpointTry1"));
		System.out.println("Current step: " +recover.schedule.getSteps());
		
		Care copy = simulation;
		System.out.println("Copy time step at begining: "+copy.schedule.getSteps());
		
		for (int i =0; i<800;i++) {
			simulation.schedule.step(simulation);
		}
		
		System.out.println("Copy time step at after intration: "+copy.schedule.getSteps());
		System.out.println("Original time step at after copy's intration: "+simulation.schedule.getSteps());

		double[][] Hcopy = copy.observer.getH();
		double[][] Horig = copy.observer.getH();
		double totalH = 0;
		for(int i=0; i< Hcopy[1].length;i++) {
			totalH += Hcopy[1][i];
		}
		System.out.println("Total copy H:" + totalH);
		
		totalH = 0;
		for(int i=0; i< Horig[1].length;i++) {
			totalH += Horig[1][i];
		}
		System.out.println("Total original H:" + totalH);

	}
	

}
