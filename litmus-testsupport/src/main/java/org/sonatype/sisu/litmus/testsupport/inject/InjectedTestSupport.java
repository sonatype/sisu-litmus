
package org.sonatype.sisu.litmus.testsupport.inject;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.sonatype.gossip.Level;
import org.sonatype.guice.bean.containers.InjectedTest;
import org.sonatype.sisu.litmus.testsupport.TestTracer;
import org.sonatype.sisu.litmus.testsupport.TestUtil;

/**
 * Support for injection-based tests.
 *
 * @since 1.0
 */
public class InjectedTestSupport
    extends InjectedTest
{

    static
    {
        System.setProperty( "guice.disable.misplaced.annotation.check", "true" );
        // http://code.google.com/p/guava-libraries/issues/detail?id=92
        System.setProperty( "guava.executor.class", "NONE" );
        // http://code.google.com/p/google-guice/issues/detail?id=288#c30
        System.setProperty( "guice.executor.class", "NONE" );
    }

    protected final TestUtil util = new TestUtil(this);

    @NonNls
    protected final Logger logger = util.getLog();

    private Level logLevel = Level.INFO;

    @Rule
    public final MethodRule tracer = new TestTracer(this);

    @Rule
    public final TestName testName = new TestName();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(final Level logLevel) {
        this.logLevel = Preconditions.checkNotNull(logLevel);
    }

    protected void log(final @NonNls String message) {
        logLevel.log(logger, message);
    }

    protected void log(final Object value) {
        logLevel.log(logger, String.valueOf(value));
    }

    protected void log(final @NonNls String format, final Object... args) {
        logLevel.log(logger, format, args);
    }

    protected void log(final @NonNls String message, final Throwable cause) {
        logLevel.log(logger, message, cause);
    }

    @Override
    public void configure( final Properties properties )
    {
        loadAll( properties, "injected-test.properties" );
        // per test class properties
        load( properties, this.getClass().getSimpleName() + "/injected-test.properties" );
        super.configure( properties );
    }


    private void loadAll( final Properties properties, final String name )
    {
        try
        {
            final Enumeration<URL> resources = getClass().getClassLoader().getResources( name );
            while ( resources.hasMoreElements() )
            {
                final URL resource = resources.nextElement();
                load( properties, resource );
            }
        }
        catch ( final IOException e )
        {
            throw new IllegalStateException( "Failed to load " + name, e );
        }
    }

    private void load( final Properties properties, final String name )
    {
        load( properties, getClass().getResource( name ) );
    }

    private void load( final Properties properties, final URL url )
    {
        if ( url != null )
        {
            InputStream in = null;
            try
            {
                in = url.openStream();
                if ( in != null )
                {
                    properties.load( in );
                }
                properties.putAll( System.getProperties() );
            }
            catch ( final IOException e )
            {
                throw new IllegalStateException( "Failed to load " + url.toExternalForm(), e );
            }
            finally
            {
                if ( in != null )
                {
                    try
                    {
                        in.close();
                    }
                    catch( IOException ex )
                    {
                        log("Failed to close property inputstream {}", ex.getMessage());
                    }
                }
            }
        }
    }

}
