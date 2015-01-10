import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class ScriptParser {
    private final List<IMatchListener> matchListenerList = new ArrayList<>();
    private final String originalScript;
    private List<PreprocessorReplacePattern> preprocessorPatterns = new ArrayList<>();
    private List<ParserPattern> parserPatterns = new ArrayList<>();
    private String preprocessedScript = null;
    private boolean preprocessorCalled = false;

    public ScriptParser(Path path) throws IOException {
        originalScript = preprocessedScript = new String(Files.readAllBytes(path), "UTF-8");
    }

    public ScriptParser setPreprocessorPatterns(List<PreprocessorReplacePattern> patterns) {
        if (preprocessorCalled) {
            throw new RuntimeException("Preprocessor will not be called again, no need to setup it");
        }

        preprocessorPatterns = patterns;
        return this;
    }

    public ScriptParser setParserPatterns(List<ParserPattern> patterns) {
        parserPatterns = patterns;
        return this;
    }

    public void parse() {
        if (!preprocessorCalled) {
            preprocessScript();
        }
        doParse();
    }

    public void addMatchListener(IMatchListener listener)
    {
        matchListenerList.add(listener);
    }

    protected void callMatchListeners(Match match) {
        for (IMatchListener listener : matchListenerList)
        {
            listener.gotMatch(match);
        }
    }

    private void preprocessScript() {
        assert (!preprocessorCalled);
        preprocessorCalled = true;

        String processing = preprocessedScript;
        for (PreprocessorReplacePattern pattern : preprocessorPatterns) {
            StringBuffer buffer = new StringBuffer();
            Matcher replacer = pattern.getPattern().matcher(processing);
            while (replacer.find()) {
                MatchResult match = replacer.toMatchResult();

                assert (match.group().length() == pattern.getReplacementFor(match).length());
                replacer.appendReplacement(buffer, pattern.getReplacementFor(match));
            }
            replacer.appendTail(buffer);
            processing = buffer.toString();
        }
        preprocessedScript = processing;
    }

    private void doParse() {
        int begin = 0;

        resetmatcher:
        while (begin != preprocessedScript.length()) {
            for (ParserPattern pattern : parserPatterns) {
                Pattern regex = pattern.getPattern();
                Matcher m;

                if (ParserPattern.AgainstWhat.AGAINST_ORIGINAL == pattern.getAgainstWhat()) {
                    m = regex.matcher(originalScript);
                } else if (ParserPattern.AgainstWhat.AGAINST_PREPROCESSED == pattern.getAgainstWhat()) {
                    m = regex.matcher(preprocessedScript);
                } else {
                    throw new RuntimeException("Invalid program state");
                }

                m.region(begin, m.regionEnd());

                if (m.lookingAt()) {
                    String originalFragment = originalScript.substring(begin, m.end());
                    String preprocessedFragment = preprocessedScript.substring(begin, m.end());
                    callMatchListeners(new Match(pattern, originalFragment, preprocessedFragment));

                    begin = m.end();
                    continue resetmatcher;
                }
            }

            //TODO: call event listener for "nothing found" error
            return;
        }
    }
}
