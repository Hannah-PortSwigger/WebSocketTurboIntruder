package queue;

import attack.AttackStatus;
import data.ConnectionMessage;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TableBlockingQueueConsumer implements Runnable
{
    private final Supplier<ConnectionMessage> queue;
    private final Consumer<ConnectionMessage> messageConsumer;
    private final AttackStatus attackStatus;

    public TableBlockingQueueConsumer(
            Supplier<ConnectionMessage> queue,
            AttackStatus attackStatus,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        this.queue = queue;
        this.messageConsumer = messageConsumer;
        this.attackStatus = attackStatus;
    }

    @Override
    public void run()
    {
        while (attackStatus.isRunning())
        {
            ConnectionMessage connectionMessage = queue.get();

            if (connectionMessage != null)
            { // TODO - push down
                messageConsumer.accept(connectionMessage);
            }
        }
    }
}
