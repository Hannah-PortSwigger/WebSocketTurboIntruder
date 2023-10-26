package connection;

import attack.AttackHandler;
import burp.WebSocketExtensionWebSocketMessageHandler;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.extension.ExtensionWebSocket;
import burp.api.montoya.websocket.extension.ExtensionWebSocketCreation;
import data.WebSocketConnectionMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionFactory
{
    private final MontoyaApi api;
    private final AtomicBoolean isProcessing;
    private final AttackHandler attackHandler;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;

    public ConnectionFactory(MontoyaApi api, AtomicBoolean isProcessing, AttackHandler attackHandler, BlockingQueue<WebSocketConnectionMessage> sendMessageQueue)
    {
        this.api = api;
        this.isProcessing = isProcessing;
        this.attackHandler = attackHandler;
        this.sendMessageQueue = sendMessageQueue;
    }

    public Connection create(WebSocketMessage baseWebSocketMessage)
    {
        return new WebSocketConnection(api, isProcessing, attackHandler, baseWebSocketMessage, sendMessageQueue);
    }
}
