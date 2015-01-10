/**
 * Created by TarCV.
 */
public class Match {
    private final ParserPattern pattern;

    private final String originalFragment;
    private final String preprocessedFragment;

    public Match(ParserPattern pattern, String originalFragment, String preprocessedFragment) {

        this.pattern = pattern;
        this.originalFragment = originalFragment;
        this.preprocessedFragment = preprocessedFragment;
    }

    public String getOriginalFragment() {
        return originalFragment;
    }

    public ParserPattern getPattern() {
        return pattern;
    }

    public String getPreprocessedFragment() {
        return preprocessedFragment;
    }
}
