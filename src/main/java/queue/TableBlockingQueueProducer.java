package queue;

import data.ConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;
import org.python.core.PyObject;

import java.util.concurrent.BlockingQueue;

public class TableBlockingQueueProducer extends PyObject
{
    private final Logger logger;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;

    public TableBlockingQueueProducer(
            Logger logger,
            BlockingQueue<ConnectionMessage> tableBlockingQueue
    )
    {
        this.logger = logger;
        this.tableBlockingQueue = tableBlockingQueue;
    }

    public void add(ConnectionMessage connectionMessage)
    {
        try
        {
            tableBlockingQueue.put(connectionMessage);
        }
        catch (InterruptedException e)
        {
            logger.logError(LoggerLevel.ERROR, "Failed to insert Server to Client message into tableBlockingQueue.");
        }
    }
}
