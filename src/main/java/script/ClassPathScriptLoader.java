package script;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

class ClassPathScriptLoader implements ScriptLoader
{
    public List<Script> loadScripts(String path)
    {
        URL url = this.getClass().getResource(path);

        if (url == null)
        {
            return emptyList();
        }

        Stream<Path> stream = null;

        try
        {
            URI uri = url.toURI();

            try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                stream = Files.walk(Paths.get(uri));

                return stream
                        .filter(scriptPath -> scriptPath.toString().endsWith(".py"))
                        .sorted()
                        .map(scriptPath -> (Script) new ClasspathScript(scriptPath))
                        .toList();
            }
        } catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
}
