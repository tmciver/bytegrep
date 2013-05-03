
package com.timmciver.bytegrep;

/**
 *
 * @author tim
 */
public class ZeroOrMore extends RepetitionExpression {

    public ZeroOrMore(RegularExpression expr) {
        super(expr, 0, Integer.MAX_VALUE);
    }
    
}
