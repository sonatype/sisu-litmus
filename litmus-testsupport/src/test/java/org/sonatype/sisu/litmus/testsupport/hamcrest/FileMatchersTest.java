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

import java.io.File;
import org.hamcrest.Matchers;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author plynch
 */
public class FileMatchersTest {

    private static File REAL_FILE;

    private static File REAL_DIR;

    private static File NON_EXISTING_FILE;

    @Before
    public void setupFiles() throws Exception {
        REAL_FILE = new File(System.getProperty("basedir") + System.getProperty("file.separator") + "pom.xml");
        assertThat(REAL_FILE.isFile(), is(true));
        assertThat(REAL_FILE.canRead(), is(true));
        assertThat(REAL_FILE.canWrite(), is(true));


        REAL_DIR = new File(System.getProperty("basedir"));
        assertThat(REAL_DIR.isDirectory(), is(true));
        NON_EXISTING_FILE = new File(REAL_DIR, "/DOESNOTEXIST");
        assertThat(NON_EXISTING_FILE.exists(), is(false));
    }

    @Test
    public void isDirectoryMatchesDirectory() {
        assertThat(REAL_DIR, FileMatchers.isDirectory());
    }

    @Test
    public void isDirectoryDoesNotMatchFile() {
        assertThat(REAL_FILE, not(FileMatchers.isDirectory()));
    }

    @Test
    public void isFileDoesNotMatchDirectory() {
        assertThat(REAL_DIR, not(FileMatchers.isFile()));
    }

    @Test
    public void isFileMatchesFile() {
        assertThat(REAL_FILE, FileMatchers.isFile());
    }

    @Test
    public void existsFileThatDoesNotExist() {
        assertThat(NON_EXISTING_FILE, not(FileMatchers.exists()));
    }

    @Test
    public void existsDirThatExists() {
        assertThat(REAL_DIR, FileMatchers.exists());
    }

    @Test
    public void writableMatchesWritable() {
        assertThat(REAL_FILE, FileMatchers.writable());
    }

    @Test
    public void readableMatchesReadable() {
        assertThat(REAL_FILE, FileMatchers.writable());
    }

    @Test
    public void named() {
        assertThat(REAL_FILE, FileMatchers.named("pom.xml"));
    }

    @Test
    public void withCanonicalPath() {
        assertThat(REAL_FILE, not(FileMatchers.withCanonicalPath("pom.xml")));
    }

    @Test
    public void withAbsolutePath() {
        assertThat(REAL_FILE, not(FileMatchers.withAbsolutePath("pom.xml")));
    }


    @Test
    public void sizedWithMatcher() {
        assertThat(REAL_FILE, FileMatchers.sized(Matchers.greaterThan(100L)));
    }

    @Test
    public void sizedStandalone() {
        File sizedFile = new File(REAL_DIR, "src/test/resources/sized_file.txt");
        assertThat(sizedFile, FileMatchers.exists());
        assertThat(sizedFile, FileMatchers.sized(21L));
    }

}
