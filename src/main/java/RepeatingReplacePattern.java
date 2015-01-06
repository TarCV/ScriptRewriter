import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Created by const on 05.01.15.
 */
public class RepeatingReplacePattern {
    private final Pattern pattern;
    private final char replacer;

    public RepeatingReplacePattern(String pattern, char replacer) {
        this.pattern = Pattern.compile(pattern);
        this.replacer = replacer;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getReplacementFor(MatchResult result) {
        char[] replacementChars = new char[result.end() - result.start()];
        Arrays.fill(replacementChars, replacer);
        return new String(replacementChars);
    }
}
