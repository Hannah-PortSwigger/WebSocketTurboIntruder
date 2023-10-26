package queue;

import burp.api.montoya.MontoyaApi;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import org.python.core.PyObject;

import java.util.concurrent.BlockingQueue;

public class TableBlockingQueueProducer extends PyObject
{
    private final MontoyaApi api;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;

    public TableBlockingQueueProducer(MontoyaApi api, BlockingQueue<ConnectionMessage> tableBlockingQueue)
    {
        this.api = api;

        this.tableBlockingQueue = tableBlockingQueue;
    }

    public void add(ConnectionMessage connectionMessage)
    {
        try
        {
            tableBlockingQueue.put(connectionMessage);
        } catch (InterruptedException e)
        {
            api.logging().logToError("Failed to insert Server to Client message into tableBlockingQueue.");
        }
    }
}
