package data;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;

public record InitialWebSocketMessage(HttpRequest upgradeRequest, ByteArray message)
{
}
