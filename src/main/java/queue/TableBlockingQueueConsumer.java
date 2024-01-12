package queue;

import attack.AttackStatus;
import data.ConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Logger logger;
    private final BlockingQueue<ConnectionMessage> queue;
    private final Consumer<ConnectionMessage> messageConsumer;
    private final AttackStatus attackStatus;

    public TableBlockingQueueConsumer(
            Logger logger,
            BlockingQueue<ConnectionMessage> queue,
            AttackStatus attackStatus,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        this.logger = logger;
        this.queue = queue;
        this.messageConsumer = messageConsumer;
        this.attackStatus = attackStatus;
    }

    @Override
    public void run()
    {
        while (attackStatus.isRunning())
        {
            try
            {
                messageConsumer.accept(queue.take());
            }
            catch (InterruptedException e)
            {
                if (attackStatus.isRunning())
                {
                    logger.logError("Error taking from tableBlockingQueue.");
                }
            }
        }
    }
}
