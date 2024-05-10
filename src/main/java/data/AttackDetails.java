package data;

import burp.api.montoya.http.message.requests.HttpRequest;

public record AttackDetails(int numberOfThreads, String payload, HttpRequest upgradeRequest, String script)
{
}
