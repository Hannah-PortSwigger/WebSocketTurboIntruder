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
        currentLoggerLevel = LoggerLevel.DEBUG;
    }

    public void setLogLevel(LoggerLevel loggerLevel)
    {
        currentLoggerLevel = loggerLevel;
    }

    public void logOutput(LoggerLevel loggerLevel, String output)
    {
        if (currentLoggerLevel == LoggerLevel.DEBUG || (currentLoggerLevel == LoggerLevel.ERROR_ONLY && loggerLevel == LoggerLevel.ERROR_ONLY))
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
}
