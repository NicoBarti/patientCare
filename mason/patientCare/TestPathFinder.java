package patientCare;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;


public class TestPathFinder {
	
	String testingPath = "/Users/nicolasbarticevic/Git/testingTrash";
	Care care;

	@Test
	void initialize_storage_internals_given_args() {
		PathFinder pathfinder = new PathFinder(
				new String[] {"varsigma", "10", "path", testingPath, "TIMES", "16",
						"testing", "true"});
		assertEquals(pathfinder.varsigma, 10, "max_varsigma not initialized ok");
		assertEquals(pathfinder.path, testingPath, "path not initialized ok");
		assertEquals(pathfinder.TIMES, 16, "REPS not initialized ok");
		assertEquals(pathfinder.storage_H.length, 16, "storage_H initialization not ok");
		assertEquals(pathfinder.storage_SEED.length, 16, "storage_H initialization not ok");
	}
	
	@Test
	void right_number_of_repetitions() {
		int REPS = 128;
		PathFinder pathfinder = new PathFinder(
				new String[] {"varsigma", "10", "path", testingPath, "TIMES", Integer.toString(REPS),
						"testing", "true"});
		assertEquals(REPS,pathfinder.TIMES, "REPS not initialized ok");
		pathfinder.runRepetitions();
		assertEquals(REPS, pathfinder.storedRepetitions );
	}
	
	@Test
	void check_local_storage() {
		int REPS = 1;
		PathFinder pathfinder = new PathFinder(
				new String[] {"varsigma", "10", "path", testingPath, "TIMES", Integer.toString(REPS),
						"testing", "true"});
		pathfinder.runRepetitions();
		double[][] H = pathfinder.care1.observer.getH();
		int Repline = 1;
		double average = 0;
		int arraywidth = H[0].length;
		int ps = H.length;
		//System.out.println("n patients "+ps);
		//System.out.println("H[0].length "+arraywidth);
		for (int p=0;p<ps;p++) {
			average+=H[p][arraywidth-1];
		}
		average=average/H.length;
		assertEquals(average, pathfinder.storage_H[0]);

	}
	
	@Test
	void check_seed_recover() {
		PathFinder pathfinder = new PathFinder(
				new String[] {"varsigma", "500", "path", testingPath, 
						"TIMES", "8","testing", "true"});
		long seed = 1752852775281L;
		Care care = new Care(seed);
		pathfinder.configureCare(care);
		care.start();
		care.startObserver(true, false, false, false, false, false, false, false, false);
		for(int i=0;i<500;i++) {
			care.schedule.step(care);
			
		}
		care.finish();
		//pathfinder.localStorage(care, seed);
		double average = 0;
		double[][] H = care.observer.getH();
			for(int p=0; p<H.length;p++) {
				average += H[p][H[0].length-1];
			}
		System.out.println("H[0].length "+H[0].length);
		System.out.println("H.length "+H.length);

		System.out.println("average H "+average/H.length);
		
	}
	

}
