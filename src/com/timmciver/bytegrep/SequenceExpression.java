
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

    @Override
    public String toString() {
        return expr1.toString() + expr2.toString();
    }
    
}
