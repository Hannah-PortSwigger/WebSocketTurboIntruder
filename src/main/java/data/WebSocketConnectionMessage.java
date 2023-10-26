package data;

import burp.api.montoya.websocket.Direction;
import connection.WebSocketConnection;

import java.time.LocalDateTime;

public class WebSocketConnectionMessage implements ConnectionMessage
{
    private final String payload;
    private final Direction direction;
    private final int length;
    private final LocalDateTime dateTime;
    private String comment;
    private final WebSocketConnection connection;

    public WebSocketConnectionMessage(
            String payload,
            Direction direction,
            LocalDateTime dateTime,
            String comment,
            WebSocketConnection connection
    )
    {
        this.payload = payload;
        this.direction = direction;
        this.length = payload.length();
        this.dateTime = dateTime;
        this.comment = comment;
        this.connection = connection;
    }

    @Override
    public String getPayload()
    {
        return payload;
    }

    @Override
    public Direction getDirection()
    {
        return direction;
    }

    @Override
    public int getLength()
    {
        return length;
    }

    @Override
    public LocalDateTime getDateTime()
    {
        return dateTime;
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
    public WebSocketConnection getConnection()
    {
        return connection;
    }

    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void send()
    {
        connection.sendMessage(payload);
    }
}
