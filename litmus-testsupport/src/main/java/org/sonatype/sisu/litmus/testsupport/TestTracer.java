/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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