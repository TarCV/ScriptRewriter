import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by const on 25.11.14.
 */
public class ScriptRewriter {
    public static void main(String[] args) throws IOException {
        String path = args[0];
        ParsedScript script = new ParsedScript(path);
    }
}
