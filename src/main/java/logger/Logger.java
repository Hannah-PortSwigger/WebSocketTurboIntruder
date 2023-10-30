package logger;

import burp.api.montoya.logging.Logging;

import java.io.OutputStream;

import static logger.LoggerLevel.DEBUG;
import static logger.LoggerLevel.ERROR;

public class Logger
{
    private final Logging logging;
    private boolean debugLogLevel;
    private boolean errorLogLevel;

    public Logger(Logging logging)
    {
        this.logging = logging;

        debugLogLevel  = false;
        errorLogLevel = true;
    }

    public boolean isDebugLogLevel()
    {
        return debugLogLevel;
    }

    public boolean isErrorLogLevel()
    {
        return errorLogLevel;
    }

    public void setDebugLogLevel(boolean value)
    {
        debugLogLevel = value;
    }

    public void setErrorLogLevel(boolean value)
    {
        errorLogLevel = value;
    }

    public void logOutput(LoggerLevel loggerLevel, String output)
    {
        if (isValid(loggerLevel))
        {
            logging.logToOutput(output);
        }
    }

    public void logError(LoggerLevel loggerLevel, String output)
    {
        if (isValid(loggerLevel))
        {
            logging.logToError(output);
        }
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
        if (outputLogLevel.equals(DEBUG) && !debugLogLevel)
        {
            return false;
        }
        else if (outputLogLevel.equals(ERROR) && !errorLogLevel)
        {
            return false;
        }

        return true;
    }
}
