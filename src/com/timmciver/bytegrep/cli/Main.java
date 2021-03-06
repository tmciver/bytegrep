
package com.timmciver.bytegrep.cli;

import com.timmciver.bytegrep.RegularExpression;
import com.timmciver.bytegrep.parser.DefaultParser;
import com.timmciver.bytegrep.parser.Parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        
        // read data from the file
        File file = new File(filePath);
        byte[] data = new byte[(int)file.length()];
        InputStream in = new FileInputStream(file);
        in.read(data);
        
        // try matching at every byte
        //int numMatched = 0;
        //int byteVal;
        int offset;
        boolean matched = false;
        List<Byte> matchedBytes = new ArrayList<>();
        for (offset = 0; offset < data.length; ++offset) {
            if ((matched = re.match(data, offset, matchedBytes))) {
                break;
            }
            matchedBytes.clear();
        }
        
        // tell user if we found a match or not
        if (matched) {
            System.out.println("Found match at byte offset " + offset);
        } else {
            System.out.println("No match found.");
        }
    }
    
}
