package queue;

import attack.AttackHandler;
import burp.api.montoya.websocket.Direction;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendMessageQueueConsumer implements Runnable
{
    private final Logger logger;
    private final AttackHandler attackHandler;
    private final AtomicBoolean isAttackRunning;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;

    public SendMessageQueueConsumer(
            Logger logger,
            AttackHandler attackHandler,
            AtomicBoolean isAttackRunning
    )
    {
        this.logger = logger;
        this.attackHandler = attackHandler;
        this.isAttackRunning = isAttackRunning;

        sendMessageQueue = attackHandler.getSendMessageQueue();
    }


    @Override
    public void run()
    {
        while (isAttackRunning.get())
        {
            try
            {
                WebSocketConnectionMessage webSocketConnectionMessage = sendMessageQueue.take();

                if (webSocketConnectionMessage.getDirection() == Direction.CLIENT_TO_SERVER)
                {
                    webSocketConnectionMessage.send();
                }

                attackHandler.executeCallback(webSocketConnectionMessage);
            } catch (InterruptedException e)
            {
                logger.logError(LoggerLevel.ERROR, "Failed to take message from sendMessageQueue");
            }
        }
    }
}
