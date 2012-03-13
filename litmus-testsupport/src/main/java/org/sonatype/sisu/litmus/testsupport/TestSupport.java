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
package org.sonatype.sisu.litmus.testsupport;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.sonatype.gossip.Level;

/**
 * Support for tests.
 *
 * @since 1.0
 */
public class TestSupport
{

    protected final TestUtil util = new TestUtil( this );

    @NonNls
    protected final Logger logger = util.getLog();

    private Level logLevel = Level.INFO;

    @Rule
    public final TestTracer tracer = new TestTracer( this );

    /**
     * Test method utilities.
     */
    @Rule
    public final TestMethod testMethod = new TestMethod( util );

    @Before
    public void initMocks()
    {
        MockitoAnnotations.initMocks( this );
    }

    public Level getLogLevel()
    {
        return logLevel;
    }

    public void setLogLevel( final Level logLevel )
    {
        this.logLevel = checkNotNull( logLevel );
    }

    protected void log( final @NonNls String message )
    {
        logLevel.log( logger, message );
    }

    protected void log( final Object value )
    {
        logLevel.log( logger, String.valueOf( value ) );
    }

    protected void log( final @NonNls String format, final Object... args )
    {
        logLevel.log( logger, format, args );
    }

    protected void log( final @NonNls String message, final Throwable cause )
    {
        logLevel.log( logger, message, cause );
    }

}