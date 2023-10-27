package logger;

import burp.api.montoya.logging.Logging;

import java.io.OutputStream;

public class Logger
{
    private final Logging logging;
    private LoggerLevel currentLoggerLevel;

    public Logger(Logging logging)
    {
        this.logging = logging;
        currentLoggerLevel = LoggerLevel.DEFAULT;
    }

    public void setLogLevel(LoggerLevel loggerLevel)
    {
        currentLoggerLevel = loggerLevel;
    }

    public void logOutput(LoggerLevel loggerLevel, String output)
    {
        logging.logToOutput(output);
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
}
