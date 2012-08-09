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
package org.sonatype.sisu.litmus.testsupport.junit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.sonatype.sisu.litmus.testsupport.TestData;

/**
 * JUnit rule for accessing test data
 *
 * @since 1.4
 */
public class TestDataRule
    extends TestWatcher
    implements TestData
{

    /**
     * The root directory containing test data.
     * Cannot be null.
     */
    private final File dataDir;

    /**
     * Test description.
     * Set when test starts.
     */
    private Description description;

    /**
     * Constructor.
     *
     * @param dataDir root directory containing test data. Cannot be null.
     */
    public TestDataRule( final File dataDir )
    {
        this.dataDir = checkNotNull( dataDir );
    }

    @Override
    protected void starting( final Description description )
    {
        this.description = checkNotNull( description );
    }

    @Override
    public File resolveFile( final String path )
    {
        checkState( description != null, "Test was not yet initialized" );
        File level1 = testMethodSourceDirectory( path );
        if ( level1.exists() )
        {
            return level1;
        }
        File level2 = resolveFromClassDirectory( path );
        if ( level2.exists() )
        {
            return level2;
        }
        File level3 = resolveFromPackageDirectory( path );
        if ( level3.exists() )
        {
            return level3;
        }
        File level4 = resolveFromDataDirectory( path );
        if ( level4.exists() )
        {
            return level4;
        }
        throw new RuntimeException(
            "Path " + path + " not found in any of: " + level1 + ", " + level2 + ", " + level3 + ", " + level4
        );
    }

    /**
     * Returns a test data file.
     * <p/>
     * Format: {@code <dataDir>/</path>}
     *
     * @param path path to be appended
     * @return test source directory specific to running test + provided path
     */
    private File resolveFromDataDirectory( final String path )
    {
        return file( dataDir, path );
    }

    /**
     * Returns a test data file.
     * <p/>
     * Format: {@code <dataDir>/<test class package>/</path>}
     *
     * @param path path to be appended
     * @return test source directory specific to running test class package + provided path
     */
    private File resolveFromPackageDirectory( String path )
    {
        return file( dataDir, asPath( description.getTestClass().getPackage() ), path );
    }

    /**
     * Returns a test data file.
     * <p/>
     * Format: {@code <dataDir>/<test class package>/<test class name>/</path>}
     *
     * @param path path to be appended
     * @return test source directory specific to running test class + provided path
     */
    private File resolveFromClassDirectory( String path )
    {
        return file( dataDir, asPath( description.getTestClass() ), path );
    }

    /**
     * Returns a test data file.
     * <p/>
     * Format: {@code <dataDir>/<test class package>/<test class name>/<test method name>/</path>}
     *
     * @param path path to be appended
     * @return test source directory specific to running test method + provided path
     */
    private File testMethodSourceDirectory( String path )
    {
        return file( dataDir, asPath( description.getTestClass() ), mn( description.getMethodName() ), path );
    }

    /**
     * Return a file path to a class (replaces "." with "/")
     *
     * @param clazz class to get the path for
     * @return path to class
     * @since 1.0
     */
    //@TestAccessible
    static String asPath( final Class<?> clazz )
    {
        return asPath( clazz.getPackage() ) + "/" + clazz.getSimpleName();
    }

    /**
     * Return a file path from a package (replaces "." with "/")
     *
     * @param pkg package to get the path for
     * @return package path
     */
    private static String asPath( final Package pkg )
    {
        return pkg.getName().replace( ".", "/" );
    }

    /**
     * File builder
     *
     * @param root  starting root
     * @param paths paths to append
     * @return a file starting from root and appended sub-paths
     */
    private static File file( final File root, final String... paths )
    {
        File file = root;
        for ( String path : paths )
        {
            file = new File( file, path );
        }
        return file;
    }

    /**
     * Drops index part from name in case of a parametrized test.
     *
     * @param methodName method name
     * @return index-less method name
     */
    private String mn( final String methodName )
    {
        if ( methodName.contains( "[" ) )
        {
            return methodName.substring( 0, methodName.indexOf( "[" ) );
        }
        return methodName;
    }

}
