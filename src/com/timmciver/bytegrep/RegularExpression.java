
package com.timmciver.bytegrep;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tim
 */
public abstract class RegularExpression {
    
    /**
     * Returns true if this RegularExpression matches the bytes in the
     * given InputStream, false otherwise.
     * @param in the InputStream to read bytes from
     * @return true if there's a match; false otherwise.
     */
    public final boolean match(InputStream in) {
        // mark the stream
        in.mark(Integer.MAX_VALUE);
        
        // call internalMatch
        boolean matched = internalMatch(in);
        
        // if it failed, reset
        if (!matched) {
            try {
                in.reset();
            } catch (IOException ex) {
                Logger.getLogger(RegularExpression.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return matched;
    }
    
    /**
     * Implemented by subclasses. Subclasses should not call mark() or reset()
     * in the InputStream (this has already been taken care of in the match()
     * method.
     * @param in the InputStream to read bytes from
     * @return true if there's a match; false otherwise.
     */
    protected abstract boolean internalMatch(InputStream in);
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
    
}
