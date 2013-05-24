
package com.timmciver.bytegrep;

import java.util.List;

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
     * @param matchedBytes a list of the bytes matched so far. Each
     * RegularExpression implementation should add the bytes that are matched to
     * this list.
     * @return true if the RegularExpression matched the input, false otherwise.
     */
    public abstract boolean match(byte[] data, int offset, List<Byte> matchedBytes);
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
    
}
