package connection;

import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.WebSockets;
import burp.api.montoya.websocket.extension.ExtensionWebSocket;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import data.AttackIdAndWebSocketConnectionMessage;
import data.PendingMessages;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;

import static burp.api.montoya.websocket.Direction.CLIENT_TO_SERVER;

public class WebSocketConnection implements Connection
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final PendingMessages pendingMessages;
    private final int attackId;
    private final HttpRequest upgradeRequest;
    private final ExtensionWebSocket extensionWebSocket;

    public WebSocketConnection(
            Logger logger,
            WebSockets webSockets,
            PendingMessages pendingMessages,
            int attackId,
            HttpRequest upgradeRequest
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.pendingMessages = pendingMessages;
        this.attackId = attackId;
        this.upgradeRequest = upgradeRequest;

        extensionWebSocket = createExtensionWebSocket(upgradeRequest);
    }

    @Override
    public void queue(String payload)
    {
        pendingMessages.accept(new AttackIdAndWebSocketConnectionMessage(attackId, new WebSocketConnectionMessage(payload, CLIENT_TO_SERVER, this)));
    }

    @Override
    public void queue(String payload, String comment)
    {
        pendingMessages.accept(new AttackIdAndWebSocketConnectionMessage(attackId, new WebSocketConnectionMessage(payload, CLIENT_TO_SERVER, comment, this)));
    }

    @Override
    public HttpRequest upgradeRequest()
    {
        return upgradeRequest;
    }

    public void sendMessage(String payload)
    {
        extensionWebSocket.sendTextMessage(payload);
    }

    private ExtensionWebSocket createExtensionWebSocket(HttpRequest upgradeRequest)
    {
        ExtensionWebSocket extensionWebSocket;

        ExtensionWebSocketCreation extensionWebSocketCreation = webSockets.createWebSocket(upgradeRequest);
        logger.logOutput(LoggerLevel.DEBUG, "WebSocketConnection Upgrade request: " + upgradeRequest.toString());

        if (extensionWebSocketCreation.webSocket().isPresent())
        {
            extensionWebSocket = extensionWebSocketCreation.webSocket().get();

            extensionWebSocket.registerMessageHandler(
                    new WebSocketExtensionWebSocketMessageHandler(
                            logger,
                            pendingMessages,
                            attackId,
                            this // TODO
                    )
            );
        }
        else
        {
            logger.logError("Failed to create websocket connection");
            extensionWebSocket = null;
        }

        return extensionWebSocket;
    }
}
