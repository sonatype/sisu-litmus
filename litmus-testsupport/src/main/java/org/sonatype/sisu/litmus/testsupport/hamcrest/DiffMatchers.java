/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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
 * Some ideas to represent test assertions in more usable way
 * <p/>
 * Inspired and blatantly copied from:
 * http://stackoverflow.com/questions/319479/generate-formatted-diff-output-in-java
 * <p/>
 * Thank you!
 *
 * @author cstamas
 * @since 1.0
 */
public class DiffMatchers {

    private static class StringDiffMatcher
            extends BaseMatcher<String> {

        private final String string;

        private boolean onlyDiffs;

        public StringDiffMatcher(final String string) {
            this(string, false);
        }

        public StringDiffMatcher(final String string, final boolean onlyDiffs) {
            this.string = string;
            this.onlyDiffs = onlyDiffs;
        }

        @Override
        public boolean matches(Object arg) {
            if (arg == null) {
                return string == null;
            } else {
                return arg.equals(string);
            }
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("to be equal");
        }

        @SuppressWarnings("unchecked")
        @Override
        final public void describeMismatch(Object item, Description description) {
            if (item == null) {
                super.describeMismatch(item, description);
            } else if (!String.class.isInstance(item)) {
                description.appendText("was a ")
                        .appendText(item.getClass().getName())
                        .appendText(" (")
                        .appendValue(item)
                        .appendText(")");
            } else {
                describeMismatchSafely((String) item, description);
            }
        }

        protected void describeMismatchSafely(String item, Description description) {
            description.appendText("they differ as follows (expected on the left, actual on the right) [E] only in expected, [A] only in actual, [D] difference between expected and actual:\n");
            final String diff = DiffUtils.diffSideBySide(string, item, onlyDiffs);
            description.appendText(diff);
        }

    }

    @Factory
    public static Matcher<String> equalTo(final String string) {
        return new StringDiffMatcher(string);
    }

    @Factory
    public static Matcher<String> equalToOnlyDiffs(final String string) {
        return new StringDiffMatcher(string, true);
    }

}
