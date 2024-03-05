package script;

import java.util.List;

public interface ScriptLoader
{
    List<Script> loadScripts(String path);
}
