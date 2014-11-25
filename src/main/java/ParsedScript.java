import java.io.*;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by const on 25.11.14.
 */
public class ParsedScript {
    private static int BUFFER_SIZE = 1024;

    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern PATTERN_MACRO = Pattern.compile("#.*");
    private static final Pattern PATTERN_LINECOMMENT = Pattern.compile("//.*");
    private static final Pattern PATTERN_BLOCKCOMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    private static final Pattern PATTERN_BLOCKSTART = Pattern.compile("[^;{]*[^;{\\s]*\\{");
    private static final Pattern PATTERN_BLOCKEND = Pattern.compile("\\}");

    //TODO: handle quotes
    private static final Pattern PATTERN_EXPRESSION = Pattern.compile("[\\s\\S]*?;");

    public ParsedScript(String path) throws IOException {
        this(new BufferedReader(new FileReader(path)));
    }

    public ParsedScript(Reader input) {
        MatchThem(input,
                PATTERN_WHITESPACE,
                PATTERN_MACRO,
                PATTERN_LINECOMMENT,
                PATTERN_BLOCKCOMMENT,
                PATTERN_BLOCKSTART,
                PATTERN_BLOCKEND,
                PATTERN_EXPRESSION
        );
    }

    private static boolean tryRead(Reader r, StringBuilder b)
    {
        try {
            char[] c = new char[BUFFER_SIZE];
            int size = r.read(c);

            if (size >  0) {
                System.out.println(size);
                b.append(c, 0, size);
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private static void MatchThem(Reader r, Pattern... patterns) {
        StringBuilder b = new StringBuilder();
        boolean notEOF = true;
        resetmatcher:
        while (notEOF) {
            for (Pattern p : patterns) {
                Matcher m = p.matcher(b);
                while (notEOF && !m.lookingAt() && m.hitEnd()) {
                    notEOF = tryRead(r, b);
                    m.reset(b);
                }
                if (m.lookingAt()) {
                    String match = b.substring(0, m.end());
                    b.delete(0, m.end());
                    System.out.println("Found " + p);
                    System.out.println(match);
                    System.out.println("===");
                    continue resetmatcher;
                }
            }
            return;
        }
    }
}
