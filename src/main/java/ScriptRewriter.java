import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class ScriptRewriter implements IMatchListener {
    private static final PreprocessorReplacePattern PATTERN_MACRO = new PreprocessorReplacePattern("(?m)^\\s*#.*?$", ' ');
    private static final PreprocessorReplacePattern PATTERN_LINECOMMENT = new PreprocessorReplacePattern("(?m)//.*?$", ' ');
    private static final PreprocessorReplacePattern PATTERN_BLOCKCOMMENT = new PreprocessorReplacePattern("/\\*[\\s\\S]*?\\*/", ' ');
    private static final PreprocessorReplacePattern PATTERN_STRING = new PreprocessorReplacePattern("([\"'])(?:\\\\?.)*?\\1", 'Z');

    private static final ParserPattern PATTERN_IGNORED_TO_EOL =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_PREPROCESSED, Pattern.compile("(?m)\\s+$"));
    private static final ParserPattern PATTERN_INDENT =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_ORIGINAL, Pattern.compile("(?m)^[ \\t]+"));
    private static final ParserPattern PATTERN_IGNORED_LINE_BEGINNING =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_PREPROCESSED, Pattern.compile("(?m)^\\s+(?=\\S)"));
    private static final ParserPattern PATTERN_BLOCK_START =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_PREPROCESSED, Pattern.compile("[^;{\\s][^;{]*[^;{\\s]*\\{"));
    private static final ParserPattern PATTERN_BLOCK_END =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_PREPROCESSED, Pattern.compile("\\}"));
    private static final ParserPattern PATTERN_EXPRESSION =
            new ParserPattern(ParserPattern.AgainstWhat.AGAINST_PREPROCESSED, Pattern.compile("\\S[\\s\\S]*?;"));

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
                PATTERN_IGNORED_TO_EOL,
                PATTERN_INDENT,
                PATTERN_IGNORED_LINE_BEGINNING,
                PATTERN_BLOCK_START,
                PATTERN_BLOCK_END,
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
