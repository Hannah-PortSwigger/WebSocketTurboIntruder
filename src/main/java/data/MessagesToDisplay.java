package data;

import attack.AttackStatus;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MessagesToDisplay implements Consumer<ConnectionMessage>, Supplier<ConnectionMessage>
{
    private final Logger logger;
    private final AttackStatus attackStatus;
    private final BlockingQueue<ConnectionMessage> messagesToDisplay;

    public MessagesToDisplay(Logger logger, AttackStatus attackStatus)
    {
        this.logger = logger;
        this.attackStatus = attackStatus;
        this.messagesToDisplay = new LinkedBlockingQueue<>();
    }

    @Override
    public void accept(ConnectionMessage connectionMessage)
    {
        messagesToDisplay.add(connectionMessage);
    }

    @Override
    public ConnectionMessage get()
    {
        try
        {
            return messagesToDisplay.take();
        }
        catch (InterruptedException e)
        {
            if (attackStatus.isRunning())
            {
                logger.logError("Error taking from messages to display.");
            }

            return null;
        }
    }

    public void clear()
    {
        messagesToDisplay.clear();
    }
}
