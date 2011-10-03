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
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
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
    extends TestWatcher
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

    private String prefix(final Description description) {
        return format("TEST %s", description.getMethodName()); //NON-NLS
    }

    @Override
    protected void failed(Throwable e, Description description) {
        if(e instanceof MultipleFailureException){
            MultipleFailureException mfe = (MultipleFailureException) e;
            level.log(logger, "{} FAILED {} {}", prefix(description), e, mfe.getFailures());
        } else {
            level.log(logger, "{} FAILED", prefix(description), e);
        }
    }

    @Override
    protected void finished(Description description) {
        level.log(logger, "{} FINISHED", prefix(description));
    }

    @Override
    protected void starting(Description description) {
        level.log(logger, "{} STARTING", prefix(description));
    }

    @Override
    protected void succeeded(Description description) {
        level.log(logger, "{} STARTING", prefix(description));
    }

}