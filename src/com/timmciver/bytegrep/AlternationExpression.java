
package com.timmciver.bytegrep;

import java.util.List;

/**
 * A regular expression that matches if either of its two sub
 * expressions match.
 * @author tim
 */
public class AlternationExpression extends RegularExpression {
    
    private RegularExpression expr1;
    private RegularExpression expr2;

    public AlternationExpression(RegularExpression expr1, RegularExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public boolean match(byte[] data, int offset, List<Byte> matchedBytes) {
        
        if (expr1.match(data, offset, matchedBytes)) {
            return true;
        }
        
        return expr2.match(data, offset, matchedBytes);
    }

    public RegularExpression getFirstExpression() {
        return expr1;
    }

    public RegularExpression getSecondExpression() {
        return expr2;
    }

    @Override
    public String toString() {
        return expr1 + "|" + expr2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof AlternationExpression)) {
            return false;
        }
        
        AlternationExpression ae = (AlternationExpression)obj;
        
        // check the left expression of both AlternationExpressions
        if (!getFirstExpression().equals(ae.getFirstExpression())) {
            return false;
        }
        
        // check the right expression
        return getSecondExpression().equals(ae.getSecondExpression());
    }

    @Override
    public int hashCode() {
        return expr1.hashCode() + expr2.hashCode();
    }
    
}
