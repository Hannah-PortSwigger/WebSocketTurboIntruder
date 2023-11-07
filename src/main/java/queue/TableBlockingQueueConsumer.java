package queue;

import data.ConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;
import ui.attack.table.WebSocketMessageTableModel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Logger logger;
    private final BlockingQueue<ConnectionMessage> queue;
    private final WebSocketMessageTableModel tableModel;
    private final AtomicBoolean isAttackRunning;

    public TableBlockingQueueConsumer(
            Logger logger,
            WebSocketMessageTableModel tableModel,
            BlockingQueue<ConnectionMessage> queue,
            AtomicBoolean isAttackRunning
    )
    {
        this.logger = logger;
        this.queue = queue;
        this.tableModel = tableModel;
        this.isAttackRunning = isAttackRunning;
    }

    @Override
    public void run()
    {
        while (isAttackRunning.get())
        {
            try
            {
                ConnectionMessage connectionMessage = queue.take();
                EventQueue.invokeLater(() -> tableModel.add(connectionMessage));
            } catch (InterruptedException e)
            {
                logger.logError(LoggerLevel.ERROR, "Error taking from tableBlockingQueue.");
            }
        }
    }
}
