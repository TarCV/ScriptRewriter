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
    private final List<MatchResult> replacementList = new ArrayList<>();
    private final List<IMatchListener> matchListenerList = new ArrayList<>();
    private List<RepeatingReplacePattern> preprocessorPatterns = new ArrayList<>();
    private List<Pattern> parserPatterns = new ArrayList<>();

    private String script = null;

    public ScriptParser(Path path) throws IOException {
        script = new String(Files.readAllBytes(path), "UTF-8");
    }

    public ScriptParser setPreprocessorPatterns(List<RepeatingReplacePattern> patterns) {
        if (!replacementList.isEmpty()) {
            throw new RuntimeException("Preprocessor will not be called again, no need to setup it");
        }

        preprocessorPatterns = patterns;
        return this;
    }

    public ScriptParser setParserPatterns(List<Pattern> patterns) {
        parserPatterns = patterns;
        return this;
    }

    public void parse() {
        if (replacementList.isEmpty()) {
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

    private String preprocessScript() {
        assert (replacementList.isEmpty());

        String processing = script;
        for (RepeatingReplacePattern pattern : preprocessorPatterns) {
            StringBuffer buffer = new StringBuffer();
            Matcher replacer = pattern.getPattern().matcher(processing);
            while (replacer.find()) {
                MatchResult match = replacer.toMatchResult();
                replacementList.add(match);
                replacer.appendReplacement(buffer, pattern.getReplacementFor(match));
            }
            replacer.appendTail(buffer);
            processing = buffer.toString();
        }
        return processing;
    }

    private void doParse() {
        String processing = script;
        resetmatcher:
        while (!processing.isEmpty()) {
            for (Pattern p : parserPatterns) {
                Matcher m = p.matcher(processing);
                if (m.lookingAt()) {
                    String matched = processing.substring(0, m.end());
                    processing = processing.substring(m.end());
                    callMatchListeners(new Match(p, matched));
                    continue resetmatcher;
                }
            }
            //TODO: call event listener for "nothing found" error
            return;
        }
    }
}
