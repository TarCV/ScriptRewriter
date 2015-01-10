import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class ParserPattern {
    private final AgainstWhat againstWhat;
    private final Pattern pattern;

    public ParserPattern(AgainstWhat againstWhat, Pattern pattern) {
        this.againstWhat = againstWhat;
        this.pattern = pattern;
    }

    public AgainstWhat getAgainstWhat() {
        return againstWhat;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public enum AgainstWhat {
        AGAINST_ORIGINAL,
        AGAINST_PREPROCESSED
    }
}
