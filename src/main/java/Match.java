import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class Match {
    private final Pattern pattern;
    private final String match;

    public Match(Pattern pattern, String match) {

        this.pattern = pattern;
        this.match = match;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getMatch() {
        return match;
    }
}
