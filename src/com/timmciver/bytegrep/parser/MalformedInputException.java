
package com.timmciver.bytegrep.parser;

import java.io.IOException;

/**
 *
 * @author tim
 */
public class MalformedInputException extends IOException {

    public MalformedInputException(String message) {
        super(message);
    }

    public MalformedInputException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
