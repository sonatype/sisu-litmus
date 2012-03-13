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
package org.sonatype.sisu.litmus.testsupport;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * {@link org.sonatype.sisu.litmus.testsupport.TestMethod} UTs.
 *
 * @since 1.0
 */
public class TestMethodTest
        extends TestSupport {


    /**
     * Root directory used in tests.
     */
    private File root;

    /**
     * Setups root directory.
     * <p/>
     * {@inheritDoc}
     *
     * @since 1.0
     */
    @Before
    public void setUp() {
        root = util.resolveFile("src/test/uncopied-resources");
    }

    /**
     * Tests that name is equal to test method.
     *
     * @since 1.0
     */
    @Test
    public void testName() {
        assertThat(testMethod.getName(), is(equalTo("testName")));
    }

    /**
     * Test that a file is resolved from root directory.
     *
     * @throws FileNotFoundException re-thrown
     * @since 1.0
     */
    @Test
    public void resolveFromRoot() throws FileNotFoundException {
        File file = testMethod.resolveFile(root, "from-root");
        assertThat(file, is(equalTo(util.resolveFile("src/test/uncopied-resources/from-root"))));
    }

    /**
     * Test that a file is resolved from package directory in root directory.
     *
     * @throws FileNotFoundException re-thrown
     * @since 1.0
     */
    @Test
    public void resolveFromPackage() throws FileNotFoundException {
        File file = testMethod.resolveFile(root, "from-package");
        assertThat(file, is(equalTo(util.resolveFile("src/test/uncopied-resources/org/sonatype/sisu/litmus/testsupport/from-package"))));
    }

    /**
     * Test that a file is resolved from class directory in root directory.
     *
     * @throws FileNotFoundException re-thrown
     * @since 1.0
     */
    @Test
    public void resolveFromClass() throws FileNotFoundException {
        File file = testMethod.resolveFile(root, "from-class");
        assertThat(file, is(equalTo(util.resolveFile("src/test/uncopied-resources/org/sonatype/sisu/litmus/testsupport/TestMethodTest/from-class"))));
    }

    /**
     * Test that a file is resolved from method directory in root directory.
     *
     * @throws FileNotFoundException re-thrown
     * @since 1.0
     */
    @Test
    public void resolveFromMethod() throws FileNotFoundException {
        File file = testMethod.resolveFile(root, "from-method");
        assertThat(file, is(equalTo(util.resolveFile("src/test/uncopied-resources/org/sonatype/sisu/litmus/testsupport/TestMethodTest/resolveFromMethod/from-method"))));
    }

    /**
     * Test that a method specific target directory is resolved.
     *
     * @since 1.0
     */
    @Test
    public void targetDirMethodFile() {
        File file = testMethod.getTargetDirMethodFile("foo", "bar/car.txt");
        assertThat(file, is(equalTo(new File(util.getTargetDir(), "foo/org/sonatype/sisu/litmus/testsupport/TestMethod/targetDirMethodFile/bar/car.txt"))));
    }

}
