
package com.timmciver.bytegrep;

/**
 * A regular expression that matches if the given regular expression
 * matches a number of times between minMatches and maxMatches
 * inclusive.
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
    public int match(byte[] data, int offset) {
        
        // a) If the first one doesn't match and minMatches is equal to zero, it's
        // a match. b) If the first one is a match and maxMatches is equal to
        // one, then it's a match. c) If minMatches is greater than zero, then
        // it's not a match.
        int numMatches = expr.match(data, offset);
        boolean matched = numMatches > 0;
        if (!matched && minMatches == 0) {
            return numMatches;
        } else if (matched && maxMatches == 1) {
            return numMatches;
        } else if (!matched) {
            return 0;
        }
        
        // consume input while there's a match
        int previousMatches = 0;
        while (numMatches > previousMatches) {
            previousMatches = numMatches;
            numMatches += expr.match(data, offset + numMatches);
        }
        
        // it's a match no matter what now
        return numMatches;
    }

    @Override
    public String toString() {
        char repChar = '*';
        if (minMatches == 0 && maxMatches == 1) {
            repChar = '?';
        } else if (minMatches == 1) {
            repChar = '+';
        }
        return "(" + expr + ")" + repChar;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof RepetitionExpression)) {
            return false;
        }
        
        RepetitionExpression re = (RepetitionExpression)obj;
        
        // check min and max matches
        if (minMatches != re.minMatches ||
                maxMatches != re.maxMatches) {
            return false;
        }
        
        // check the right expression
        return expr.equals(re.expr);
    }

    @Override
    public int hashCode() {
        return expr.hashCode() + minMatches + maxMatches;
    }
    
}
