package connection;

public interface Connection
{
    void queue(String payload);

    void queue(String payload, String comment);
}
