
package com.timmciver.bytegrep;

/**
 *
 * @author tim
 */
public class OneOrMore extends RepetitionExpression {

    public OneOrMore(RegularExpression expr) {
        super(expr, 1, Integer.MAX_VALUE);
    }
    
}
