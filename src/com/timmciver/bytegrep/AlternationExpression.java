
package com.timmciver.bytegrep;

import java.io.InputStream;

/**
 *
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
    public boolean internalMatch(InputStream in) {
        return expr1.match(in) || expr2.match(in);
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
