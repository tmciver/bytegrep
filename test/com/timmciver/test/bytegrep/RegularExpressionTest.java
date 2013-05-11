/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.timmciver.test.bytegrep;

import com.timmciver.bytegrep.LiteralByte;
import com.timmciver.bytegrep.OneOrMore;
import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.RepetitionExpression;
import com.timmciver.bytegrep.AlternationExpression;
import com.timmciver.bytegrep.SequenceExpression;
import com.timmciver.bytegrep.ZeroOrMore;
import com.timmciver.bytegrep.ZeroOrOne;
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
    
    @Test
    public void testLiteralByteEquals() {
        
        // create some literals to test
        RegularExpression literal1 = new LiteralByte((byte)0xAA);
        RegularExpression literal2 = new LiteralByte((byte)0xAA);
        RegularExpression literal3 = new LiteralByte((byte)0xAB);
        
        // and some other RegularExpression subtype
        RegularExpression re = new AlternationExpression(literal1, literal3);
        
        // an object should be equal to itself
        Assert.assertTrue(literal1.equals(literal1));
        
        // literal1 and literal2 should be equal
        Assert.assertTrue(literal1.equals(literal2));
        
        // literal1 and literal3 should not be equal
        Assert.assertFalse(literal1.equals(literal3));
        
        // literal1 and re should not be equal
        Assert.assertFalse(literal1.equals(re));
    }
    
    @Test
    public void testSequenceExpressionEquals() {
        
        // create some LiteralBytes
        RegularExpression literal1 = new LiteralByte(0xAA);
        RegularExpression literal2 = new LiteralByte(0xAA);
        RegularExpression literal3 = new LiteralByte(0xAB);
        RegularExpression literal4 = new LiteralByte(0xAB);
        
        // create some SequenceExpressions from them
        RegularExpression seq1 = new SequenceExpression(literal1, literal3);
        RegularExpression seq2 = new SequenceExpression(literal1, literal3);
        RegularExpression seq3 = new SequenceExpression(literal2, literal4);
        RegularExpression seq4 = new SequenceExpression(literal1, literal1);
        
        Assert.assertTrue(seq1.equals(seq1));
        Assert.assertTrue(seq1.equals(seq2));
        Assert.assertTrue(seq1.equals(seq3));
        Assert.assertFalse(seq1.equals(seq4));
    }
    
    @Test
    public void testRepetitionExpressionEquals() {
        
        // create some LiteralBytes
        RegularExpression literal1 = new LiteralByte(0xAA);
        RegularExpression literal2 = new LiteralByte(0xAB);
        
        // create some RepetitionExpressions to test
        RegularExpression zeroOrMore1 = new RepetitionExpression(literal1, 0, Integer.MAX_VALUE);
        RegularExpression zeroOrMore2 = new ZeroOrMore(literal1);
        RegularExpression zeroOrMore3 = new ZeroOrMore(literal2);
        RegularExpression oneOrMore1 = new RepetitionExpression(literal1, 1, Integer.MAX_VALUE);
        RegularExpression oneOrMore2 = new OneOrMore(literal1);
        RegularExpression zeroOrOne1 = new RepetitionExpression(literal1, 0, 1);
        RegularExpression zeroOrOne2 = new ZeroOrOne(literal1);
        
        // do tests
        Assert.assertTrue(zeroOrMore1.equals(zeroOrMore1));
        Assert.assertTrue(zeroOrMore1.equals(zeroOrMore2));
        Assert.assertTrue(zeroOrMore2.equals(zeroOrMore1));
        Assert.assertFalse(zeroOrMore1.equals(zeroOrMore3));
        Assert.assertTrue(oneOrMore1.equals(oneOrMore2));
        Assert.assertTrue(oneOrMore2.equals(oneOrMore1));
        Assert.assertFalse(oneOrMore1.equals(zeroOrMore2));
        Assert.assertTrue(zeroOrOne1.equals(zeroOrOne2));
        Assert.assertTrue(zeroOrOne2.equals(zeroOrOne1));
        Assert.assertFalse(zeroOrOne1.equals(zeroOrMore1));
        Assert.assertFalse(zeroOrOne1.equals(oneOrMore1));
    }
    
    @Test
    public void testAlternationExpressionEquals() {
        
        // create some LiteralBytes
        RegularExpression literal1 = new LiteralByte(0xAA);
        RegularExpression literal2 = new LiteralByte(0xAA);
        RegularExpression literal3 = new LiteralByte(0xAB);
        RegularExpression literal4 = new LiteralByte(0xAB);
        
        // create some AlternationExpressions from them
        RegularExpression alt1 = new AlternationExpression(literal1, literal3);
        RegularExpression alt2 = new AlternationExpression(literal1, literal3);
        RegularExpression alt3 = new AlternationExpression(literal2, literal4);
        RegularExpression alt4 = new AlternationExpression(literal1, literal1);
        
        Assert.assertTrue(alt1.equals(alt1));
        Assert.assertTrue(alt1.equals(alt2));
        Assert.assertTrue(alt1.equals(alt3));
        Assert.assertFalse(alt1.equals(alt4));
    }
}
