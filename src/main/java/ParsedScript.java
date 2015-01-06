import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by const on 25.11.14.
 */
public class ParsedScript {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    private static final RepeatingReplacePattern PATTERN_MACRO = new RepeatingReplacePattern("(?m)^\\s*#.*?$", ' ');
    private static final RepeatingReplacePattern PATTERN_LINECOMMENT = new RepeatingReplacePattern("(?m)//.*?$", ' ');
    private static final RepeatingReplacePattern PATTERN_BLOCKCOMMENT = new RepeatingReplacePattern("/\\*[\\s\\S]*?\\*/", ' ');
    private static final RepeatingReplacePattern PATTERN_STRING = new RepeatingReplacePattern("([\"'])(?:\\\\\\\\|\\\\.|.)*?\\1", 'Z');
    private static final Pattern PATTERN_BLOCKSTART = Pattern.compile("[^;{]*[^;{\\s]*\\{");
    private static final Pattern PATTERN_BLOCKEND = Pattern.compile("\\}");
    private static final Pattern PATTERN_EXPRESSION = Pattern.compile("[\\s\\S]*?;");
    //    private static int BUFFER_SIZE = 1024;
    private final List<MatchResult> replacementList = new ArrayList<>();

    public ParsedScript(Path path) throws IOException {
        System.out.println("processing...");
        process(path);
/*        MatchThem(input,
                PATTERN_WHITESPACE,
                PATTERN_BLOCKSTART,
                PATTERN_BLOCKEND,
                PATTERN_EXPRESSION
        );*/
        System.out.println("complete");
    }

    public void process(Path path) throws IOException {
        String script = new String(Files.readAllBytes(path), "UTF-8");
        script = preprocessScript(script,
                PATTERN_STRING,
                PATTERN_MACRO,
                PATTERN_LINECOMMENT,
                PATTERN_BLOCKCOMMENT
        );
        process(script,
                PATTERN_WHITESPACE,
                PATTERN_BLOCKSTART,
                PATTERN_BLOCKEND,
                PATTERN_EXPRESSION
        );
    }

/*    private static boolean tryRead(Reader r, StringBuilder b)
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
    }*/

    private String preprocessScript(String script, RepeatingReplacePattern... patterns) {
        String processing = script;
        for (RepeatingReplacePattern pattern : patterns) {
            StringBuffer buffer = new StringBuffer();
            Matcher replacer = pattern.getPattern().matcher(processing);
            while (replacer.find()) {
                MatchResult match = replacer.toMatchResult();
                System.out.printf("Replaced at %d:%d (%s)\n", match.start(), match.end(), pattern.getPattern());
                replacementList.add(match);
                replacer.appendReplacement(buffer, pattern.getReplacementFor(match));
            }
            replacer.appendTail(buffer);
            processing = buffer.toString();
        }
        return processing;
    }

    private void process(String script, Pattern... patterns) {
        String processing = script;
        resetmatcher:
        while (!processing.isEmpty()) {
            for (Pattern p : patterns) {
                Matcher m = p.matcher(processing);
                if (m.lookingAt()) {
                    String match = processing.substring(0, m.end());
                    processing = processing.substring(m.end());
                    System.out.println("Found " + p);
                    System.out.println(match);
                    System.out.println("===");
                    continue resetmatcher;
                }
            }
            System.out.println("Nothing found");
            return;
        }
    }

/*    private static void MatchThem(Reader r, Pattern... patterns) {
        StringBuilder b = new StringBuilder();
        boolean notEOF = true;
        resetmatcher:
        while (notEOF) {
            for (Pattern p : patterns) {
                Matcher m = p.matcher(b);
                while (notEOF && m.lookingAt() && (m.hitEnd()/* || m.requireEnd()* /)) {
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
    }*/
}
