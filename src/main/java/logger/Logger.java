package logger;

import burp.api.montoya.logging.Logging;

import java.io.OutputStream;

import static logger.LoggerLevel.DEBUG;

public class Logger
{
    private final Logging logging;
    private boolean debugLogLevel;

    public Logger(Logging logging)
    {
        this.logging = logging;
        debugLogLevel  = false;
    }

    public boolean isDebugLogLevel()
    {
        return debugLogLevel;
    }

    public void setDebugLogLevel(boolean value)
    {
        debugLogLevel = value;
    }

    public void logOutput(LoggerLevel loggerLevel, String output)
    {
        if (isValid(loggerLevel))
        {
            logging.logToOutput(output);
        }
    }

    public void logError(String output)
    {
        logging.logToError(output);
    }

    public OutputStream outputStream()
    {
        return logging.output();
    }

    public OutputStream errorStream()
    {
        return logging.error();
    }
    
    private boolean isValid(LoggerLevel outputLogLevel)
    {
        return !outputLogLevel.equals(DEBUG) || debugLogLevel;
    }
}
