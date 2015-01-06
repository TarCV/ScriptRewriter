import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by TarCV on 25.11.14.
 */
public class ScriptRewriter {
    public static void main(String[] args) throws IOException {
        Path path = FileSystems.getDefault().getPath(args[0]);
        ParsedScript script = new ParsedScript(path);
    }
}
