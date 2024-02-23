package config;

import burp.api.montoya.persistence.Preferences;
import logger.Logger;
import logger.LoggerLevel;

import java.util.Objects;

public class FileLocationConfiguration
{
    private static final String DEFAULT_SCRIPT_DIRECTORY = "/examples/";
    private final Logger logger;
    private final Preferences preferences;

    public FileLocationConfiguration(Logger logger, Preferences preferences)
    {
        this.logger = logger;
        this.preferences = preferences;

        if (getWebSocketScriptPath() == null)
        {
            setWebSocketScriptPath();
        }
    }

    public String defaultDirectory()
    {
        return DEFAULT_SCRIPT_DIRECTORY;
    }

    public String getWebSocketScriptPath()
    {
        return preferences.getString("websocketsScriptsPath");
    }

    public void setWebSocketScriptPath()
    {
        setWebSocketScriptPath(DEFAULT_SCRIPT_DIRECTORY);
    }

    public void setWebSocketScriptPath(String scriptPath)
    {
        preferences.setString("websocketsScriptsPath", scriptPath);
        logger.logOutput(LoggerLevel.DEBUG, "Scripts directory set to " + scriptPath);
    }

    public boolean isDefault()
    {
        return Objects.equals(getWebSocketScriptPath(), DEFAULT_SCRIPT_DIRECTORY);
    }
}
