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

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;
import com.google.common.base.Strings;

/**
 * Some ideas to represent test assertions in more usable way
 * <p/>
 *
 * @author cstamas
 */
public class DiffMatchers
{

    private static class StringDiffMatcher
        extends TypeSafeMatcher<String>
    {

        private final String string;

        private String item;

        public StringDiffMatcher( final String string )
        {
            this.string = string;
        }

        @Override
        protected boolean matchesSafely( final String item )
        {
            this.item = item;

            if ( item == null )
            {
                return string == null;
            }
            else
            {
                return item.equals( string );
            }
        }

        @Override
        public void describeTo( final Description description )
        {
            final String diff = diffSideBySide( string, item );

            description.appendText( "\n" ).appendText( diff );
        }
    }

    @Factory
    public static Matcher<String> equalTo( final String string )
    {
        return new StringDiffMatcher( string );
    }

    // ==

    public static int maxLength( String... lines )
    {
        int maxLength = 0;

        for ( String line : lines )
        {
            if ( maxLength < line.length() )
            {
                maxLength = line.length();
            }
        }
        return maxLength;
    }

    public static String diffSideBySide( String fromStr, String toStr )
    {
        // this is equivalent of running unix diff -y command
        // not pretty, but it works. Feel free to refactor against unit test.
        String[] fromLines = fromStr.split( "\n" );
        String[] toLines = toStr.split( "\n" );
        List<Difference> diffs = ( new Diff( fromLines, toLines ) ).diff();

        int padding = 3;
        int maxStrWidth = Math.max( maxLength( fromLines ), maxLength( toLines ) ) + padding;

        StringBuilder diffOut = new StringBuilder();
        int fromLineNum = 0;
        int toLineNum = 0;
        for ( Difference diff : diffs )
        {
            int delStart = diff.getDeletedStart();
            int delEnd = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd = diff.getAddedEnd();

            boolean isAdd = ( delEnd == Difference.NONE && addEnd != Difference.NONE );
            boolean isDel = ( addEnd == Difference.NONE && delEnd != Difference.NONE );
            boolean isMod = ( delEnd != Difference.NONE && addEnd != Difference.NONE );

            //write out unchanged lines between diffs
            while ( true )
            {
                String left = "";
                String right = "";
                if ( fromLineNum < ( delStart ) )
                {
                    left = fromLines[fromLineNum];
                    fromLineNum++;
                }
                if ( toLineNum < ( addStart ) )
                {
                    right = toLines[toLineNum];
                    toLineNum++;
                }
                diffOut.append( Strings.padEnd( left, maxStrWidth, ' ' ) );
                diffOut.append( "  " ); // no operator to display
                diffOut.append( right ).append( "\n" );

                if ( ( fromLineNum == ( delStart ) ) && ( toLineNum == ( addStart ) ) )
                {
                    break;
                }
            }

            if ( isDel )
            {
                //write out a deletion
                for ( int i = delStart; i <= delEnd; i++ )
                {
                    diffOut.append( Strings.padEnd( fromLines[i], maxStrWidth, ' ' ) );
                    diffOut.append( "<" ).append( "\n" );
                }
                fromLineNum = delEnd + 1;
            }
            else if ( isAdd )
            {
                //write out an addition
                for ( int i = addStart; i <= addEnd; i++ )
                {
                    diffOut.append( Strings.padEnd( "", maxStrWidth, ' ' ) );
                    diffOut.append( "> " );
                    diffOut.append( toLines[i] ).append( "\n" );
                }
                toLineNum = addEnd + 1;
            }
            else if ( isMod )
            {
                // write out a modification
                while ( true )
                {
                    String left = "";
                    String right = "";
                    if ( fromLineNum <= ( delEnd ) )
                    {
                        left = fromLines[fromLineNum];
                        fromLineNum++;
                    }
                    if ( toLineNum <= ( addEnd ) )
                    {
                        right = toLines[toLineNum];
                        toLineNum++;
                    }
                    diffOut.append( Strings.padEnd( left, maxStrWidth, ' ' ) );
                    diffOut.append( "| " );
                    diffOut.append( right ).append( "\n" );

                    if ( ( fromLineNum > ( delEnd ) ) && ( toLineNum > ( addEnd ) ) )
                    {
                        break;
                    }
                }
            }

        }

        //we've finished displaying the diffs, now we just need to run out all the remaining unchanged lines
        while ( true )
        {
            String left = "";
            String right = "";
            if ( fromLineNum < ( fromLines.length ) )
            {
                left = fromLines[fromLineNum];
                fromLineNum++;
            }
            if ( toLineNum < ( toLines.length ) )
            {
                right = toLines[toLineNum];
                toLineNum++;
            }
            diffOut.append( Strings.padEnd( left, maxStrWidth, ' ' ) );
            diffOut.append( "  " ); // no operator to display
            diffOut.append( right ).append( "\n" );

            if ( ( fromLineNum == ( fromLines.length ) ) && ( toLineNum == ( toLines.length ) ) )
            {
                break;
            }
        }

        return diffOut.toString();
    }
}
