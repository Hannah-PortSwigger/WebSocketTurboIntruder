package queue;

import data.ConnectionMessage;
import logger.Logger;
import ui.attack.table.WebSocketMessageTableModel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Logger logger;
    private final BlockingQueue<ConnectionMessage> queue;
    private final WebSocketMessageTableModel tableModel;
    private final AtomicBoolean isRunning;

    public TableBlockingQueueConsumer(
            Logger logger,
            BlockingQueue<ConnectionMessage> queue,
            WebSocketMessageTableModel tableModel,
            AtomicBoolean isRunning
    )
    {
        this.logger = logger;
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
                logger.logError("Error taking from tableBlockingQueue.");
            }
        }
    }
}
