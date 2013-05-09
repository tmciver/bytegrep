/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.timmciver.bytegrep.test.parser;

import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.SequenceExpression;
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
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression re = null;
        try {
            re = parser.parse(str);
        } catch (IOException ex) {
            fail();
        }
        
        // check the RegularExpression
        if (re == null) {
            fail();
        }
        
        if (!(re instanceof LiteralByte)) {
            fail();
        }
        
        LiteralByte lb = (LiteralByte)re;
        
        if (lb.getLiteralByte() != (byte)0xBA) {
            fail();
        }
    }
    
    @Test
    public void testParseByteLiteralSequence() {
        
        // create the string to be parsed
        String str = "0xAA0xAB0xAC";
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression re = null;
        try {
            re = parser.parse(str);
        } catch (IOException ex) {
            fail("Parse failed.");
        }
        
        // check the RegularExpression
        if (re == null) {
            fail("Parsed regex was null.");
        }
        
        if (!(re instanceof SequenceExpression)) {
            fail("regex was not a SequenceExpression");
        }
        
        // cast it
        SequenceExpression se = (SequenceExpression)re;
        
        // make sure the first expression is a LiteralByte
        if (!(se.getFirstExpression() instanceof LiteralByte)) {
            fail("First of the sequence was not a LiteralByte.");
        }
        
        // cast it to a LiteralByte
        LiteralByte literal1 = (LiteralByte)se.getFirstExpression();
        
        // make sure the second expression is a SequenceExpression
        if (!(se.getSecondExpression() instanceof SequenceExpression)) {
            fail("Second of the main sequence was not a SequenceExpression.");
        }
        
        // cast it
        SequenceExpression se2 = (SequenceExpression)se.getSecondExpression();
        
        // make sure its first expression is a LiteralByte
        if (!(se2.getFirstExpression() instanceof LiteralByte)) {
            fail("First of the second SequenceExpression was not a LiteralByte.");
        }
        
        // cast it
        LiteralByte literal2 = (LiteralByte)se2.getFirstExpression();
        
        // make sure its second expression is a LiteralByte
        if (!(se2.getSecondExpression() instanceof LiteralByte)) {
            fail("Final expression was not a LiteralByte.");
        }
        
        // cast it
        LiteralByte literal3 = (LiteralByte)se2.getSecondExpression();
        
        // make sure each literal is what we expect it to be
        if (literal1.getLiteralByte() != (byte)0xAA ||
                literal2.getLiteralByte() != (byte)0xAB ||
                literal3.getLiteralByte() != (byte)0xAC) {
            fail("One or more of the literal values was incorrect.");
        }
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
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression re = null;
        try {
            re = parser.parse(str);
        } catch (IOException ex) {
            fail();
        }
        
        // check the RegularExpression
        if (re == null) {
            fail();
        }
        
        if (!(re instanceof LiteralByte)) {
            fail();
        }
        
        LiteralByte lb = (LiteralByte)re;
        
        if (lb.getLiteralByte() != (byte)0xBA) {
            fail();
        }
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
}
