package script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

class FilePathScriptLoader implements ScriptLoader
{
    public List<Script> loadScripts(String path)
    {
        try (Stream<Path> stream = Files.walk(Paths.get(path)))
        {
            return stream
                    .filter(scriptPath -> scriptPath.toString().endsWith(".py"))
                    .sorted()
                    .map(scriptPath -> (Script) new FilepathScript(scriptPath))
                    .toList();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
