
package com.timmciver.bytegrep.test.parser;

import com.timmciver.bytegrep.AlternationExpression;
import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.SequenceExpression;
import com.timmciver.bytegrep.ZeroOrMore;
import com.timmciver.bytegrep.parser.DefaultParser;
import com.timmciver.bytegrep.parser.MalformedInputException;
import com.timmciver.bytegrep.parser.Parser;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tim
 */
public class DefaultParserTest {
    
    public DefaultParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testParseByteLiteral() {
        
        // create the string to be parsed
        String str = "0xBA";
        
        RegularExpression expected = new LiteralByte(0xBA);
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression actual = null;
        try {
            actual = parser.parse(str);
        } catch (IOException ex) {
            fail();
        }
        
        assertTrue(actual.equals(expected));
    }
    
    @Test
    public void testParseByteLiteralSequence() {
        
        // create the string to be parsed
        String str = "0xAA0xAB0xAC";
        
        // build the expexted RegularExpression
        LiteralByte literal1 = new LiteralByte((byte)0xAA);
        LiteralByte literal2 = new LiteralByte((byte)0xAB);
        LiteralByte literal3 = new LiteralByte((byte)0xAC);
        
        // create a SequenceExpression from them
        RegularExpression re2 = new SequenceExpression(literal2, literal3);
        RegularExpression exptected = new SequenceExpression(literal1, re2);
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression actual = null;
        try {
            actual = parser.parse(str);
        } catch (IOException ex) {
            fail("Parse failed.");
        }
        
        assertTrue(actual.equals(exptected));

    }
    
    @Test
    public void testMalformedByteLiteral() {
        
        // create a malformed string to be parsed
        String str = "0xBG";
        
        // create the parser
        Parser parser = new DefaultParser();
        
        // try parsing it
        try {
            RegularExpression re = parser.parse(str);
            fail();
        } catch (IOException ex) {
            if (!(ex instanceof MalformedInputException)) {
                fail();
            }
        }
        
        // again with a different malformed string
        str = "0zAB";
        
        // try parsing it
        try {
            RegularExpression re = parser.parse(str);
            fail();
        } catch (IOException ex) {
            if (!(ex instanceof MalformedInputException)) {
                fail();
            }
        }
    }
    
    @Test
    public void testParseGrouping() {
        
        // create the string to be parsed
        String str = "(0xBA)";
        
        RegularExpression exptected = new LiteralByte(0xBA);
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression actual = null;
        try {
            actual = parser.parse(str);
        } catch (IOException ex) {
            fail();
        }
        
        assertTrue(actual.equals(exptected));
    }
    
    @Test
    public void testParseMalformedGrouping() {
        
        // create a malformed string to be parsed
        String str = "0xBA)";
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression re = null;
        try {
            re = parser.parse(str);
            fail();
        } catch (IOException ex) {
            if (!(ex instanceof MalformedInputException)) {
                fail();
            }
        }
        
        // again with a differenct malformed string
        str = "(0xBA";
        
        try {
            re = parser.parse(str);
            fail();
        } catch (IOException ex) {
            if (!(ex instanceof MalformedInputException)) {
                fail();
            }
        }
    }
    
    @Test
    public void testAlternationSequence() {
        
        // create the string to be parsed
        String str = "0xAA|0xAB";
        
        // build the expexted RegularExpression
        LiteralByte literal1 = new LiteralByte((byte)0xAA);
        LiteralByte literal2 = new LiteralByte((byte)0xAB);
        
        // create a SequenceExpression from them
        RegularExpression exptected = new AlternationExpression(literal1, literal2);
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression actual = null;
        try {
            actual = parser.parse(str);
        } catch (IOException ex) {
            fail("Parse failed.");
        }
        
        assertTrue(actual.equals(exptected));
    }
    
    @Test
    public void testZeroOrMore() {
        
        // create the string to be parsed
        String str = "0xAA*";
        
        // build the expexted RegularExpression
        LiteralByte literal1 = new LiteralByte((byte)0xAA);
        
        // create a SequenceExpression from them
        RegularExpression exptected = new ZeroOrMore(literal1);
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression actual = null;
        try {
            actual = parser.parse(str);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        
        assertTrue(actual.equals(exptected));
    }
}
