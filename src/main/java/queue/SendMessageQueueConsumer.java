package queue;

import attack.AttackHandler;
import burp.api.montoya.MontoyaApi;
import data.WebSocketConnectionMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendMessageQueueConsumer implements Runnable
{
    private final MontoyaApi api;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final AttackHandler attackHandler;

    public SendMessageQueueConsumer(MontoyaApi api, AtomicBoolean isProcessing, BlockingQueue<WebSocketConnectionMessage> sendMessageQueue, AttackHandler attackHandler)
    {
        this.api = api;
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

                webSocketConnectionMessage.send();

                attackHandler.executeCallback(webSocketConnectionMessage);
            } catch (InterruptedException e)
            {
                api.logging().logToError("Failed to take message from sendMessageQueue");
            }
        }
    }
}
