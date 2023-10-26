package connection;

import attack.AttackHandler;
import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.Direction;
import burp.api.montoya.websocket.WebSockets;
import burp.api.montoya.websocket.extension.ExtensionWebSocket;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import data.WebSocketConnectionMessage;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketConnection implements Connection
{
    private final Logging logging;
    private final WebSockets webSockets;
    private final AtomicBoolean isProcessing;
    private final AttackHandler attackHandler;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final ExtensionWebSocket extensionWebSocket;

    WebSocketConnection(
            Logging logging,
            WebSockets webSockets,
            AtomicBoolean isProcessing,
            AttackHandler attackHandler,
            WebSocketMessage baseWebSocketMessage,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue
    )
    {
        this.logging = logging;
        this.webSockets = webSockets;
        this.isProcessing = isProcessing;
        this.attackHandler = attackHandler;
        this.sendMessageQueue = sendMessageQueue;

        extensionWebSocket = createExtensionWebSocket(baseWebSocketMessage);
    }

    @Override
    public void queue(String payload)
    {
        if (isProcessing.get())
        {
            WebSocketConnectionMessage webSocketConnectionMessage = new WebSocketConnectionMessage(payload, Direction.CLIENT_TO_SERVER, LocalDateTime.now(), null, this);

            try
            {
                sendMessageQueue.put(webSocketConnectionMessage);
            }
            catch (InterruptedException e)
            {
                logging.logToError("Failed to put message on sendMessageQueue");
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

            extensionWebSocket.registerMessageHandler(new WebSocketExtensionWebSocketMessageHandler(logging, attackHandler, this));
        }
        else
        {
            logging.logToError("Failed to create websocket connection");
            extensionWebSocket = null;
        }

        return extensionWebSocket;
    }
}
