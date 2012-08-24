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
 * {@link LogFileMatcher} UTs.
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

    /**
     * Verify that log file to be matched does not exist an {@link AssertionError} is thrown with a proper message.
     */
    @Test
    public void inexistentLogFile()
    {
        thrown.expect( AssertionError.class );
        thrown.expectMessage( "java.io.FileNotFoundException: File 'foo.log' does not exist" );
        assertThat(
            new File( "foo.log" ),
            LogFileMatcher.hasExceptionOfType( NullPointerException.class )
        );
    }

    /**
     * Verify that a log file that contains an NullPointerException matches.
     */
    @Test
    public void logFileHasNPE()
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasExceptionOfType( NullPointerException.class )
        );
    }

    /**
     * Verify that a log file that contains an NullPointerException  and ClassNotFoundException matches.
     */
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

    /**
     * Verifies that a log file that does not have NullPointerException but has ClassNotFoundException matches.
     */
    @Test
    public void logFileHasNoNPE()
    {
        assertThat(
            resolveLogFile(),
            InversionMatcher.not( LogFileMatcher.hasExceptionOfType( NullPointerException.class ) )
        );
    }

    /**
     * Verifies that a log file that a text "foo" matches.
     */
    @Test
    public void logFileHasText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasText( "foo" )
        );
    }

    /**
     * Verifies that a log file that does not have a text "foo" matches.
     */
    @Test
    public void logFileDoesNotHaveText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            InversionMatcher.not( LogFileMatcher.hasText( "foo" ) )
        );
    }

    /**
     * Verifies that a log file that has a text "foo-ing" matches pattern "".*foo-ing.*".
     */
    @Test
    public void logFileHasMatchingText()
        throws Exception
    {
        assertThat(
            resolveLogFile(),
            LogFileMatcher.hasText( Pattern.compile( ".*foo-ing.*" ) )
        );
    }

    /**
     * Verifies that a log file that does not have a text "foo-ing" is not matching pattern "".*foo-ing.*".
     */
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
