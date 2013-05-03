
package com.timmciver.bytegrep;

import java.io.InputStream;

/**
 *
 * @author tim
 */
public class RepetitionExpression extends RegularExpression {
    
    private RegularExpression expr;
    private int minMatches;
    private int maxMatches;

    /**
     * Creates a RegularExpression that can repeat (like with * or
     * @param expr
     * @param minMatches 
     */
    public RepetitionExpression(RegularExpression expr, int minMatches, int maxMatches) {
        this.expr = expr;
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
        
        // check that minMatches is either zero or one
        if (minMatches != 0 && minMatches != 1) {
            throw new IllegalArgumentException("minMatches can only be zero or one, currently.");
        }
        
        // make sure tha minMatche < maxMatches
        if (minMatches >= maxMatches) {
            throw new IllegalArgumentException("minMatches must be less than maxMatches.");
        }
    }

    @Override
    protected boolean internalMatch(InputStream in) {
        
        // a) If the first one doesn't match and minMatches is equal to zero, it's
        // a match. b) If the first one is a match and maxMatches is equal to
        // one, then it's a match. c) If minMatches is greater than zero, then
        // it's not a match.
        boolean matched = expr.match(in);
        if (!matched && minMatches == 0) {
            return true;
        } else if (matched && maxMatches == 1) {
            return true;
        } else if (!matched) {
            return false;
        }
        
        // consume input while there's a match
        while (matched) {
            matched = expr.match(in);
        }
        
        // it's a match no matter what now
        return true;
    }
    
}
