
package com.timmciver.bytegrep.parser;

import com.timmciver.bytegrep.RegularExpression;
import java.io.IOException;

/**
 *
 * @author tim
 */
public interface Parser {
    
    /**
     * An interface for parsing a text string into a RegularExpression.
     * @param s the string to parse
     * @return the RegularExpression
     */
    RegularExpression parse(String s) throws IOException;
    
}
