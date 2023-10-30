package burp;

import burp.api.montoya.websocket.BinaryMessage;
import burp.api.montoya.websocket.TextMessage;
import burp.api.montoya.websocket.extension.ExtensionWebSocketMessageHandler;
import connection.WebSocketConnection;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class WebSocketExtensionWebSocketMessageHandler implements ExtensionWebSocketMessageHandler
{
    private final Logger logger;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final WebSocketConnection connection;

    public WebSocketExtensionWebSocketMessageHandler(
            Logger logger,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            WebSocketConnection connection
    )
    {
        this.logger = logger;
        this.sendMessageQueue = sendMessageQueue;
        this.connection = connection;
    }

    @Override
    public void textMessageReceived(TextMessage textMessage)
    {
        try
        {
            sendMessageQueue.put(new WebSocketConnectionMessage(textMessage.payload(), textMessage.direction(), LocalDateTime.now(), null, connection));
        }
        catch (InterruptedException e)
        {
            logger.logError(LoggerLevel.ERROR, "Failed to put message on queue.");
        }
    }

    @Override
    public void binaryMessageReceived(BinaryMessage binaryMessage)
    {
        logger.logOutput(LoggerLevel.ERROR, "Unhandled binary message received");
    }
}
