
package com.timmciver.bytegrep.parser;

import com.timmciver.bytegrep.AlternationExpression;
import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.OneOrMore;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.SequenceExpression;
import com.timmciver.bytegrep.ZeroOrMore;
import com.timmciver.bytegrep.ZeroOrOne;
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
 * R ::= [byte-literal]
 *     | (R)              // grouping
 *     | RR               // sequence
 *     | R|R              // alternation
 *     | R*T              // zero or more
 *     | R+T              // one or more
 *     | R?T              // zero or one
 * 
 * T ::= R
 *     | epsilon
 *
 * [byte-literal] ::= 0xXY  // defines a single byte where X and Y represent
 *                          // hexadecimal digits.
 * 
 * The above grammar is left recursive.  DefaultParser is a predictive recursive
 * descent parser which cannot handle a left recursive grammar.  The following
 * grammar is equivalent to the above grammar with the left recursion removed.
 * 
 * S ::= R$                // start symbol; R followed by end-of-input
 * 
 * R ::= [byte-literal]T
 *     | (R)T
 * 
 * T ::= RT                // sequence
 *     | |RT               // alternation
 *     | *T                // zero or more
 *     | +T                // one or more
 *     | ?T                // zero or one
 *     | epsilon
 * 
 * [byte-literal]         // as defined above
 * 
 * @author tim
 */
public class DefaultParser implements Parser {
    
    private final static Logger logger = Logger.getLogger(DefaultParser.class.getName());

    private Set<Character> firstOfR;
    private Set<Character> followOfR;

    public DefaultParser() {
        
        // initialize firstOfR
        firstOfR = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList('0', '(')));
        
        // and followOfR
        followOfR = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(')')));
    }
    
    @Override
    public RegularExpression parse(String s) throws IOException {
        
        // create a PushbackReader from the given string
        PushbackReader reader = new PushbackReader(new StringReader(s));

        return parseS(reader);
    }

    private RegularExpression parseS(PushbackReader reader) throws IOException {
        
        RegularExpression re = parseR(reader);
        
        // it's an error if we're not at the end of the stream here
        int next = reader.read();
        if (next != -1) {
            throw new MalformedInputException("Expected EOF but found next character: " + (char)next);
        }
        
        return re;
    }
    
    private RegularExpression parseR(PushbackReader reader) throws IOException {
        
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
        
        // now parse T
        re = parseT(re, reader);

        return re;
    }
    
    private RegularExpression parseT(RegularExpression inRegex, PushbackReader reader) throws IOException {
        
        // read the next character of input
        int next = reader.read();
        char nextChar = (char)next;
        
        // check for end-of-input
        if (next == -1) {
            // end of input; choose epsilon production; we're done
            return inRegex;
        }
        
        RegularExpression outRegex = null;
        
        // which T production to use?
        if (firstOfR.contains(nextChar)) {
            // we've got another R
            // push back the read character so that it can be read by parseR
            reader.unread(next);
            RegularExpression fromR = parseR(reader);
            outRegex = new SequenceExpression(inRegex, fromR);
            logger.log(Level.INFO, "Parsed regular expression: " + outRegex);
        } else if (nextChar == '|') {
            // alternation
            RegularExpression fromR = parseR(reader);
            outRegex = new AlternationExpression(inRegex, fromR);
            logger.log(Level.INFO, "Parsed alternation regular expression: " + outRegex);
        } else if (nextChar == '*') {
            // zero or more
            outRegex = new ZeroOrMore(inRegex);
            logger.log(Level.INFO, "Parsed zero or more regular expression: " + outRegex);
        } else if (nextChar == '+') {
            // one or more
            outRegex = new OneOrMore(inRegex);
            logger.log(Level.INFO, "Parsed one or more regular expression: " + outRegex);
        } else if (nextChar == '?') {
            // zero or one
            outRegex = new ZeroOrOne(inRegex);
            logger.log(Level.INFO, "Parsed zero or one regular expression: " + outRegex);
        } else {
            // it's an error if nextChar is not in followOfR
            if (!followOfR.contains(nextChar)) {
                throw new MalformedInputException("Read unexpected character: " + nextChar);
            }
            
            // push back the read character so that it can be read by parseR
            reader.unread(next);

            // since the next character is in follow(R), we must choose the
            // epsilon production of T
            return inRegex;
        }
        
        // The T productions are right recursive (except for the epsilon
        // transition which has already been accoutned for).
        return parseT(outRegex, reader);
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
        
        RegularExpression byteLiteral = new LiteralByte(byteVal);
        
        // log it
        logger.log(Level.INFO, "Created byte literal regular expression: " + byteLiteral);
        
        // return a LiteralByte regex
        return byteLiteral;
    }
    
    private RegularExpression parseGrouping(PushbackReader reader) throws IOException {
        
        // make sure the next character is '('
        char next = (char)reader.read();
        if (next != '(') {
            throw new MalformedInputException("Expected '(' but read '" + next + "'");
        }
        
        // log start of group
        logger.log(Level.INFO, "Start parsing regular expression grouping.");
        
        // parse the grouped regular expression
        RegularExpression re = parseR(reader);
        
        // make sure the next character is ')'
        next = (char)reader.read();
        if (next != ')') {
            throw new MalformedInputException("Expected ')' but read '" + next + "'");
        }
        
        // log end of group
        logger.log(Level.INFO, "Finished parsing regular expression grouping.");
        
        return re;
    }
}
