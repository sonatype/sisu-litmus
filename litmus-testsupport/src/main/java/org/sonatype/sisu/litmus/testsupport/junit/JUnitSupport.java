/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
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
                logger.error("Test Failure: {}", failure.getException());
            }
            Assert.fail("There were test failures. See logging output.");
        }
    }
}
