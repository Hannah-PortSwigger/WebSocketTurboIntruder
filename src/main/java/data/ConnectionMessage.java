package data;

import burp.api.montoya.websocket.Direction;
import connection.Connection;

import java.time.LocalDateTime;

public interface ConnectionMessage
{
    String getPayload();

    Direction getDirection();

    int getLength();

    LocalDateTime getDateTime();

    String getComment();

    Connection getConnection();

    void setComment(String comment);
}
