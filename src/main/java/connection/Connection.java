package connection;

import burp.api.montoya.http.message.requests.HttpRequest;

public interface Connection
{
    void queue(String payload);

    void queue(String payload, String comment);

    HttpRequest upgradeRequest();
}
