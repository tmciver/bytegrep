/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.timmciver.bytegrep.test.parser;

import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.parser.DefaultParser;
import com.timmciver.bytegrep.parser.Parser;
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
        RegularExpression re = parser.parse(str);
        
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
    public void testParseGrouping() {
        
        // create the string to be parsed
        String str = "(0xBA)";
        
        // parse it
        Parser parser = new DefaultParser();
        RegularExpression re = parser.parse(str);
        
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
}
