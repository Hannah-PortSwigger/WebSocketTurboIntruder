package queue;

import attack.AttackHandler;
import attack.AttackStatus;
import burp.api.montoya.websocket.Direction;
import data.WebSocketConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;

public class SendMessageQueueConsumer implements Runnable
{
    private final Logger logger;
    private final AttackHandler attackHandler;
    private final AttackStatus attackStatus;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;

    public SendMessageQueueConsumer(
            Logger logger,
            AttackHandler attackHandler,
            AttackStatus attackStatus,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue
    )
    {
        this.logger = logger;
        this.attackHandler = attackHandler;
        this.attackStatus = attackStatus;
        this.sendMessageQueue = sendMessageQueue;
    }

    @Override
    public void run()
    {
        while (attackStatus.isRunning())
        {
            try
            {
                WebSocketConnectionMessage webSocketConnectionMessage = sendMessageQueue.take();

                if (webSocketConnectionMessage.getDirection() == Direction.CLIENT_TO_SERVER)
                {
                    webSocketConnectionMessage.send();
                }

                attackHandler.processMessage(webSocketConnectionMessage);
            } catch (InterruptedException e)
            {
                if (attackStatus.isRunning())
                {
                    logger.logError("Failed to take message from sendMessageQueue");
                }
            }
        }
    }
}
