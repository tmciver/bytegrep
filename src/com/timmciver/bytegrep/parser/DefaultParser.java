
package com.timmciver.bytegrep.parser;

import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.RegularExpression;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The default Parser implementation for ByteGrep.  The following defines
 * the grammar accepted by this parser.
 * 
 * R := byte-literal
 * 
 * byte-literal defines a single byte and has the form '0xXY' where X and Y
 * represent hexadecimal digits.
 * 
 * @author tim
 */
public class DefaultParser implements Parser {
    
    private final static Logger logger = Logger.getLogger(DefaultParser.class.getName());
    
    private Set<Character> firstOfByteLiteral = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(new Character[]{'0'})));

    @Override
    public RegularExpression parse(String s) {
        
        // create a PushbackReader from the given string
        PushbackReader reader = new PushbackReader(new StringReader(s));
        
        RegularExpression regex = null;
        try {
            // parse and return the regular expression from the PushbackReader
            regex = parseRegularExpression(reader);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException while reading from PushbackReader.");
        }
        
        return regex;
    }
    
    private RegularExpression parseRegularExpression(PushbackReader reader) throws IOException {
        
        // read the next character of input
        char next = (char)reader.read();
        
        // and immediately push it back
        reader.unread(next);
        
        // since we're only parsing byte literals for now we only need to check 
        // that the next character is in the first of a byte literal.
        if (!firstOfByteLiteral.contains(next)) {
            logger.log(Level.SEVERE, "Expexted byte literal of the form '0xXY'");
            return null;
        }

        return parseByteLiteral(reader);
    }
    
    private RegularExpression parseByteLiteral(PushbackReader reader) throws IOException {
        
        // read the next four bytes
        char[] str = new char[4];
        reader.read(str);
        
        // the first two characters must be '0x'
        if (str[0] != '0' || Character.toLowerCase(str[1]) != 'x') {
            // push what we've read back into the reader
            reader.unread(str);
            return null;
        }
        
        // create a string from the char array
        String hexStr = new String(str);
        
        // try to decode the hex string
        int val;
        try {
            val = Integer.decode(hexStr);
        } catch (NumberFormatException nfe) {
            logger.log(Level.INFO, "Failed parsing a byte literal.");
            reader.unread(str);
            return null;
        }
        
        // cast to a byte
        byte byteVal = (byte)val;
        
        // return a LiteralByte regex
        return new LiteralByte(byteVal);
    }
}
