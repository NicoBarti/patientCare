package runners;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import java.io.PrintWriter;

public class RunAllTests {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClass(patientCare.Tests.class),
                selectClass(runners.TestRunWithParams.class),
                selectClass(runners.TestPathFinder.class),
                selectClass(patientCare.TestPatientFlowManager.class)
            )
            .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        
        System.out.println("Running simulation test suites...");
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        PrintWriter writer = new PrintWriter(System.out);
        summary.printTo(writer);
        writer.flush();
        
        if (summary.getFailures().size() > 0) {
            System.out.println("\n--- DETAILED FAILURES ---");
            for (org.junit.platform.launcher.listeners.TestExecutionSummary.Failure failure : summary.getFailures()) {
                System.out.println("Test Failed: " + failure.getTestIdentifier().getUniqueId());
                System.out.println("Reason: " + failure.getException().getMessage());
                failure.getException().printStackTrace(System.out);
                System.out.println("-------------------------");
            }
            System.out.println("SOME TESTS OR CONTAINERS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
            System.exit(0);
        }
    }
}
