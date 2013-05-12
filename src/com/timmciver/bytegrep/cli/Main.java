
package com.timmciver.bytegrep.cli;

import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.parser.DefaultParser;
import com.timmciver.bytegrep.parser.Parser;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author tim
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        
        // two args are required; first is the regular expression, second is the
        // path to a file to search
        if (args.length != 2) {
            System.err.println("usage: java -jar <path-to-jar> <regexp-str> <path-to-file>");
            System.exit(1);
        }
        
        // pull out the args
        String regexStr = args[0];
        String filePath = args[1];
        
        // try to parse the regex string
        Parser parser = new DefaultParser();
        RegularExpression re = null;
        try {
            re = parser.parse(regexStr);
        } catch (IOException ex) {
            System.err.println("Could not parse regex: " + ex);
            System.exit(1);
        }
        
        // create a FileInputStream from the given file path
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        
        // try matching at every byte
        long index = -1;
        boolean matched;
        int byteVal;
        do {
            ++index;
            matched = re.match(in);
            byteVal = in.read();
            //System.out.println("Read byte: " + byteVal);
        } while (!matched && byteVal != -1);
        
        // tell user if we found a match or not
        if (matched) {
            System.out.println("Found match at byte offset " + Long.toHexString(index));
        } else {
            System.out.println("No match found.");
        }
    }
    
}
