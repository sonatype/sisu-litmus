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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.contains;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.doesNotContain;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.exists;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.isDirectory;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

/**
 * {@link TestIndexRule} UTs.
 *
 * @since 1.4
 */
public class TestIndexRuleTest
    extends TestSupport
{

    private File indexRoot = util.resolveFile( "target/test-index-rule" );

    @Rule
    public TestInfoRule testInfo = new TestInfoRule();

    @Rule
    public TestIndexRule underTest = new TestIndexRule( indexRoot );

    /**
     * Verifies that a root directory is created.
     */
    @Test
    public void rootDirIsCreated()
    {
        final File rootDir = underTest.getDirectory();
        assertThat( rootDir, exists() );
        assertThat( rootDir, isDirectory() );
    }

    /**
     * Verifies that a sub dir directory is created.
     */
    @Test
    public void subDirIsCreated()
    {
        final File fooDir = underTest.getDirectory( "foo" );
        assertThat( fooDir, exists() );
        assertThat( fooDir, isDirectory() );
        assertThat( fooDir.getParentFile(), is( equalTo( underTest.getDirectory() ) ) );
    }

    /**
     * Verifies that a index.xml and friends are created.
     */
    @Test
    public void indexXmlAndFriendsAreCreated()
    {
        // index.xml are created lazy on first usage
        underTest.getDirectory();

        assertThat( new File( indexRoot, "index.xml" ), exists() );
        assertThat( new File( indexRoot, "index.css" ), exists() );
        assertThat( new File( indexRoot, "index.xsl" ), exists() );
    }

    /**
     * Verifies that information is recorded.
     */
    @Test
    public void recordInfo()
    {
        underTest.recordInfo( "info", testInfo.getMethodName() );
        underTest.save();
        assertThat( new File( indexRoot, "index.xml" ), contains( testInfo.getMethodName() ) );
    }

    /**
     * Verifies that last information is recorded for a key.
     */
    @Test
    public void recordOnlyLastInfo()
    {
        underTest.recordInfo( "info", "Initial "+ testInfo.getMethodName());
        underTest.recordInfo( "info", "Updated " + testInfo.getMethodName() );
        underTest.save();
        assertThat( new File( indexRoot, "index.xml" ), contains( "Updated "+ testInfo.getMethodName() ) );
        assertThat( new File( indexRoot, "index.xml" ), doesNotContain( "Initial "+ testInfo.getMethodName() ) );
    }

    /**
     * Verifies that a relative path is recorded as info.
     */
    @Test
    public void recordLinkAboutFileInIndexDir()
    {
        underTest.recordLink( "info", underTest.getDirectory( "some-dir" ) );
        underTest.save();
        assertThat( new File( indexRoot, "index.xml" ), contains( "some-dir" ) );
    }

    /**
     * Verifies that a relative path is recorded as info.
     */
    @Test
    public void recordLinkAboutFileNotInIndexDir()
    {
        final File file = util.resolveFile( "target/some-dir" );
        file.mkdirs();
        underTest.recordLink( "info", file );
        underTest.save();
        assertThat( new File( indexRoot, "index.xml" ), contains( "../some-dir" ) );
    }

    /**
     * Calculate relative path from a dir to a sub dir.
     */
    @Test
    public void relativePath01()
        throws IOException
    {
        final File from = new File( "a/b/c" );
        final File to = new File( "a/b/c/d" );

        final String relativePath = TestIndexRule.calculateRelativePath( from, to );
        assertThat( relativePath, is( equalTo( "d" ) ) );
    }

    /**
     * Calculate relative path from a sub dir to a parent dir.
     */
    @Test
    public void relativePath02()
        throws IOException
    {
        final File from = new File( "a/b/c/d" );
        final File to = new File( "a/b/c" );

        final String relativePath = TestIndexRule.calculateRelativePath( from, to );
        assertThat( relativePath, is( equalTo( ".." ) ) );
    }

    /**
     * Calculate relative path from a sub dir to a sub dir or same parent dir.
     */
    @Test
    public void relativePath03()
        throws IOException
    {
        final File from = new File( "a/b/c/d" );
        final File to = new File( "a/b/e/f" );

        final String relativePath = TestIndexRule.calculateRelativePath( from, to );
        assertThat( relativePath, is( equalTo( "../../e/f" ) ) );
    }

}
