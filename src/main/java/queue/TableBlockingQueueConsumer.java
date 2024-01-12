package queue;

import data.ConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Logger logger;
    private final BlockingQueue<ConnectionMessage> queue;
    private final Consumer<ConnectionMessage> messageConsumer;
    private final AtomicBoolean isAttackRunning;

    public TableBlockingQueueConsumer(
            Logger logger,
            BlockingQueue<ConnectionMessage> queue,
            AtomicBoolean isAttackRunning,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        this.logger = logger;
        this.queue = queue;
        this.messageConsumer = messageConsumer;
        this.isAttackRunning = isAttackRunning;
    }

    @Override
    public void run()
    {
        while (isAttackRunning.get())
        {
            try
            {
                messageConsumer.accept(queue.take());
            }
            catch (InterruptedException e)
            {
                if (isAttackRunning.get())
                {
                    logger.logError("Error taking from tableBlockingQueue.");
                }
            }
        }
    }
}
