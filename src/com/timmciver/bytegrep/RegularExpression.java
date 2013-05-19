
package com.timmciver.bytegrep;

/**
 * Parent class for all regular expressions.
 * @author tim
 */
public abstract class RegularExpression {
    
    /**
     * Returns the number of bytes matched.
     * @param data input byte array
     * @param offset the byte offset into the data array at which matching
     * should begin
     * @return the number of bytes matched
     */
    public abstract int match(byte[] data, int offset);
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
    
}
