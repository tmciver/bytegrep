
package com.timmciver.bytegrep;

import java.util.List;

/**
 * A regular expression to match a single byte.
 * @author tim
 */
public class LiteralByte extends RegularExpression {
    
    private byte literal;

    public LiteralByte(byte literal) {
        this.literal = literal;
    }
    
    public LiteralByte(int val) {
        this((byte)val);
    }

    @Override
    public boolean match(byte[] data, int offset, List<Byte> matchedBytes) {
        
        if (offset >= data.length) {
            return false;
        }
        
        boolean matched = data[offset] == literal;
        
        if (matched) {
            matchedBytes.add(literal);
        }
        
        return matched;
    }

    public byte getLiteralByte() {
        return literal;
    }

    @Override
    public String toString() {
        return String.format("0x%2X", literal);
    }

    @Override
    public int hashCode() {
        return 47 * literal + 113;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof LiteralByte)) {
            return false;
        }
        
        LiteralByte otherLiteral = (LiteralByte)obj;
        
        if (literal != otherLiteral.literal) {
            return false;
        }
        
        return true;
    }
    
}
