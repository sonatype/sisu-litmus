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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;

/**
 * Meant to be launched by {@link URLIsReachableTest}, to verify URLIsReachable actually works.
 * @since 1.3
 */
@RunWith(JunitExtRunner.class)
public class URLIsReachableTestDelegate
{

    @Test
    @RunIf(value = URLIsReachable.class, arguments = {"http://XXXfubarXXX:48972"})
    public void shouldFailUsingOneArgs() throws Exception {
        shouldNeverExecute();
    }

    @Test
    @RunIf(value = URLIsReachable.class, arguments = {"http://XXXfubarXXX:48972", "1000"})
    public void shouldFailUsingTwoArgs() throws Exception {
        shouldNeverExecute();
    }

    @Test
    @RunIf(value = URLIsReachable.class, arguments = {"http://XXXfubarXXX:48972", "1000", "200"})
    public void shouldFailUsingThreeArgs() throws Exception {
        shouldNeverExecute();
    }

    @Test
    @RunIf(value = URLIsReachable.class, arguments = {"http://XXXfubarXXX:48972", "1000", "200", "fubar is offline"})
    public void shouldFailUsingFourArgs() throws Exception {
        shouldNeverExecute();
    }

    private void shouldNeverExecute(){
        Assert.fail( "URLIsReachable should have prevented this test from running." );
    }
}
