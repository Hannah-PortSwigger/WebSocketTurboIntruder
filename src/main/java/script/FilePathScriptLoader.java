package script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class FilePathScriptLoader implements ScriptLoader
{
    public List<Script> loadScripts(String path)
    {
        List<Script> scriptList = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(Paths.get(path)))
        {
            stream.forEach(scriptPath -> {
                if (scriptPath.toString().endsWith(".py"))
                {
                    scriptList.add(new FilepathScript(scriptPath));
                }
            });
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return scriptList;
    }
}
