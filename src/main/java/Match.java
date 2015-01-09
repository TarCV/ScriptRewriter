import java.util.regex.Pattern;

/**
 * Created by TarCV.
 */
public class Match {
    private final Pattern pattern;

    private final String originalFragment;
    private final String preprocessedFragment;

    public Match(Pattern pattern, String originalFragment, String preprocessedFragment) {

        this.pattern = pattern;
        this.originalFragment = originalFragment;
        this.preprocessedFragment = preprocessedFragment;
    }

    public String getOriginalFragment() {
        return originalFragment;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getPreprocessedFragment() {
        return preprocessedFragment;
    }
}
