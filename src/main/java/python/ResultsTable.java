package python;

import data.ConnectionMessage;
import org.python.core.PyObject;

import java.util.function.Consumer;

public class ResultsTable extends PyObject
{
    private final Consumer<ConnectionMessage> messageConsumer;

    public ResultsTable(Consumer<ConnectionMessage> messageConsumer)
    {
        this.messageConsumer = messageConsumer;
    }

    public void add(ConnectionMessage connectionMessage)
    {
        messageConsumer.accept(connectionMessage);
    }
}
