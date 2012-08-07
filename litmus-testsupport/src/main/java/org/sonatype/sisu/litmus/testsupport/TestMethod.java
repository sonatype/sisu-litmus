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

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.rules.TestWatcher;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test method related utilities.
 *
 * @since 1.0
 * @deprecated Use {@link org.sonatype.sisu.litmus.testsupport.junit.TestDataRule} instead.
 */
@Deprecated()
public class TestMethod extends TestWatcher {

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
    protected void starting(Description description) {
        name = description.getMethodName();
    }

    /**
     * Returns the name of the currently-running test method
     *
     * @return the name of the currently-running test method
     * @deprecated Use {@link org.sonatype.sisu.litmus.testsupport.junit.TestInfoRule#getMethodName()} instead.
     */
    @Deprecated()
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
     * @deprecated Use {@link org.sonatype.sisu.litmus.testsupport.junit.TestIndexRule#getDirectory(String)} instead.
     */
    @Deprecated()
    public File getTargetDirMethodFile(final String root, String path) {
        return file(util.getTargetDir(), root, asPath(getClass()), getName(), path);
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
     * @deprecated Use {@link org.sonatype.sisu.litmus.testsupport.junit.TestDataRule#resolveFile(String)} instead.
     */
    @Deprecated()
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
     * @since 1.0
     */
    private File testSourceDirectory(final File root, final String path) {
        return file(root, path);
    }

    /**
     * Returns a test source directory specific to running test class package.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test class + provided path
     * @since 1.0
     */
    private File testPackageSourceDirectory(final File root, final String path) {
        return file(root, asPath(util.owner.getPackage()), path);
    }

    /**
     * Returns a test source directory specific to running test class.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test class + provided path
     * @since 1.0
     */
    private File testClassSourceDirectory(final File root, final String path) {
        return file(root, asPath(util.owner), path);
    }

    /**
     * Returns a test source directory specific to running test method.
     * <p/>
     * Format: {@code <project>/src/test/<root>/<test class package>/<test class name>/<test method name>/<path>}
     *
     * @param root root directory
     * @param path path to be appended
     * @return test source directory specific to running test method + provided path
     * @since 1.0
     */
    private File testMethodSourceDirectory(final File root, final String path) {
        return file(root, asPath(util.owner), getName(), path);
    }

    /**
     * Return a file path to a class (replaces "." with "/")
     *
     * @param clazz class to get the path for
     * @return path to class
     * @since 1.0
     */
    private static String asPath(final Class<?> clazz) {
        return asPath(clazz.getPackage()) + "/" + clazz.getSimpleName();
    }

    /**
     * Return a file path from a package (replaces "." with "/")
     *
     * @param pkg package to get the path for
     * @return package path
     * @since 1.0
     */
    private static String asPath(final Package pkg) {
        return pkg.getName().replace(".", "/");
    }

    /**
     * File builder
     *
     * @param root  starting root
     * @param paths paths to append
     * @return a file starting from root and appended sub-paths
     * @since 1.0
     */
    private static File file(final File root, final String... paths) {
        File file = root;
        for (String path : paths) {
            file = new File(file, path);
        }
        return file;
    }

}
