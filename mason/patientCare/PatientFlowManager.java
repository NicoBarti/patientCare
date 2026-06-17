package patientCare;

import sim.engine.*;
import sim.util.*;

public class PatientFlowManager implements Steppable {
	private static final long serialVersionUID = 1L;

	private Care care;
	private double add_proportion;
	private double remove_proportion;
	private int flow_period;
	private double dropout_severity;

	public PatientFlowManager(Care care, double add_proportion, double remove_proportion, int flow_period,
			double dropout_severity) {
		this.care = care;
		this.add_proportion = add_proportion;
		this.remove_proportion = remove_proportion;
		this.flow_period = flow_period;
		this.dropout_severity = dropout_severity;
	}

	@Override
	public void step(SimState state) {
		Care care = (Care) state;

		// 1. Severity Dropout Check and Replacement
		if (dropout_severity > 0.0) {
			// Iterate backwards to safely remove patients from the Bag during iteration
			for (int i = care.patients.numObjs - 1; i >= 0; i--) {
				Patient patient = (Patient) care.patients.get(i);
				if (patient.h_p_i_1 > dropout_severity) {
					// Remove the dropped patient from the active patients bag
					// TODO Alteratively, I could mark the patient as removed and have them not
					// scheduled
					// this second approach allows for statistics on these patients
					care.patients.remove(i);

					// Mark them as unobserved in the observer
					if (care.observer != null) {
						care.observer.unobservePatient(patient.p);
					}

					// Expand observer arrays
					if (care.observer != null) {
						care.observer.increaseNmidway(1);
					}

					// Expand provider sum interaction arrays
					if (care.providers != null) {
						for (int pIdx = 0; pIdx < care.providers.numObjs; pIdx++) {
							((Provider) care.providers.get(pIdx)).increaseNmidway(1);
						}
					}

					// Create and initialize a new Patient to replace them
					Patient newPatient = new Patient();
					care.pat_init.initialize(newPatient);
					care.patients.add(newPatient);

					// Schedule the new patient for the current time step
					care.schedule.scheduleOnce(newPatient, care.prioritize.hat_o(newPatient));
				}
			}
		}

		// 2. Periodic Population Flow
		long steps = care.schedule.getSteps();
		if (steps > 0 && flow_period > 0 && steps % flow_period == 0) {
			int currentCount = care.patients.numObjs;
			int toRemove = (int) (currentCount * remove_proportion);
			int toAdd = (int) (currentCount * add_proportion);

			// Remove patients
			if (toRemove > 0) {
				care.patients.shuffle(care.random);
				int actualToRemove = Math.min(toRemove, care.patients.numObjs);
				for (int i = 0; i < actualToRemove; i++) {
					Patient poppedPatient = (Patient) care.patients.pop();
					if (care.observer != null) {
						care.observer.unobservePatient(poppedPatient.p);
					}
				}
			}

			// Add patients
			if (toAdd > 0) {
				for (int i = 0; i < toAdd; i++) {
					// Expand observer arrays
					if (care.observer != null) {
						care.observer.increaseNmidway(1);
					}

					// Expand provider sum interaction arrays
					if (care.providers != null) {
						for (int pIdx = 0; pIdx < care.providers.numObjs; pIdx++) {
							((Provider) care.providers.get(pIdx)).increaseNmidway(1);
						}
					}

					// Create and initialize a new Patient to replace/add
					Patient newPatient = new Patient();
					care.pat_init.initialize(newPatient);
					care.patients.add(newPatient);

					// Schedule the new patient for the current time step
					care.schedule.scheduleOnce(newPatient, care.prioritize.hat_o(newPatient));
				}
			}

			// Update the N count in care to match the active patients bag size
			care.N = care.patients.numObjs;
		}
	}
}
