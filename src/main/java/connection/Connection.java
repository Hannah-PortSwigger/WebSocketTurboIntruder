package connection;

import burp.api.montoya.http.message.requests.HttpRequest;

public interface Connection
{
    void queue(String payload);

    void queue(String payload, String replacement);

    void queueWithComment(String payload, String comment);

    void queueWithComment(String payload, String replacement, String comment);

    HttpRequest upgradeRequest();
}
