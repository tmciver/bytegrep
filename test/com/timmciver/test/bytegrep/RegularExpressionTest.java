/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.timmciver.test.bytegrep;

import com.timmciver.bytegrep.AlternationExpression;
import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.OneOrMore;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.RepetitionExpression;
import com.timmciver.bytegrep.SequenceExpression;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author tim
 */
public class RegularExpressionTest {
    
    public RegularExpressionTest() {
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
    public void testLiteralByte() {
        
        byte literal = (byte)0x82;
        
        // create a ByteArrayInputStream
        byte[] data = new byte[]{literal, (byte)0x8F, (byte)0x25};
        InputStream in = new ByteArrayInputStream(data);
        
        // create the regular expression
        RegularExpression re = new LiteralByte(literal);
        
        System.out.println("Literal: " + re);
        
        // the first byte should match
        Assert.assertTrue(re.match(in));

        
        // the second byte should not match
        Assert.assertFalse(re.match(in));
    }
    
    @Test
    public void testAlternation() {
        
        // create a couple of literal bytes
        LiteralByte literal1 = new LiteralByte((byte)0xAA);
        LiteralByte literal2 = new LiteralByte((byte)0xAB);
        
        // create an AlternationExpression from them
        RegularExpression ae = new AlternationExpression(literal1, literal2);
        
        System.out.println("Alternation: " + ae);
        
        // create a ByteArrayInputStream whose first byte will match literal1
        InputStream in1 = new ByteArrayInputStream(new byte[]{literal1.getLiteralByte(), (byte)0x8F, (byte)0x25});
        
        // create a ByteArrayInputStream whose first byte will match literal2
        InputStream in2 = new ByteArrayInputStream(new byte[]{literal2.getLiteralByte(), (byte)0x8F, (byte)0x25});
        
        // finally create a ByteArrayInputStream that will not match
        InputStream in3 = new ByteArrayInputStream(new byte[]{(byte)0xAC, (byte)0x8F, (byte)0x25});
        
        // verify that ae matches the first byte of in1
        Assert.assertTrue(ae.match(in1));
        
        // verify that it does NOT match the second byte
        Assert.assertFalse(ae.match(in1));
        
        // verify that ae matches the first byte of in2
        Assert.assertTrue(ae.match(in2));
        
        // verify that it does NOT match the second byte
        Assert.assertFalse(ae.match(in2));
        
        // verify that it does not match in3
        Assert.assertFalse(ae.match(in3));
    }
    
    @Test
    public void testSequence() {
        
        // define 3 byte literals
        LiteralByte literal1 = new LiteralByte((byte)0xAA);
        LiteralByte literal2 = new LiteralByte((byte)0xAB);
        LiteralByte literal3 = new LiteralByte((byte)0xAC);
        
        // create a SequenceExpression from them
        RegularExpression re2 = new SequenceExpression(literal2, literal3);
        RegularExpression seq = new SequenceExpression(literal1, re2);
        
        System.out.println("Sequence: " + seq);
        
        // create a ByteArrayInputStream from these byte literals
        InputStream in1 = new ByteArrayInputStream(new byte[]{literal1.getLiteralByte(), literal2.getLiteralByte(), literal3.getLiteralByte()});
        
        // create another ByteArrayInputStream with non-matching data
        InputStream in2 = new ByteArrayInputStream(new byte[]{(byte)0xAC, (byte)0x8F, (byte)0x25});
        
        // seq should match in1
        Assert.assertTrue(seq.match(in1));
        
        // seq should not match in2
        Assert.assertFalse(seq.match(in2));
    }
    
    @Test
    public void testRepetition() {
        
        // create a couple of literal bytes
        LiteralByte literal = new LiteralByte((byte)0xAA);
        
        // create a RepetitionExpression from it
        RegularExpression re = new OneOrMore(literal);
        
        System.out.println("Repetition: " + re);
        
        // create a ByteArrayInputStream whose first byte will match the repetition
        InputStream in1 = new ByteArrayInputStream(new byte[]{literal.getLiteralByte(), (byte)0x8F, (byte)0x25});
        
        // create a ByteArrayInputStream all of whose bytes are the literal
        InputStream in2 = new ByteArrayInputStream(new byte[]{literal.getLiteralByte(), literal.getLiteralByte(), literal.getLiteralByte()});
        
        // finally create a ByteArrayInputStream that will not match
        InputStream in3 = new ByteArrayInputStream(new byte[]{(byte)0xAC, (byte)0x8F, (byte)0x25});
        
        // verify that ae matches the first byte of in1
        Assert.assertTrue(re.match(in1));
        
        // verify that it does NOT match the second byte
        Assert.assertFalse(re.match(in1));
        
        // verify that re matches in2
        Assert.assertTrue(re.match(in2));
        
        // verify that it does not match in3
        Assert.assertFalse(re.match(in3));
    }
}
