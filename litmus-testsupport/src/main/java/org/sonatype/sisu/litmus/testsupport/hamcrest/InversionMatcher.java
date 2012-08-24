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
