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
        ScriptLoader scriptLoader = fileLocationConfiguration.isDefault() ? new ClassPathScriptLoader() : new FilePathScriptLoader();

        return scriptLoader.loadScripts(fileLocationConfiguration.getWebSocketScriptPath());
    }
}
