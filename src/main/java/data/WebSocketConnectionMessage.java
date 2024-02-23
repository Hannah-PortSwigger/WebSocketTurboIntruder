package data;

import burp.api.montoya.websocket.Direction;
import connection.WebSocketConnection;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class WebSocketConnectionMessage implements ConnectionMessage
{
    private final String message;
    private final Direction direction;
    private final int length;
    private final LocalDateTime dateTime;
    private String comment;
    private final WebSocketConnection connection;

    public WebSocketConnectionMessage(String message, Direction direction, WebSocketConnection connection)
    {
        this(message, direction, null, connection);
    }

    public WebSocketConnectionMessage(
            String message,
            Direction direction,
            String comment,
            WebSocketConnection connection
    )
    {
        this.message = message;
        this.direction = direction;
        this.length = message.length();
        this.dateTime = now();
        this.comment = comment;
        this.connection = connection;
    }

    @Override
    public String getMessage()
    {
        return message;
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
        connection.sendMessage(message);
    }
}
