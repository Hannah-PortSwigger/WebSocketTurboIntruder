package queue;

import burp.api.montoya.logging.Logging;
import data.ConnectionMessage;
import org.python.core.PyObject;

import java.util.concurrent.BlockingQueue;

public class TableBlockingQueueProducer extends PyObject
{
    private final Logging logging;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;

    public TableBlockingQueueProducer(
            Logging logging,
            BlockingQueue<ConnectionMessage> tableBlockingQueue
    )
    {

        this.logging = logging;
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
            logging.logToError("Failed to insert Server to Client message into tableBlockingQueue.");
        }
    }
}
