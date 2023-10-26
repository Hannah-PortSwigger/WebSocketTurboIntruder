package queue;

import burp.api.montoya.logging.Logging;
import data.ConnectionMessage;
import ui.attack.table.WebSocketMessageTableModel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Logging logging;
    private final BlockingQueue<ConnectionMessage> queue;
    private final WebSocketMessageTableModel tableModel;
    private final AtomicBoolean isRunning;

    public TableBlockingQueueConsumer(
            Logging logging,
            BlockingQueue<ConnectionMessage> queue,
            WebSocketMessageTableModel tableModel,
            AtomicBoolean isRunning
    )
    {
        this.logging = logging;
        this.queue = queue;
        this.tableModel = tableModel;
        this.isRunning = isRunning;
    }

    @Override
    public void run()
    {
        while (isRunning.get())
        {
            try
            {
                ConnectionMessage connectionMessage = queue.take();
                EventQueue.invokeLater(() -> tableModel.add(connectionMessage));
            } catch (InterruptedException e)
            {
                logging.logToError("Error taking from tableBlockingQueue.");
            }
        }
    }
}
