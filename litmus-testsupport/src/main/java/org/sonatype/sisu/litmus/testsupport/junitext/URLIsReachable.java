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
package org.sonatype.sisu.litmus.testsupport.junitext;

import com.googlecode.junit.ext.checkers.Checker;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.URLMatchers.respondsWithStatusWithin;

import org.slf4j.LoggerFactory;

/**
 * Checks if a provided URL responds with the specified status code within the given timeout.
 * <p/>
 * URL is always required as the first argument, optionally followed by timeout, http status and on error message suffix, in that order.
 * <p/>
 * Example use:
 *
 * <pre>
 * import com.googlecode.junit.ext.*;
 *
 *{@literal @}RunWith(JunitExtRunner.class)
 * public class URLIsReachableTest {
 *
 *    {@literal @}Test
 *    {@literal @}RunIf(value = URLIsReachable.class, arguments = {"http://www.google.com", "5000", "200", "google is offline"})
 *     public void shouldGet200StatusWithin5SecondsFromGoogle() throws Exception {
 *         // this test method will only be run if http://www.google.com responds with status 200 within 5 seconds
 *     }
 *
 * }
 * </pre>
 *
 * <p/>
 * More information on how to use {@link Checker} can be found at <a href="http://code.google.com/p/junit-ext/">http://code.google.com/p/junit-ext/</a>}
 */
public class URLIsReachable implements Checker {

    /**
     * Default timeout is ten seconds
     */
    public final static int DEFAULT_TIMEOUT = 10 * 1000;

    /**
     * Default HTTP status is 200
     */
    public final static int DEFAULT_STATUS = 200;

    /**
     * Default additional message to print on failure is empty string ""
     */
    public final static String DEFAULT_MESSAGE_SUFFIX = null;

    private URL url; // arg 0
    private int timeout = DEFAULT_TIMEOUT; // arg 1
    private int statusCode = DEFAULT_STATUS; // arg 2
    private String message = DEFAULT_MESSAGE_SUFFIX; // arg 3, ideally the test name

    /**
     * Create an instance with the required String that can be parsed as a URL.
     * @param urlString a valid URL string
     * @throws IllegalArgumentException  if urlString cannot be parsed into a valid {@link URL}
     */
    public URLIsReachable(final String urlString) throws MalformedURLException {
        final String[] args = new String[]{urlString};
        init(args);
    }

    /**
     * Create an instance using a list of arguments.
     * @param args checker args array with length of 1 to 4
     * @throws IllegalArgumentException if first arg is a malformed {@link URL}, the number of arguments is less than one or greater than 4, or arguments 2 and three cannot be parsed as integer
     */
    public URLIsReachable(final String[] args) throws MalformedURLException {
        init(args);
    }

    private void init(String[] args) throws MalformedURLException {
        if(args == null || args.length > 4 || args.length < 1){
            throw new IllegalArgumentException("args must have length 1 to 4");
        }

        this.url = new URL(args[0]); // required

        if (args.length >= 2) {
            timeout = Integer.parseInt(args[1]);
            if(timeout < 0){
                throw new IllegalArgumentException( "timeout cannot be less than zero" );
            }
        }
        if (args.length >= 3) {
            statusCode = Integer.parseInt(args[2]);
            if(statusCode < 0){
                throw new IllegalArgumentException( "http status code cannot be less than zero" );
            }
        }
        if (args.length == 4) {
            this.message = args[3];
        }
    }

    /**
     *  This checker is satisfied if the provided URL responds with the specified status code within the given timeout.
     *
     * @return true if the provided URL responds with the specified status code within the given timeout
     */
    @Override
    public boolean satisfy() {
        try {
            assertThat(this.url, respondsWithStatusWithin(this.statusCode, this.timeout));
        } catch (AssertionError ae) {
            LoggerFactory.getLogger( this.getClass() ).warn( "{} caused test to be ignored {}: {}", new Object[] { this.getClass().getSimpleName(), this.message != null ? "(" + this.message + ")" : "", ae.getMessage()} );
            return false;
        }
        return true;
    }

}
