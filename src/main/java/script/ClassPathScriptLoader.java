package script;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

class ClassPathScriptLoader implements ScriptLoader
{
    public List<Script> loadScripts(String path)
    {
        List<Script> scriptList = new ArrayList<>();

        URL url = this.getClass().getResource(path);

        if (url != null)
        {
            Stream<Path> stream  = null;
            try
            {
                URI uri = url.toURI();

                try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                    stream = Files.walk(Paths.get(uri));

                    stream.forEach(scriptPath -> {
                        if (scriptPath.toString().endsWith(".py"))
                        {
                            scriptList.add(new ClasspathScript(scriptPath));
                        }
                    });
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

        return scriptList;
    }
}
