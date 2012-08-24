package org.sonatype.sisu.litmus.testsupport.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * TODO
 *
 * @since 1.0
 */
public interface NegativeMatcher<T>
    extends Matcher<T>
{

    void describeNegationTo( final Description description );

}
