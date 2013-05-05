
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
 *    | (R)              // grouping
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
    private Set<Character> firstOfGrouping = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(new Character[]{'('})));

    @Override
    public RegularExpression parse(String s) {
        
        // create a PushbackReader from the given string
        PushbackReader reader = new PushbackReader(new StringReader(s));
        
        RegularExpression regex = null;
        try {
            // parse and return the regular expression from the PushbackReader
            regex = parseRegularExpression(reader);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString());
        }
        
        return regex;
    }
    
    private RegularExpression parseRegularExpression(PushbackReader reader) throws IOException {
        
        // read the next character of input
        char next = (char)reader.read();
        
        // and immediately push it back
        reader.unread(next);
        
        // decide which right hand side to use based on the next input character
        RegularExpression re = null;
        if (firstOfByteLiteral.contains(next)) {
            re = parseByteLiteral(reader);
        } else if (firstOfGrouping.contains(next)) {
            re = parseGrouping(reader);
        } else {
            logger.log(Level.SEVERE, "Could not parse input string.");
        }

        return re;
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
    
    private RegularExpression parseGrouping(PushbackReader reader) throws IOException {
        
        // make sure the next character is '('
        char next = (char)reader.read();
        if (next != '(') {
            throw new RuntimeException("Expected '(' but read '" + next + "'");
        }
        
        // parse the grouped regular expression
        RegularExpression re = parseRegularExpression(reader);
        
        // make sure the next character is ')'
        next = (char)reader.read();
        if (next != ')') {
            throw new RuntimeException("Expected ')' but read '" + next + "'");
        }
        
        return re;
    }
}
