package queue;

import attack.AttackHandler;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.websocket.Direction;
import data.WebSocketConnectionMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendMessageQueueConsumer implements Runnable
{
    private final Logging logging;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final AttackHandler attackHandler;

    public SendMessageQueueConsumer(
            Logging logging,
            AtomicBoolean isProcessing,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            AttackHandler attackHandler
    )
    {
        this.logging = logging;
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
                logging.logToError("Failed to take message from sendMessageQueue");
            }
        }
    }
}
