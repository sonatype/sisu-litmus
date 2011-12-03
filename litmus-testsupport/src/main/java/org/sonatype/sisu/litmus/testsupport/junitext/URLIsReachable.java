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

import static org.sonatype.sisu.litmus.testsupport.hamcrest.URLMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.net.MalformedURLException;
import java.net.URL;

import com.googlecode.junit.ext.checkers.Checker;

public class URLIsReachable implements Checker {

    private URL url; // arg 0
    private int timeout = 10 * 1000; // arg 1
    private int statusCode = 200; // arg 2
    private String message = ""; // arg 3, ideally the test name

    public URLIsReachable(final String urlString) throws MalformedURLException {
        final String[] args = new String[] { urlString };
        init(args);
    }

    public URLIsReachable(final String[] args) throws MalformedURLException {
        init(args);
    }

    private void init(String[] args) throws MalformedURLException {
        this.url = new URL(args[0]); // required
        if (args.length >= 2) {
            timeout = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            statusCode = Integer.parseInt(args[2]);
        }
        if (args.length == 4) {
            this.message = args[3];
        }
    }

    @Override
    public boolean satisfy() {
        try {
            assertThat(this.url, respondsWithStatusWithin(this.statusCode, this.timeout));
        } catch (AssertionError ae) {
            System.err.println("[WARN] "
                    + String.format("%s caused a test to be ignored %s, %s", this.getClass().getSimpleName(),
                            this.message, ae.getMessage()));
            return false;
        }
        return true;
    }

}
