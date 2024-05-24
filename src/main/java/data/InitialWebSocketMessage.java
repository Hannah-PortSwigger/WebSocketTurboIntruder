package data;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;

public record InitialWebSocketMessage(HttpRequest upgradeRequest, ByteArray message)
{
    public static InitialWebSocketMessage from(WebSocketMessage webSocketMessage)
    {
        return new InitialWebSocketMessage(webSocketMessage.upgradeRequest(), webSocketMessage.payload());
    }
}
