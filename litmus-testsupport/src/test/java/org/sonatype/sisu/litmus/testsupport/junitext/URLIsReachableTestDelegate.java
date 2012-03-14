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
