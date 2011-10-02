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
package org.sonatype.sisu.litmus.testsupport;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

import java.io.File;
import java.io.FileNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test method related utilities.
 *
 * @since 1.0
 */
public class TestMethod extends TestWatchman {

    private static final String SRC_TEST = "src/test";
    /**
     * Test util.
     */
    private TestUtil util;

    /**
     * Running test name.
     */
    private String name;

    /**
     * Constructor.
     *
     * @param util test util
     */
    public TestMethod(final TestUtil util) {
        this.util = checkNotNull(util);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0
     */
    @Override
    public void starting(FrameworkMethod method) {
        name = method.getName();
    }

    /**
     * Returns the name of the currently-running test method
     *
     * @return the name of the currently-running test method
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a directory specific to running test method.
     * <p/>
     * Format: {@code <project>/target/<root>/<test class package>/<test class name>/<test method name>/<path>}
     *
     * @param root root name relative to target dir
     * @param path path to be appended to test method specific directory
     * @return directory specific to running test method + provided path
     */
    public File getTargetDirMethodFile(final String root, String path) {
        return
                new File(
                        new File(
                                new File(
                                        new File(
                                                util.getTargetDir(),
                                                root
                                        ),
                                        getClass().getCanonicalName().replace(".", "/")
                                ),
                                getName()
                        ),
                        path
                );
    }

    /**
     * Resolves a test file by looking up the specified path into test resources starting with a specified root.
     * <p/>
     * It searches the following path locations:<br/>
     * {@code <project>/src/test/<root>/<test class package>/<test class name>/<test method name>/<path>}<br/>
     * {@code <project>/src/test/<root>/<test class package>/<test class name>/<path>}<br/>
     * {@code <project>/src/test/<root>/<test class package>/<path>}<br/>
     * {@code <project>/src/test/<root>/<path>}<br/>
     *
     * @param root root directory
     * @param path path to look up
     * @return found file
     * @throws java.io.FileNotFoundException if path cannot be found in any of above locations
     * @since 1.0
     */
    public File resolveFile(final File root, final String path) throws FileNotFoundException {
        File level1 = testMethodSourceDirectory(root, path);
        if (level1.exists()) {
            return level1;
        }
        File level2 = testClassSourceDirectory(root, path);
        if (level2.exists()) {
            return level2;
        }
        File level3 = testPackageSourceDirectory(root, path);
        if (level3.exists()) {
            return level3;
        }
        File level4 = testSourceDirectory(root, path);
        if (level4.exists()) {
            return level4;
        }
        throw new FileNotFoundException("Path " + path + " not found in any of: " + level1 + ", " + level2 + ", " + level3 + ", " + level4);
    }

    /**
     * Returns a test source directory specific to running test.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test + provided path
     * @since 1.10.0
     */
    private File testSourceDirectory(final File root, final String path) {
        return
                new File(
                        root,
                        path
                );
    }

    /**
     * Returns a test source directory specific to running test class package.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test class + provided path
     * @since 1.10.0
     */
    private File testPackageSourceDirectory(final File root, final String path) {
        return
                new File(
                        new File(
                                root,
                                util.owner.getPackage().getName().replace(".", "/")
                        ),
                        path
                );
    }

    /**
     * Returns a test source directory specific to running test class.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test class + provided path
     * @since 1.10.0
     */
    private File testClassSourceDirectory(final File root, final String path) {
        return
                new File(
                        new File(
                                root,
                                util.owner.getCanonicalName().replace(".", "/")
                        ),
                        path
                );
    }

    /**
     * Returns a test source directory specific to running test method.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<test method name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test method + provided path
     * @since 1.10.0
     */
    private File testMethodSourceDirectory(final File root, final String path) {
        return
                new File(
                        new File(
                                new File(
                                        root,
                                        util.owner.getCanonicalName().replace(".", "/")
                                ),
                                getName()
                        ),
                        path
                );
    }

}