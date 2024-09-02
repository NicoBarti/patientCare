package patientCare;

import sim.engine.*;
import sim.portrayal.Inspector;
import sim.display.*;

public class CareWithUI extends GUIState {

	public static void main(String[] args) {
		CareWithUI vid = new CareWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
		c.setWhenShouldPause(52);
		// .setWhenShouldEndTime(53);
	}

	public CareWithUI() {
		super(new Care(System.currentTimeMillis()));
	}

	public CareWithUI(SimState state) {
		super(state);
	}

	public static String getName() {
		return "Patient - doctor interactions";
	}

	private Care care = (Care) state;

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {
		Inspector i = super.getInspector();
		// i.setVolatile(true);
		return i;
	}

	public void start() {
		care.setweeks(52);
		super.start();
	}

	public void init(Controller c) {
		super.init(c);
	}

	public void quit() {

	}

	public void finish() {
		super.quit();
		care.patients.clear();
		System.out.println("Stopping simmulation");
	}

}
