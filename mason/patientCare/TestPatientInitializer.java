package patientCare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.util.Bag;

/**
 * Dedicated unit tests for {@link PatientInitializer} focusing on random delta generation.
 */
public class TestPatientInitializer {

    @Test
    void testRandomDeltaWithinRange() {
        long seed = 12345L;
        Care sim = new Care(seed);
        sim.prov_init = new ProviderInitializer(sim, "applyFixed");
        // configure PatientInitializer for random delta
        PatientInitializer init = new PatientInitializer(sim, "apply_fixed");
        init.random_delta = true;
        init.random_delta_min = 2.0;
        init.random_delta_max = 5.0;
        // ensure loc/scale not used
        init.loc_scale_delta = false;

        int N = 100;
        Bag patients = new Bag();
        for (int i = 0; i < N; i++) {
            Patient p = new Patient();
            init.initialize(p);
            patients.add(p);
        }
        // verify each patient's delta is within the specified bounds
        for (int i = 0; i < patients.numObjs; i++) {
            Patient p = (Patient) patients.objs[i];
            assertTrue(p.delta_p >= init.random_delta_min && p.delta_p <= init.random_delta_max,
                     "Delta out of range: " + p.delta_p);
        }
    }

    @Test
    void testLocScaleDeltaClampedWithinRange() {
        long seed = 67890L;
        Care sim = new Care(seed);
        sim.prov_init = new ProviderInitializer(sim, "applyFixed");
        PatientInitializer init = new PatientInitializer(sim, "apply_fixed");
        // enable loc/scale delta generation
        init.loc_scale_delta = true;
        init.loc_delta = 3.0;   // mean of the normal distribution
        init.scale_delta = 1.0; // standard deviation
        // set clamping bounds
        init.random_delta_min = 1.0;
        init.random_delta_max = 5.0;
        // ensure uniform random flag is off
        init.random_delta = false;

        int N = 200;
        Bag patients = new Bag();
        for (int i = 0; i < N; i++) {
            Patient p = new Patient();
            init.initialize(p);
            patients.add(p);
        }
        // all generated deltas should be clamped to the [min, max] interval
        for (int i = 0; i < patients.numObjs; i++) {
            Patient p = (Patient) patients.objs[i];
            assertTrue(p.delta_p >= init.random_delta_min && p.delta_p <= init.random_delta_max,
                    "Loc/Scale delta out of bounds: " + p.delta_p);
        }
    }
}
