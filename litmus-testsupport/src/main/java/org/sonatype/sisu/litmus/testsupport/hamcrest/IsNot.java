package org.sonatype.sisu.litmus.testsupport.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Replacement for {@link org.hamcrest.core.IsNot} that delegates mismatch description to wrapped Matcher if set.
 *
 * @since 1.0
 */
public class IsNot<T> extends BaseMatcher<T> {

        private final Matcher<T> matcher;

        public IsNot(Matcher<T> matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean matches(Object arg) {
            return !matcher.matches(arg);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("not ").appendDescriptionOf(matcher);
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            // BUG FIX: get matcher mismatch description in case item does not provide a suitable mismatch description
            if (this.matcher != null) {
                matcher.describeMismatch(item, description);
            } else {
                super.describeMismatch(item, description);
            }
        }

        /**
         * Inverts the rule.
         */
        @Factory
        public static <T> Matcher<T> not(Matcher<T> matcher) {
            return new IsNot(matcher);
        }

        /**
         * This is a shortcut to the frequently used not(equalTo(x)).
         *
         * For example:  assertThat(cheese, is(not(equalTo(smelly))))
         *          vs.  assertThat(cheese, is(not(smelly)))
         */
        @Factory
        public static <T> Matcher<T> not(T value) {
            return not(org.hamcrest.Matchers.equalTo(value));
        }
    }
