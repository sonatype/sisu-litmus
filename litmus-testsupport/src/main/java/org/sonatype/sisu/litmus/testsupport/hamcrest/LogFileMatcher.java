package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import com.google.common.base.Preconditions;

/**
 * TODO
 *
 * @since 1.0
 */
public abstract class LogFileMatcher
    extends TypeSafeMatcher<File>
    implements NegativeMatcher<File>
{

    private File logFile;

    private int failingLineNumber;

    private String failingLine;

    @Override
    protected final boolean matchesSafely( final File logFile )
    {
        Preconditions.checkNotNull( logFile );
        this.logFile = logFile;
        try
        {
            @SuppressWarnings( "unchecked" )
            final List<String> lines = (List<String>) FileUtils.readLines( logFile, "UTF-8" );
            if ( lines != null && lines.size() > 0 )
            {
                failingLineNumber = 0;
                for ( final String line : lines )
                {
                    failingLineNumber++;
                    failingLine = line;
                    if ( matchesLine( line ) )
                    {
                        return true;
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new AssertionError( e );
        }
        failingLineNumber = 0;
        failingLine = null;
        return false;
    }

    @Override
    public final void describeTo( final Description description )
    {
        description.appendText( "that log file " );
        if ( logFile != null )
        {
            description.appendValue( logFile.getName() );
            description.appendText( " " );
        }
        describeTo( logFile, description );
    }

    @Override
    public void describeNegationTo( final Description description )
    {
        description.appendText( "that log file " );
        if ( logFile != null )
        {
            description.appendValue( logFile.getName() );
            description.appendText( " " );
        }
        description.appendText( "does not " );
        describeTo( logFile, description );
    }

    @Override
    protected final void describeMismatchSafely( final File logFile, final Description mismatchDescription )
    {
        if ( failingLine != null )
        {
            mismatchDescription
                .appendText( "contained on line " )
                .appendValue( failingLineNumber )
                .appendText( ": " )
                .appendValue( failingLine );
        }
        else
        {
            mismatchDescription.appendText( "log file " );
            describeMismatchTo( logFile, mismatchDescription );
        }
    }

    protected abstract boolean matchesLine( final String line );

    protected abstract void describeTo( final File logFile, final Description description );

    protected abstract void describeMismatchTo( final File logFile, final Description mismatchDescription );

    @Factory
    public static LogFileMatcher hasExceptionOfType( final Class<? extends Exception> exception )
    {
        return new LogFileMatcher()
        {
            @Override
            public boolean matchesLine( final String line )
            {
                return line.contains( exception.getName() + ":" );
            }

            @Override
            protected void describeTo( final File logFile, final Description description )
            {
                description.appendText( "contains exception of type " ).appendValue( exception.getName() );
            }

            @Override
            protected void describeMismatchTo( final File logFile, final Description mismatchDescription )
            {
                mismatchDescription.appendText( "did not contain " ).appendValue( exception.getName() );
            }

        };
    }

    @Factory
    public static LogFileMatcher hasText( final String text )
    {
        return new LogFileMatcher()
        {
            @Override
            public boolean matchesLine( final String line )
            {
                return line.contains( text );
            }

            @Override
            protected void describeTo( final File logFile, final Description description )
            {
                description.appendText( "contains " ).appendValue( text );
            }

            @Override
            protected void describeMismatchTo( final File logFile, final Description mismatchDescription )
            {
                mismatchDescription.appendText( "did not contain " ).appendValue( text );
            }

        };
    }

    @Factory
    public static LogFileMatcher hasText( final Pattern pattern )
    {
        return new LogFileMatcher()
        {
            @Override
            public boolean matchesLine( final String line )
            {
                return pattern.matcher( line ).matches();
            }

            @Override
            protected void describeTo( final File logFile, final Description description )
            {
                description.appendText( "matches " ).appendValue( pattern );
            }

            @Override
            protected void describeMismatchTo( final File logFile, final Description mismatchDescription )
            {
                mismatchDescription.appendText( "did not match " ).appendValue( pattern );
            }

        };
    }

}
