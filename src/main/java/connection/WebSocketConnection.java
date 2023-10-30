package connection;

import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
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
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final ExtensionWebSocket extensionWebSocket;

    WebSocketConnection(
            Logger logger,
            WebSockets webSockets,
            AtomicBoolean isProcessing,
            WebSocketMessage baseWebSocketMessage,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.isProcessing = isProcessing;
        this.sendMessageQueue = sendMessageQueue;

        extensionWebSocket = createExtensionWebSocket(baseWebSocketMessage);
    }

    @Override
    public void queue(String payload)
    {
        if (isProcessing.get())
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

    public void sendMessage(String payload)
    {
        extensionWebSocket.sendTextMessage(payload);
    }

    private ExtensionWebSocket createExtensionWebSocket(WebSocketMessage baseWebSocketMessage)
    {
        ExtensionWebSocket extensionWebSocket;

        ExtensionWebSocketCreation extensionWebSocketCreation = webSockets.createWebSocket(baseWebSocketMessage.upgradeRequest());

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
