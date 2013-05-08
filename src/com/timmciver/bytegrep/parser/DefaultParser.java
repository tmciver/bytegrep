
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
 * R ::= ST
 * 
 * S ::= byte-literal
 *     | (R)              // grouping
 * 
 * T ::= |R               // alternation
 *     | *                // zero or more
 *     | +                // one or more
 *     | ?                // zero or one
 *     | R                // sequence
 *     | epsilon
 * 
 * byte-literal ::= 0xXY  // defines a single byte where X and Y represent
 *                        // hexadecimal digits.
 * 
 * @author tim
 */
public class DefaultParser implements Parser {
    
    private final static Logger logger = Logger.getLogger(DefaultParser.class.getName());
    
    // a set representing first(S)
    private Set<Character> firstOfS;
    
    // a set representing first(T)
    private Set<Character> firstOfT;

    public DefaultParser() {
        
        // initialize firstOfS
        firstOfS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList('0', '(')));
        
        // initialize firstOfT
        firstOfT = new HashSet<>(firstOfS);     // first(T) contains first(S), plus some other stuff
        firstOfT.addAll(Arrays.asList('|', '*', '+', '?', '$'));
        firstOfT = Collections.unmodifiableSet(firstOfT);
    }

    @Override
    public RegularExpression parse(String s) {
        
        // create a PushbackReader from the given string
        PushbackReader reader = new PushbackReader(new StringReader(s), 10);
        
        RegularExpression regex = null;
        try {
            // parse and return the regular expression from the PushbackReader
            regex = parseR(reader);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString());
        }
        
        return regex;
    }
    
    private RegularExpression parseR(PushbackReader reader) throws IOException {
        
        // parse S
        RegularExpression re = parseS(reader);
        
        // pass the parsed regular expression on to the T parser
        re = parseT(re, reader);
        
        // return it
        return re;
    }
    
    private RegularExpression parseS(PushbackReader reader) throws IOException {
        
        // read the next character of input
        char next = (char)reader.read();
        
        // and immediately push it back
        reader.unread(next);
        
        // decide which right hand side to use based on the next input character
        RegularExpression re = null;
        switch (next) {
            case '0':
                re = parseByteLiteral(reader);
                break;
            case '(':
                re = parseGrouping(reader);
                break;
            default:
                logger.log(Level.SEVERE, "Read unexpected character: " + next);
                throw new MalformedInputException("Read unexpected character: " + next);
        }

        return re;
    }
    
    private RegularExpression parseT(RegularExpression re, PushbackReader reader) {
        // for now we'll just return the passed-in regular expression
        return re;
    }
    
    private RegularExpression parseByteLiteral(PushbackReader reader) throws IOException {
        
        // read the next four characters
        char[] str = new char[4];
        reader.read(str);
        
        // the first two characters must be '0x'
        if (str[0] != '0' || Character.toLowerCase(str[1]) != 'x') {
            String errorStr = "Attempted to parse a byte literal: expected '0x' but got '" + str[0] + str[1] + "'";
            logger.log(Level.SEVERE, errorStr);
            throw new MalformedInputException(errorStr);
        }
        
        // create a string from the char array
        String hexStr = new String(str);
        
        // try to decode the hex string
        int val;
        try {
            val = Integer.decode(hexStr);
        } catch (NumberFormatException nfe) {
            logger.log(Level.INFO, "Failed parsing a byte literal: " + nfe);
            throw new MalformedInputException("Failed to parse byte literal.");
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
            throw new MalformedInputException("Expected '(' but read '" + next + "'");
        }
        
        // parse the grouped regular expression
        RegularExpression re = parseR(reader);
        
        // make sure the next character is ')'
        next = (char)reader.read();
        if (next != ')') {
            throw new MalformedInputException("Expected ')' but read '" + next + "'");
        }
        
        return re;
    }
}
