package connection;

import attack.AttackStatus;
import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.Direction;
import burp.api.montoya.websocket.WebSockets;
import burp.api.montoya.websocket.extension.ExtensionWebSocket;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import data.PendingMessages;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;

import java.time.LocalDateTime;

public class WebSocketConnection implements Connection
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final PendingMessages pendingMessages;
    private final HttpRequest upgradeRequest;
    private final AttackStatus attackStatus;
    private final ExtensionWebSocket extensionWebSocket;

    public WebSocketConnection(
            Logger logger,
            WebSockets webSockets,
            PendingMessages pendingMessages,
            HttpRequest upgradeRequest,
            AttackStatus attackStatus
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.pendingMessages = pendingMessages;
        this.upgradeRequest = upgradeRequest;
        this.attackStatus = attackStatus;

        extensionWebSocket = createExtensionWebSocket(upgradeRequest);
    }

    @Override
    public void queue(String payload)
    {
        if (attackStatus.isRunning()) // TODO
        {
           pendingMessages.accept(new WebSocketConnectionMessage(payload, Direction.CLIENT_TO_SERVER, LocalDateTime.now(), null, this));
        }
    }

    @Override
    public void queue(String payload, String comment)
    {
        if (attackStatus.isRunning())
        {
            pendingMessages.accept(new WebSocketConnectionMessage(payload, Direction.CLIENT_TO_SERVER, LocalDateTime.now(), comment, this)); // TODO - now
        }
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
