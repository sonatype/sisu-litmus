/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.litmus.testsupport.junitext;

import java.net.MalformedURLException;
import java.util.List;

import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.sun.net.httpserver.HttpServer;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.sonatype.sisu.litmus.testsupport.junit.JUnitSupport;

/**
 * Verifies {@link URLIsReachable} works with {@link com.googlecode.junit.ext.JunitExtRunner} by invoking {@link URLIsReachableTestDelegate}.
 * <p>
 * Also performs basic argument validation to checker as well.
 *
 * @since 1.3
 */
public class URLIsReachableTest {

    @Test
    public void runDelegateTest(){
        final Result result = JUnitCore.runClasses( URLIsReachableTestDelegate.class );
        if(!result.wasSuccessful()){
            new JUnitSupport().logFailures( result.getFailures() );
            assertThat(result.getFailureCount(), Matchers.equalTo( 0 ));
        }
    }

    @Test(expected = MalformedURLException.class)
    public void urlConstructorMalformed()
        throws Exception
    {
        new URLIsReachable( "##$&(" );
    }

    @Test(expected = MalformedURLException.class)
    public void urlConstructorNull()
        throws Exception
    {
        new URLIsReachable( (String)null );
    }

    @Test
    public void arrayConstructorOneValid()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar"} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorTwoNull()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", null} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorTwoNotANumber()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", "notanumber"} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorTwoNegative()
        throws Exception
    {
        // could be common typo
        new URLIsReachable( new String[]{"http://fubar", "-100"} );
    }

    @Test
    public void arrayConstructorTwoValid()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", "100"} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorThreeNull()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", "100", null} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorThreeNotANumber()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", "100", "notanumber"} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorThreeNegative()
        throws Exception
    {
        // could be common typo
        new URLIsReachable( new String[]{"http://fubar", "100", "-1000"} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorEmpty()
        throws Exception
    {
        new URLIsReachable( new String[]{} );
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorTooLarge()
        throws Exception
    {
        new URLIsReachable( new String[]{"http://fubar", "100", "200", "message", "toomanyargs"} );
    }

}
