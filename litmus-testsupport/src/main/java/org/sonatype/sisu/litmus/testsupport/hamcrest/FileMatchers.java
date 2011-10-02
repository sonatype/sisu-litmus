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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Some ideas copied freely from
 * http://www.time4tea.net/wiki/display/MAIN/Testing+Files+with+Hamcrest
 * <p>
 * Converted to pure Hamcrest
 *
 * @author time4tea technology ltd 2007
 * @author plynch
 */
public class FileMatchers {

    @Factory
    public static Matcher<File> isDirectory() {
        return new TypeSafeMatcher<File>() {
            File fileTested;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                return item.isDirectory();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("that ");
                description.appendValue(fileTested);
                description.appendText(" is a directory");
            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                if(item.isFile()){
                    mismatchDescription.appendText("is a file");
                } else {
                    mismatchDescription.appendText("is neither a file or directory");
                }
            }


        };
    }

    @Factory
    public static Matcher<File> exists() {
        return new TypeSafeMatcher<File>() {
            File fileTested;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                return item.exists();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("file ");
                description.appendValue(fileTested);
                description.appendText(" exists");
            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                    mismatchDescription.appendText("did not exist");
            }

        };
    }

    @Factory
    public static Matcher<File> isFile() {
        return new TypeSafeMatcher<File>() {
            File fileTested;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                return item.isFile();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("that ");
                description.appendValue(fileTested);
                description.appendText(" is a file");
            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                super.describeMismatchSafely(item, mismatchDescription);
                if(item.isDirectory()){
                    mismatchDescription.appendText("is a directory");
                } else {
                    mismatchDescription.appendText("is neither a file or directory");
                }
            }


        };
    }

    @Factory
    public static Matcher<File> readable() {
        return new TypeSafeMatcher<File>() {
            File fileTested;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                return item.canRead();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" that file ");
                description.appendValue(fileTested);
                description.appendText(" is readable");
            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                mismatchDescription.appendText("not");
            }

        };
    }

    @Factory
    public static Matcher<File> writable() {
        return new TypeSafeMatcher<File>() {
            File fileTested;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                return item.canWrite();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" that file ");
                description.appendValue(fileTested);
                description.appendText("is writable");
            }
            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                mismatchDescription.appendText("not");
            }
        };
    }

    @Factory
    public static Matcher<File> sized(Long size) {
     return sized(Matchers.equalTo(size));
    }

    @Factory
    public static Matcher<File> sized(final Matcher<Long> size) {
        return new TypeSafeMatcher<File>() {
            File fileTested;
            long actualLength;

            @Override
            public boolean matchesSafely(File item) {
                fileTested = item;
                actualLength = item.length();
                return size.matches(actualLength);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" a file ");
                description.appendValue(fileTested);
                description.appendText(" sized with ");
                description.appendDescriptionOf(size);
                description.appendText(" bytes");

            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                mismatchDescription.appendText("was ");
                mismatchDescription.appendValue(actualLength);
                mismatchDescription.appendText(" bytes");

            }
        };
    }


    @Factory
    public static Matcher<File> named(final String name) {
        return new TypeSafeMatcher<File>() {
            private String filename;

            @Override
            public boolean matchesSafely(File item) {
                filename = item.getName();
                return name.matches(filename);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("File named ");
                description.appendValue(name);
            }

            @Override
            protected void describeMismatchSafely(File item, Description mismatchDescription) {
                mismatchDescription.appendText("named ");
                mismatchDescription.appendValue(filename);
            }


        };
    }

    @Factory
    public static Matcher<File> withCanonicalPath(final String path) {
        return new TypeSafeMatcher<File>() {

            private String canonPath;

            @Override
            public boolean matchesSafely(File item) {
                try {
                    canonPath = item.getCanonicalPath();
                    return path.matches(canonPath);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("File with canonical path ");
                description.appendValue(path);
            }

            @Override
            protected void describeMismatchSafely(File item, Description description) {
                description.appendText("was ");
                description.appendValue(canonPath);
            }
        };
    }

    @Factory
    public static Matcher<File> withAbsolutePath(final String path) {
        return new TypeSafeMatcher<File>() {
            private String absPath;

            @Override
            public boolean matchesSafely(File item) {
                absPath = item.getAbsolutePath();
                return path.matches(absPath);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("File with absolute path ");
                description.appendValue(path);
            }

            @Override
            protected void describeMismatchSafely(File item, Description description) {
                description.appendText("was ");
                description.appendValue(absPath);
            }

        };
    }

    @Factory
    public static Matcher<File> contains(final String... entries) {
        return new TypeSafeMatcher<File>() {

            private File item;
            private List<String> missing = new ArrayList<String>();

            @Override
            public boolean matchesSafely(File item) {
                this.item = item;
                try {
                    String content = readFully(item);
                    for (String entry : entries) {
                        if (!content.contains(entry)) {
                            missing.add(entry);
                        }
                    }
                    return missing.isEmpty();
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("File ");
                description.appendValue(item);
                description.appendText(" contains ");
                description.appendValueList("[", "][", "]", entries);
            }

            @Override
            protected void describeMismatchSafely(File item, Description description) {
                description.appendText("did not contain ");
                description.appendValueList("[", "][", "]", missing);
            }

        };
    }

    @Factory
    public static Matcher<File> doesNotContain(final String... entries) {
        return new TypeSafeMatcher<File>() {

            private File item;
            private List<String> contained = new ArrayList<String>();

            @Override
            public boolean matchesSafely(File item) {
                this.item = item;
                try {
                    String content = readFully(item);
                    for (String entry : entries) {
                        if (content.contains(entry)) {
                            contained.add(entry);
                        }
                    }
                    return contained.isEmpty();
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("File ");
                description.appendValue(item);
                description.appendText(" did not contain ");
                description.appendValueList("[", "][", "]", entries);
            }

            @Override
            protected void describeMismatchSafely(File item, Description description) {
                description.appendText("contained ");
                description.appendValueList("[", "][", "]", contained);
            }

        };
    }

    /**
     * Read from file till EOF.
     *
     * @param file the file from which to read
     * @return the contents read out of the given file
     * @throws IOException if the contents could not be read out from the
     *                     reader.
     */
    public static String readFully(File file) throws IOException {
        Reader rdr = null;
        try {
            rdr = new BufferedReader(new FileReader(file));
            return readFully(rdr, 8192);
        } finally {
            if (rdr != null) {
                rdr.close();
            }
        }
    }

    /**
     * Read from reader till EOF.
     *
     * @param rdr the reader from which to read.
     * @return the contents read out of the given reader.
     * @throws IOException if the contents could not be read out from the
     *                     reader.
     */
    public static String readFully(Reader rdr) throws IOException {
        return readFully(rdr, 8192);
    }

    /**
     * Read from reader till EOF.
     *
     * @param rdr        the reader from which to read.
     * @param bufferSize the buffer size to use when reading.
     * @return the contents read out of the given reader.
     * @throws IOException if the contents could not be read out from the
     *                     reader.
     */
    public static String readFully(Reader rdr, int bufferSize)
            throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater "
                    + "than 0");
        }
        final char[] buffer = new char[bufferSize];
        int bufferLength = 0;
        StringBuffer textBuffer = null;
        while (bufferLength != -1) {
            bufferLength = rdr.read(buffer);
            if (bufferLength > 0) {
                textBuffer = (textBuffer == null) ? new StringBuffer() : textBuffer;
                textBuffer.append(new String(buffer, 0, bufferLength));
            }
        }
        return (textBuffer == null) ? null : textBuffer.toString();
    }

}
