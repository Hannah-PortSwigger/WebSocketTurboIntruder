package data;

import attack.AttackStatus;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MessagesToDisplay implements Consumer<ConnectionMessage>, Supplier<ConnectionMessage>
{
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final Logger logger;
    private final AttackStatus attackStatus;

    public MessagesToDisplay(Logger logger, AttackStatus attackStatus)
    {
        this.logger = logger;
        this.attackStatus = attackStatus;
        this.tableBlockingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void accept(ConnectionMessage connectionMessage)
    {
        tableBlockingQueue.add(connectionMessage);
    }

    @Override
    public ConnectionMessage get()
    {
        try
        {
            return tableBlockingQueue.take();
        }
        catch (InterruptedException e)
        {
            if (attackStatus.isRunning())
            {
                logger.logError("Error taking from tableBlockingQueue.");
            }

            return null;
        }
    }

    public void clear()
    {
        tableBlockingQueue.clear();
    }
}
