package connection;

import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.Direction;
import burp.api.montoya.websocket.WebSockets;
import burp.api.montoya.websocket.extension.ExtensionWebSocket;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketConnection implements Connection
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final HttpRequest upgradeRequest;
    private final AtomicBoolean isAttackRunning;
    private final ExtensionWebSocket extensionWebSocket;

    WebSocketConnection(
            Logger logger,
            WebSockets webSockets,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            HttpRequest upgradeRequest,
            AtomicBoolean isAttackRunning
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.sendMessageQueue = sendMessageQueue;
        this.upgradeRequest = upgradeRequest;
        this.isAttackRunning = isAttackRunning;

        extensionWebSocket = createExtensionWebSocket(upgradeRequest);
    }

    @Override
    public void queue(String payload)
    {
        if (isAttackRunning.get())
        {try
            {
                sendMessageQueue.put(new WebSocketConnectionMessage(payload, Direction.CLIENT_TO_SERVER, LocalDateTime.now(), null, this));
            }
            catch (InterruptedException e)
            {
                logger.logError(LoggerLevel.ERROR, "Failed to put message on sendMessageQueue");
            }
        }
    }

    @Override
    public void queue(String payload, String comment)
    {
        if (isAttackRunning.get())
        {try
            {
                sendMessageQueue.put(new WebSocketConnectionMessage(payload, Direction.CLIENT_TO_SERVER, LocalDateTime.now(), comment, this));
            }
            catch (InterruptedException e)
            {
                logger.logError(LoggerLevel.ERROR, "Failed to put message on sendMessageQueue");
            }
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

            extensionWebSocket.registerMessageHandler(new WebSocketExtensionWebSocketMessageHandler(logger, sendMessageQueue, this));
        }
        else
        {
            logger.logError(LoggerLevel.DEFAULT, "Failed to create websocket connection");
            extensionWebSocket = null;
        }

        return extensionWebSocket;
    }
}
