/*
 * Sonatype Overlord (TM)
 * Copyright (C) 2011 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/overlord/attributions/.
 * "Sonatype" and "Sonatype Overlord" are trademarks of Sonatype, Inc.
 */

package org.sonatype.sisu.litmus.testsupport;

import org.jetbrains.annotations.NonNls;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gossip.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Traces test execution to a {@link Logger}.
 *
 * @since 1.0
 */
public class TestTracer
    extends TestWatchman
{
    @NonNls
    private final Logger logger;

    @NonNls
    private Level level = Level.INFO;

    public TestTracer(final Logger logger) {
        this.logger = checkNotNull(logger);
    }

    public TestTracer(final Object owner) {
        this(LoggerFactory.getLogger(owner.getClass()));
    }

    public TestTracer withLevel(final Level level) {
        this.level = checkNotNull(level);
        return this;
    }

    private String prefix(final FrameworkMethod method) {
        return format("TEST %s", method.getName()); //NON-NLS
    }

    @Override
    public void starting(final FrameworkMethod method) {
        level.log(logger, "{} STARTING", prefix(method));
    }

    @Override
    public void succeeded(final FrameworkMethod method) {
        level.log(logger, "{} SUCCEEDED", prefix(method));
    }

    @Override
    public void failed(final Throwable e, final FrameworkMethod method) {
        level.log(logger, "{} FAILED", prefix(method), e);
    }

    @Override
    public void finished(final FrameworkMethod method) {
        level.log(logger, "{} FINISHED", prefix(method));
    }
}