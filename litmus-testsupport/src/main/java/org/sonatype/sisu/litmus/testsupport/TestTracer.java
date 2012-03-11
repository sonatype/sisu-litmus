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
    extends TestWatcher
{
    private static final String UNKNOWN_METHOD_NAME = "UNKNOWN METHOD NAME";

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

    private String prefix(final Description desc) {
        return format("TEST %s", desc == null ? UNKNOWN_METHOD_NAME : desc.getMethodName());
    }


    @Override
    public void starting(final Description desc) {
        level.log(logger, "{} STARTING", prefix(desc));
    }

    @Override
    public void succeeded(final Description desc) {
        level.log(logger, "{} SUCCEEDED", prefix(desc));
    }

    @Override
    public void failed(final Throwable e, final Description desc) {
        if(e instanceof MultipleFailureException){
            MultipleFailureException mfe = (MultipleFailureException) e;
            level.log(logger, "{} FAILED {} {}", prefix(desc), e, mfe.getFailures());
        } else {
            level.log(logger, "{} FAILED", prefix(desc), e);
        }
    }

    @Override
    public void finished(final Description desc) {
        level.log(logger, "{} FINISHED", prefix(desc));
    }
}