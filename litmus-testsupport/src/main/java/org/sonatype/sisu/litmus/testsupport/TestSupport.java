/*
 * Sonatype Overlord (TM)
 * Copyright (C) 2011 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/overlord/attributions/.
 * "Sonatype" and "Sonatype Overlord" are trademarks of Sonatype, Inc.
 */

package org.sonatype.sisu.litmus.testsupport;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.sonatype.gossip.Level;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for tests.
 *
 * @since 1.0
 */
public class TestSupport
{
    protected final TestUtil util = new TestUtil(this);

    @NonNls
    protected final Logger logger = util.getLog();

    private Level logLevel = Level.INFO;

    @Rule
    public final MethodRule tracer = new TestTracer(this);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(final Level logLevel) {
        this.logLevel = checkNotNull(logLevel);
    }

    protected void log(final @NonNls String message) {
        logLevel.log(logger, message);
    }

    protected void log(final Object value) {
        logLevel.log(logger, String.valueOf(value));
    }

    protected void log(final @NonNls String format, final Object... args) {
        logLevel.log(logger, format, args);
    }

    protected void log(final @NonNls String message, final Throwable cause) {
        logLevel.log(logger, message, cause);
    }
}