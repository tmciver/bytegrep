
package com.timmciver.bytegrep;

import java.io.InputStream;

/**
 *
 * @author tim
 */
public class SequenceExpression extends RegularExpression {
    
    private RegularExpression expr1;
    private RegularExpression expr2;

    public SequenceExpression(RegularExpression expr1, RegularExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    protected boolean internalMatch(InputStream in) {
        return expr1.match(in) && expr2.match(in);
    }

    public RegularExpression getFirstExpression() {
        return expr1;
    }

    public RegularExpression getSecondExpression() {
        return expr2;
    }

    @Override
    public String toString() {
        return expr1.toString() + expr2.toString();
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof SequenceExpression)) {
            return false;
        }
        
        SequenceExpression se = (SequenceExpression)obj;
        
        // check the left expression of both SequenceExpressions
        if (!getFirstExpression().equals(se.getFirstExpression())) {
            return false;
        }
        
        // check the right expression
        return getSecondExpression().equals(se.getSecondExpression());
    }

    @Override
    public int hashCode() {
        return 43 * expr1.hashCode() + 11 * expr2.hashCode();
    }
    
}
