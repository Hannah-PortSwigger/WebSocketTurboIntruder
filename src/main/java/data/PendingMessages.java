package data;

import attack.AttackStatus;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PendingMessages implements Consumer<AttackIdAndWebSocketConnectionMessage>, Supplier<WebSocketConnectionMessage> // TODO
{
    private final BlockingQueue<WebSocketConnectionMessage> unprocessedMessageQueue;
    private final Logger logger;
    private final AttackStatus attackStatus;

    public PendingMessages(Logger logger, AttackStatus attackStatus)
    {
        this.logger = logger;
        this.attackStatus = attackStatus;
        this.unprocessedMessageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void accept(AttackIdAndWebSocketConnectionMessage attackIdAndWebSocketConnectionMessage)
    {
        if (attackStatus.isRunning() && attackStatus.isCurrentAttackId(attackIdAndWebSocketConnectionMessage.attackId()))
        {
            unprocessedMessageQueue.add(attackIdAndWebSocketConnectionMessage.webSocketConnectionMessage());
        }
    }

    @Override
    public WebSocketConnectionMessage get()
    {
        try
        {
            return unprocessedMessageQueue.take();
        }
        catch (InterruptedException e)
        {
            if (attackStatus.isRunning())
            {
                logger.logError("Error taking from pending messages.");
            }

            return null;
        }
    }

    public void clear()
    {
        unprocessedMessageQueue.clear();
    }
}
