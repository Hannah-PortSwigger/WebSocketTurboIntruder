package connection;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.WebSockets;
import data.WebSocketConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionFactory
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final AtomicBoolean isAttackRunning;

    public ConnectionFactory(
            Logger logger,
            WebSockets webSockets,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            AtomicBoolean isAttackRunning
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.sendMessageQueue = sendMessageQueue;
        this.isAttackRunning = isAttackRunning;
    }

    public Connection create(HttpRequest upgradeRequest)
    {
        return new WebSocketConnection(logger, webSockets, sendMessageQueue, upgradeRequest, isAttackRunning);
    }
}
