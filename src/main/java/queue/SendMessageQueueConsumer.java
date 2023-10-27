package queue;

import attack.AttackHandler;
import burp.api.montoya.websocket.Direction;
import data.WebSocketConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendMessageQueueConsumer implements Runnable
{
    private final Logger logger;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final AttackHandler attackHandler;

    public SendMessageQueueConsumer(
            Logger logger,
            AtomicBoolean isProcessing,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            AttackHandler attackHandler
    )
    {
        this.logger = logger;
        this.isProcessing = isProcessing;
        this.sendMessageQueue = sendMessageQueue;
        this.attackHandler = attackHandler;
    }


    @Override
    public void run()
    {
        while (isProcessing.get())
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
                logger.logError("Failed to take message from sendMessageQueue");
            }
        }
    }
}
