package data;

import burp.api.montoya.websocket.Direction;
import connection.Connection;

import java.time.LocalDateTime;

public class ConnectionMessage
{
    private final WebSocketConnectionMessage message;

    public ConnectionMessage(WebSocketConnectionMessage message)
    {
        this.message = message;
    }

    public String getPayload()
    {
        return message.getPayload();
    }

    public Direction getDirection()
    {
        return message.getDirection();
    }

    public int getLength()
    {
        return message.getLength();
    }

    public LocalDateTime getDateTime()
    {
        return message.getDateTime();
    }

    public String getComment()
    {
        return message.getComment();
    }

    public Connection getConnection()
    {
        return message.getConnection();
    }

    public void setComment(String comment)
    {
        message.setComment(comment);
    }
}
