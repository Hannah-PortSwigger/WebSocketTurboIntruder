package connection;

import attack.AttackStatus;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.WebSockets;
import data.WebSocketConnectionMessage;
import logger.Logger;

import java.util.concurrent.BlockingQueue;

public class ConnectionFactory
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final AttackStatus attackStatus;

    public ConnectionFactory(
            Logger logger,
            WebSockets webSockets,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            AttackStatus attackStatus
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.sendMessageQueue = sendMessageQueue;
        this.attackStatus = attackStatus;
    }

    public Connection create(HttpRequest upgradeRequest)
    {
        return new WebSocketConnection(logger, webSockets, sendMessageQueue, upgradeRequest, attackStatus);
    }
}
