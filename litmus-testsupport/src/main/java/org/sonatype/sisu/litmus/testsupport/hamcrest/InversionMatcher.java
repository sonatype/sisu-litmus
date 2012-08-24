package org.sonatype.sisu.litmus.testsupport.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * TODO
 *
 * @since 1.0
 */
public class InversionMatcher<T>
    extends BaseMatcher<T>
{

    private final NegativeMatcher<T> matcher;

    public InversionMatcher( final NegativeMatcher<T> matcher )
    {
        this.matcher = matcher;
    }

    @Override
    public boolean matches( final Object arg )
    {
        return !matcher.matches( arg );
    }

    @Override
    public void describeTo( final Description description )
    {
        matcher.describeNegationTo( description );
    }

    @Override
    public void describeMismatch( final Object item, final Description description )
    {
        matcher.describeMismatch( item, description );
    }

    /**
     * Inverts the rule.
     */
    @Factory
    public static <T> InversionMatcher<T> not( NegativeMatcher<T> matcher )
    {
        return new InversionMatcher<T>( matcher );
    }

}
