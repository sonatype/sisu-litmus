package org.sonatype.sisu.litmus.testsupport.mock;

import java.util.Arrays;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Setup a test using Mockito annotations, and validate the mockito usage after the test completes.
 * <p>
 * {@link Mockito#validateMockitoUsage() } is called even if the passed statement triggers a {@link Throwable}. If validation
 * fails then, both {@link Throwable}s are wrapped in a {@link MultipleFailureException}
 *
 * @since 1.0
 */
public class MockitoRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
       return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(description.getTestClass());
                Throwable throwable = null;
                try {
                    base.evaluate();
                } catch (final Throwable t) {
                    throwable = t;
                    throw t;
                } finally {
                    try {
                        Mockito.validateMockitoUsage();
                    } catch (final Throwable t) {
                        if (throwable != null) {
                            throw new MultipleFailureException(Arrays.asList(new Throwable[]{throwable,t}));
                        } else {
                            throw t;
                        }
                    }
                }
            }
        };
    }
}