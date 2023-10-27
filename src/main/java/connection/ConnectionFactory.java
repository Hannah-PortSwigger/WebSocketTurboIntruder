package connection;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import data.WebSocketConnectionMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionFactory
{
    private final Logging logging;
    private final WebSockets webSockets;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;

    public ConnectionFactory(
            Logging logging,
            WebSockets webSockets,
            AtomicBoolean isProcessing,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue
    )
    {
        this.logging = logging;
        this.webSockets = webSockets;
        this.isProcessing = isProcessing;
        this.sendMessageQueue = sendMessageQueue;
    }

    public Connection create(WebSocketMessage baseWebSocketMessage)
    {
        return new WebSocketConnection(logging, webSockets, isProcessing, baseWebSocketMessage, sendMessageQueue);
    }
}
