package queue;

import attack.AttackStatus;
import data.WebSocketConnectionMessage;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static burp.api.montoya.websocket.Direction.CLIENT_TO_SERVER;

public class SendMessageQueueConsumer implements Runnable
{
    private final Consumer<WebSocketConnectionMessage> messageProcessor;
    private final AttackStatus attackStatus;
    private final Supplier<WebSocketConnectionMessage> pendingMessageSupplier;

    public SendMessageQueueConsumer(
            Consumer<WebSocketConnectionMessage> messageProcessor,
            AttackStatus attackStatus,
            Supplier<WebSocketConnectionMessage> pendingMessageSupplier
    )
    {
        this.messageProcessor = messageProcessor;
        this.attackStatus = attackStatus;
        this.pendingMessageSupplier = pendingMessageSupplier;
    }

    @Override
    public void run()
    {
        while (attackStatus.isRunning())
        {
            // TODO - Move into Will? (PendingMessages)
            WebSocketConnectionMessage webSocketConnectionMessage = pendingMessageSupplier.get();

            if (webSocketConnectionMessage != null && webSocketConnectionMessage.getDirection() == CLIENT_TO_SERVER)
            {
                webSocketConnectionMessage.send();
            }

            messageProcessor.accept(webSocketConnectionMessage);
        }
    }
}
