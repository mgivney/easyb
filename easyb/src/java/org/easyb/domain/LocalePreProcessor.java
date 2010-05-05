package org.easyb.domain;

import org.easyb.util.PreProcessorable;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PreProcessor that attempts to find a Locale setting in the story and
 * sets the default locale to whatever it finds, defaulting to US English
 * for backwards compatibility.  Locales can be set in a story like this:
 * "locale en_US"  or "locale fr_FR".  A locale setting should appear on
 * its own line.
 */
public class LocalePreProcessor implements PreProcessorable {
    private static Pattern LOCALE_PATTERN = Pattern.compile("locale\\s+([a-z]{2}_[A-Z]{2})");

    public String process(String input) {

        try {
            input = new String( input.getBytes(), "utf-8" );
        } catch (UnsupportedEncodingException e) {
            // no op
        }

        // default to English for backwards compatibility
        Locale locale = Locale.ENGLISH;

        // grab the first locale line
        for ( String line : input.split("\\n") ) {
            Matcher m = LOCALE_PATTERN.matcher( line.trim() );
            if ( m.matches() ) {
                String [] parts = m.group( 1 ).split( "_" );
                String lang = parts[0];
                String location = parts[1];
                locale = new Locale( lang, location );
            }
        }

        ResourceBundle strings =
                ResourceBundle.getBundle("org.easyb.i18n.keywords", locale);
        
        // now replace all keywords to their easyb defaults
        for ( String keyword : strings.keySet() ) {
            try {
                String replace = new String( strings.getString(keyword).getBytes() , "utf-8");
                input = input.replaceAll( replace, keyword );
            } catch(UnsupportedEncodingException e){
                // no op
            }
        }

        // get rid of the locale line so we don't mess up the story processing
        return LOCALE_PATTERN.matcher( input ).replaceAll("");
    }

    public String process(File inputFile) {
        StringBuilder contents = new StringBuilder();
        BufferedReader b = null;
        try {
            b = new BufferedReader( new FileReader( inputFile ) );
            String line = null;
            while ( ( line = b.readLine() ) != null ){
                contents.append( line );
                contents.append( "\n" );
            }
        }
        catch (IOException ex){
            throw new RuntimeException( "Failed to preprocess file for locale:", ex );
        }
        finally {
            try {
                if ( b != null ) b.close();
            } catch ( IOException e ) { /* ignore */ }
        }

        return process( contents.toString() );
    }
}
