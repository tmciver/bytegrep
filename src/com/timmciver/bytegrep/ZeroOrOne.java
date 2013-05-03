
package com.timmciver.bytegrep;

/**
 *
 * @author tim
 */
public class ZeroOrOne extends RepetitionExpression {

    public ZeroOrOne(RegularExpression expr) {
        super(expr, 0, 1);
    }
    
}
