
package com.timmciver.bytegrep;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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
    public boolean internalMatch(InputStream in) {
        byte nextByte;
        try {
            nextByte = (byte)in.read();
        } catch (IOException ex) {
            Logger.getLogger(LiteralByte.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return (nextByte == literal) ? true : false;
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
