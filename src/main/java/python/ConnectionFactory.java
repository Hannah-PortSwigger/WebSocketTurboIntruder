package python;

import attack.AttackStatus;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.WebSockets;
import connection.Connection;
import connection.WebSocketConnection;
import data.PendingMessages;
import logger.Logger;

public class ConnectionFactory
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final PendingMessages pendingMessages;
    private final AttackStatus attackStatus;

    public ConnectionFactory(
            Logger logger,
            WebSockets webSockets,
            PendingMessages pendingMessages,
            AttackStatus attackStatus
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.pendingMessages = pendingMessages;
        this.attackStatus = attackStatus;
    }

    @SuppressWarnings("unused") // called by Python
    public Connection create(HttpRequest upgradeRequest)
    {
        return new WebSocketConnection(logger, webSockets, pendingMessages, upgradeRequest, attackStatus);
    }
}
