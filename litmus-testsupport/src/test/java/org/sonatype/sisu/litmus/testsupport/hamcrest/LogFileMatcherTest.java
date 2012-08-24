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
package org.sonatype.sisu.litmus.testsupport.hamcrest;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.junit.TestInfoRule;

/**
 * {@link org.sonatype.nexus.testsuite.support.hamcrest.NexusMatchers} UTs.
 *
 * @since 2.2
 */
public class LogFileMatcherTest
    extends TestSupport
{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestInfoRule testInfo = new TestInfoRule();

    @Test
    public void inexistentLogFile()
        throws Exception
    {
        thrown.expect( AssertionError.class );
        thrown.expectMessage( "java.io.FileNotFoundException: File 'foo.log' does not exist" );
        assertThat(
            new File( "foo.log" ),
            LogFileMatcher.hasExceptionOfType( NullPointerException.class )
        );
    }

    @Test
    public void logFileHasNPE()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasExceptionOfType( NullPointerException.class )
        );
    }

    @Test
    public void logFileHasNPEAndCNF()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            Matchers.allOf(
                LogFileMatcher.hasExceptionOfType( NullPointerException.class ),
                LogFileMatcher.hasExceptionOfType( ClassNotFoundException.class )
            )
        );
    }

    @Test
    public void logFileHasNoNPE()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            InversionMatcher.not( LogFileMatcher.hasExceptionOfType( NullPointerException.class ) )
        );
    }

    @Test
    public void logFileHasText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasText( "foo" )
        );
    }

    @Test
    public void logFileDoesNotHaveText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            InversionMatcher.not( LogFileMatcher.hasText( "foo" ) )
        );
    }

    @Test
    public void logFileHasMatchingText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasText( Pattern.compile( ".*foo-ing.*" ) )
        );
    }

    @Test
    public void logFileDoesNotMatchText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            InversionMatcher.not( LogFileMatcher.hasText( Pattern.compile( ".*foo-ing.*" ) ) )
        );
    }

    private File resolveLogFile()
    {
        return util.resolveFile( String.format(
            "src/test/uncopied-resources/%s/%s.log", testInfo.getTestClass().getSimpleName(), testInfo.getMethodName()
        ) );
    }

}
