package connection;

import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import data.WebSocketConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionFactory
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;

    public ConnectionFactory(
            Logger logger,
            WebSockets webSockets,
            AtomicBoolean isProcessing,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.isProcessing = isProcessing;
        this.sendMessageQueue = sendMessageQueue;
    }

    public Connection create(WebSocketMessage baseWebSocketMessage)
    {
        return new WebSocketConnection(logger, webSockets, isProcessing, baseWebSocketMessage, sendMessageQueue);
    }
}
