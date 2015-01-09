import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class ScriptRewriter implements IMatchListener {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    private static final RepeatingReplacePattern PATTERN_MACRO = new RepeatingReplacePattern("(?m)^\\s*#.*?$", ' ');
    private static final RepeatingReplacePattern PATTERN_LINECOMMENT = new RepeatingReplacePattern("(?m)//.*?$", ' ');
    private static final RepeatingReplacePattern PATTERN_BLOCKCOMMENT = new RepeatingReplacePattern("/\\*[\\s\\S]*?\\*/", ' ');
    private static final RepeatingReplacePattern PATTERN_STRING = new RepeatingReplacePattern("([\"'])(?:\\\\?.)*?\\1", 'Z');
    private static final Pattern PATTERN_BLOCKSTART = Pattern.compile("[^;{\\s][^;{]*[^;{\\s]*\\{");
    private static final Pattern PATTERN_BLOCKEND = Pattern.compile("\\}");
    private static final Pattern PATTERN_EXPRESSION = Pattern.compile("\\S[\\s\\S]*?;");

    private final Path path;

    public ScriptRewriter(Path path) {
        this.path = path;
    }

    public static void main(String[] args) throws IOException {
        Path path = FileSystems.getDefault().getPath(args[0]);
        ScriptRewriter rewriter = new ScriptRewriter(path);
        System.out.println("processing...");
        rewriter.rewrite();
        System.out.println("complete");
    }

    public void rewrite() throws IOException {
        ScriptParser parser = new ScriptParser(path);

        parser.setPreprocessorPatterns(Arrays.asList(
                PATTERN_STRING,
                PATTERN_MACRO,
                PATTERN_LINECOMMENT,
                PATTERN_BLOCKCOMMENT
        ));

        parser.setParserPatterns(Arrays.asList(
                PATTERN_WHITESPACE,
                PATTERN_BLOCKSTART,
                PATTERN_BLOCKEND,
                PATTERN_EXPRESSION
        ));

        parser.addMatchListener(this);

        parser.parse();
    }

    @Override
    public void gotMatch(Match match) {
        System.out.println("Found " + match.getPattern());
        System.out.println(match.getPreprocessedFragment());
        System.out.println("===");
    }
}
