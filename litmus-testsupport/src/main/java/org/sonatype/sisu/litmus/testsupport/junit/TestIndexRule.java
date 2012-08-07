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

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;
import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.sonatype.sisu.litmus.testsupport.TestIndex;
import org.sonatype.sisu.litmus.testsupport.junit.index.IndexXO;
import org.sonatype.sisu.litmus.testsupport.junit.index.TestInfoXO;
import org.sonatype.sisu.litmus.testsupport.junit.index.TestXO;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * JUnit rule for creating/accessing directories/files unique to a test method/run. The rule will create a mapping file
 * (an index) to easy link the tests to its directories. The index file (index.xml) is created in the directory
 * configured as root (see constructor}.
 * <p/>
 * How it works:
 * <p/>
 * When a test directory/file is requested, or an info/link is recorded, the rule will generate an directory unique to
 * the test method that is running, which will be then used for all calls done during that specific test method/run.
 * When the test method/run ends, the information specific to the test is written to index file (index.xml).
 * <p/>
 * So, for example in case of the following test:<br/>
 * <pre class="code"><code class="java">
 * public class SomeTest
 * &nbsp;&nbsp;extends TestSupport
 * {
 * <br/>
 * &nbsp;&nbsp;&#064;Rule
 * &nbsp;&nbsp;public TestIndexRule index = new TestIndexRule( util.resolveFile( "target/example" ) );
 * <br/>
 * &nbsp;&nbsp;&#064;Test
 * &nbsp;&nbsp;public void testFoo()
 * &nbsp;&nbsp;{
 * &nbsp;&nbsp;&nbsp;&nbsp;index.getDirectory( "foo" );
 * &nbsp;&nbsp;}
 * <br/>
 * &nbsp;&nbsp;&#064;Test
 * &nbsp;&nbsp;public void testBar()
 * &nbsp;&nbsp;{
 * &nbsp;&nbsp;&nbsp;&nbsp;index.getDirectory( "foo" );
 * &nbsp;&nbsp;&nbsp;&nbsp;index.getDirectory( "bar" );
 * &nbsp;&nbsp;}
 * <br/>
 * }
 * </code></pre>
 * The created structure on disk will look similar to:<br/>
 * <pre class="code"><code class="text">
 * target/example
 * |
 * |- 1
 * |  |- foo
 * |
 * |- 2
 * |  |- bar
 * |  |- foo
 * |
 * |- ...
 * index.xml
 * </code></pre>
 * The index file will contain the mappings between the numbered directories (1,2,...) and the test class/method.
 *
 * @since 1.4
 */
public class TestIndexRule
    extends TestWatcher
    implements TestIndex
{

    /**
     * The root directory that contains the index and test specific directories.
     * Never null.
     */
    private final File indexDir;

    /**
     * Test description.
     * Set when test starts.
     */
    private Description description;

    /**
     * Timestamp when test was started.
     */
    private long startTime;

    /**
     * True if the rule is initialized.
     * Used to lazy create an index entry on first usage.
     */
    private boolean initialized;

    /**
     * Test specific directory of format ${indexDir}/${counter}.
     * Lazy initialized upon first usage.
     */
    private File testDir;

    /**
     * File contained indexing information.
     * Lazy initialized upon first usage.
     */
    private File indexXml;

    /**
     * Index data.
     * Lazy initialized upon first usage.
     */
    private IndexXO index;

    /**
     * Index test data.
     * Lazy initialized upon first usage.
     */
    private TestXO test;

    /**
     * Constructor.
     *
     * @param indexDir root directory that contains the index and test specific directories. Cannot be null.
     */
    public TestIndexRule( final File indexDir )
    {
        this.indexDir = indexDir;
    }

    @Override
    protected void starting( final Description description )
    {
        this.description = Preconditions.checkNotNull( description );
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected void succeeded( final Description description )
    {
        initialize();
        test.setSuccess( true );
    }

    @Override
    protected void failed( final Throwable e, final Description description )
    {
        initialize();
        test.setSuccess( false );
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        test.setThrowableMessage( e.getMessage() );
        test.setThrowableStacktrace( sw.toString() );
    }

    @Override
    protected void finished( final Description description )
    {
        initialize();
        test.setDuration( ( System.currentTimeMillis() - startTime ) / 1000 );
        save();
    }

    @Override
    public File getDirectory()
    {
        initialize();
        return testDir;
    }

    @Override
    public File getDirectory( final String name )
    {
        final File dir = new File( getDirectory(), name );
        checkState(
            ( dir.mkdirs() || dir.exists() ) && dir.isDirectory(),
            "Not able to create test directory '{}'",
            dir.getAbsolutePath()
        );
        return dir;
    }

    @Override
    public void recordInfo( final String key, final String value )
    {
        initialize();
        test.withTestInfos( new TestInfoXO().withLink( false ).withKey( key ).withValue( value ) );
    }

    @Override
    public void recordLink( final String key, final String value )
    {
        initialize();
        test.withTestInfos( new TestInfoXO().withLink( true ).withKey( key ).withValue( value ) );
    }

    @Override
    public void recordLink( final String key, final File file )
    {
        if ( file.exists() )
        {
            initialize();
            try
            {
                recordLink( key, calculateRelativePath( indexDir, file ) );
            }
            catch ( IOException e )
            {
                throw Throwables.propagate( e );
            }
        }
    }

    /**
     * Reads the index from ${indexDir}/index.xml and records information about current running test.
     * It will also create the test specific directory under ${indexDir}.
     */
    private void initialize()
    {
        checkState( description != null );
        if ( !initialized )
        {
            load();

            index.setCounter( index.getCounter() + 1 );

            test = new TestXO()
                .withIndex( index.getCounter() )
                .withClassName( description.getClassName() )
                .withMethodName( description.getMethodName() );

            index.withTests( test );

            save();
            copyStyleSheets();

            testDir = new File( indexDir, String.valueOf( index.getCounter() ) );
            checkState(
                ( testDir.mkdirs() || testDir.exists() ) && testDir.isDirectory(),
                "Not able to create test directory '{}'",
                testDir.getAbsolutePath()
            );

            initialized = true;
        }
    }

    /**
     * Copy index CSS and XSLT to ${indexDir} (they are referenced by index.xml), overriding existent ones.
     */
    private void copyStyleSheets()
    {
        try
        {
            copyURLToFile(
                getClass().getClassLoader().getResource( "index.css" ),
                new File( indexDir, "index.css" )
            );
            copyURLToFile(
                getClass().getClassLoader().getResource( "index.xsl" ),
                new File( indexDir, "index.xsl" )
            );
        }
        catch ( IOException e )
        {
            // well, that's it!
        }
    }

    /**
     * Loads index data from ${indexDir}/index.xml.
     */
    private void load()
    {
        indexXml = new File( indexDir, "index.xml" );
        index = new IndexXO().withCounter( 0 );
        if ( indexXml.exists() )
        {
            try
            {
                final Unmarshaller unmarshaller = JAXBContext.newInstance( IndexXO.class ).createUnmarshaller();
                index = (IndexXO) unmarshaller.unmarshal( indexXml );
            }
            catch ( Exception e )
            {
                // TODO Should we fail the test if we cannot write the index?
                throw Throwables.propagate( e );
            }
        }
    }

    /**
     * Saves index data from ${indexDir}/index.xml.
     */
    // @TestAccessible
    void save()
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter( writer );

            printWriter.println( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" );
            printWriter.println( "<?xml-stylesheet type=\"text/css\" href=\"index.css\"?>" );
            printWriter.println( "<?xml-stylesheet type=\"text/xsl\" href=\"index.xsl\"?>" );

            final Marshaller marshaller = JAXBContext.newInstance( IndexXO.class ).createMarshaller();
            marshaller.setProperty( JAXB_FORMATTED_OUTPUT, TRUE );
            marshaller.setProperty( JAXB_FRAGMENT, TRUE );
            marshaller.marshal( index, writer );

            writeStringToFile( indexXml, writer.toString() );
        }
        catch ( Exception e )
        {
            // TODO Should we fail the test if we cannot write the index?
            throw Throwables.propagate( e );
        }
    }

    /**
     * Calculates the relative path to a given file from a specified file.
     *
     * @param from File from which the relative path should be calculated
     * @param to   File to which the relative path should be calculated
     * @return relative path
     * @throws IOException if files have no common sub directories beside the root, or none at all
     */
    static String calculateRelativePath( final File from,
                                         final File to )
        throws IOException
    {
        final File parent = from.getParentFile();
        if ( parent == null )
        {
            throw new IOException( "File '" + from.getAbsolutePath() + "' cannot be a root directory" );
        }
        final String fromPath = from.getCanonicalPath();
        final String toPath = to.getCanonicalPath();
        if ( toPath.equals( fromPath ) )
        {
            return "";
        }
        if ( toPath.startsWith( fromPath ) )
        {
            return toPath.substring( fromPath.length() + 1 );
        }
        final String relativePath = calculateRelativePath( parent, to );
        return ( ".." + ( relativePath.trim().isEmpty() ? "" : File.separator + relativePath ) );
    }

}