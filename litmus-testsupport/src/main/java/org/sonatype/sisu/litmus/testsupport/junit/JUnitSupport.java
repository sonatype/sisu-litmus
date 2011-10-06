package org.sonatype.sisu.litmus.testsupport.junit;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;

/**
 * Generic JUnit support utils.
 *
 * @since 1.0
 */
public class JUnitSupport {

    @Inject
    private Logger logger;

    /**
     * Run one or more JUnit tests sequentially in the order given.
     *
     *
     * @param testClasses the test classes to run
     *
     * @throws NullPointerException if testClasses is null
     * @throws IllegalArgumentException if testClasses are not provided
     * @throws AssertionFailedError if one or more test classes fail
     */
    public void runUnitTests(Class... testClasses) {
        if (checkNotNull(testClasses).length == 0) {
            throw new IllegalArgumentException("Please specify one or more JUnit test class.");
        }

        Result result = JUnitCore.runClasses(testClasses);
        List<Failure> failures = result.getFailures();
        if (!result.wasSuccessful()) {
            for (Failure failure : failures) {
                logger.error(" {} failed: {}", failure.getTestHeader(), failure.getException());
            }
            Assert.fail("There were test failures. See logging output.");
        }
    }
}
