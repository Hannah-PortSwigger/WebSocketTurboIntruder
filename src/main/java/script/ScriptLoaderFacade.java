package script;

import config.FileLocationConfiguration;

import java.util.List;

public class ScriptLoaderFacade
{
    private final FileLocationConfiguration fileLocationConfiguration;

    public ScriptLoaderFacade(FileLocationConfiguration fileLocationConfiguration)
    {
        this.fileLocationConfiguration = fileLocationConfiguration;
    }

    public List<Script> loadScripts()
    {
        ScriptLoader scriptLoader;

        if (fileLocationConfiguration.isDefault())
        {
            scriptLoader = new ClassPathScriptLoader();
        }
        else
        {
            scriptLoader = new FilePathScriptLoader();
        }

        return scriptLoader.loadScripts(fileLocationConfiguration.getWebSocketScriptPath());
    }
}
