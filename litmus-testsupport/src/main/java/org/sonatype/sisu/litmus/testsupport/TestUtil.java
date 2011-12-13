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

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

// Based on Apache Geronimo https://github.com/apache/geronimo/blob/trunk/framework/modules/testsupport-common/src/main/java/org/apache/geronimo/testsupport/TestUtil.java

/**
 * Test utilities.
 *
 * @since 1.0
 */
public class TestUtil
{
    public static final String BASEDIR = "basedir"; //NON-NLS

    final Class owner;

    protected final File baseDir;

    @NonNls
    protected final Logger log;

    private File tmpDir;

    public TestUtil(final Class owner) {
        this.owner = checkNotNull(owner);
        this.baseDir = initBaseDir();
        this.log = LoggerFactory.getLogger(owner);
        log.trace("Base directory: {}", baseDir);
    }

    public TestUtil(final Object owner) {
        this(owner.getClass());
    }

    public File getBaseDir() {
        return baseDir;
    }

    public Logger getLog() {
        return log;
    }

    /**
     * Determine the value of <tt>${basedir}</tt>, which should be the base directory of
     * the module which the concrete test class is defined in.
     *
     * <p>
     * If The system property <tt>basedir</tt> is already set, then that value is used,
     * otherwise we determine the value from the code-source of the containing concrete class
     * and set the <tt>basedir</tt> system property to that value.
     *
     * @see #baseDir    This field is always initialized to the value which this method returns.
     *
     * @return  The base directory of the module which contains the concrete test class.
     */
    protected final File initBaseDir() {
        File dir;

        // If ${basedir} is set, then honor it
        String tmp = System.getProperty(BASEDIR);
        if (tmp != null) {
            dir = new File(tmp);
        }
        else {
            // Find the directory which this class (or really the sub-class of TestSupport) is defined in.
            String path = owner.getProtectionDomain().getCodeSource().getLocation().getFile();

            // We expect the file to be in target/test-classes, so go up 2 dirs
            dir = new File(path).getParentFile().getParentFile();

            // Set ${basedir} which is needed by logging to initialize
            System.setProperty(BASEDIR, dir.getPath());
        }

        return dir;
    }

    public File getTargetDir() {
        return resolveFile("target");
    }

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = getTargetDir();
        }
        return tmpDir;
    }

    public void setTmpDir(final File tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     * Resolve the given path to a file rooted to {@link #baseDir}.
     *
     * @param path  The path to resolve.
     * @return      The resolved file for the given path.
     */
    public final File resolveFile(final @NonNls String path) {
        checkNotNull(path);

        File file = new File(path);

        // Complain if the file is already absolute... probably an error
        if (file.isAbsolute()) {
            log.warn("Given path is already absolute; nothing to resolve: {}", file);
        }
        else {
            file = new File(baseDir, path);
        }

        return file;
    }

    /**
     * Resolve the given path to a path rooted to {@link #baseDir}.
     *
     * @param path  The path to resolve.
     * @return      The resolved path for the given path.
     *
     * @see #resolveFile(String)
     */
    public final String resolvePath(final String path) {
        return resolveFile(path).getPath();
    }

    public final File createTempFile(final File dir, final String prefix) throws IOException {
        File file = File.createTempFile(prefix + "-", ".tmp", dir); //NON-NLS
        file.deleteOnExit();
        return file;
    }

    public final File createTempFile(final String prefix) throws IOException {
        return createTempFile(getTmpDir(), prefix);
    }
    
    public final File createTempDir(final File dir, final String prefix) throws IOException {
        File file = createTempFile(dir, prefix);
        file.delete(); // ^^^ makes a file, so nuke it and turn it into a directory
        file.mkdirs();
        return file;
    }

    public final File createTempDir(final String prefix) throws IOException {
        return createTempDir(getTmpDir(), prefix);
    }
}