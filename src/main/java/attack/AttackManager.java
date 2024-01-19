package attack;

import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static logger.LoggerLevel.DEBUG;

public class AttackManager implements AttackStarter, AttackStatus, AttackStopper
{
    private final Logger logger;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final Consumer<WebSocketConnectionMessage> messageProcessor;
    private final AtomicBoolean isRunning;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackManager(
            Logger logger,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            BlockingQueue<ConnectionMessage> tableBlockingQueue,
            Consumer<ConnectionMessage> messageConsumer,
            Consumer<WebSocketConnectionMessage> messageProcessor)
    {
        this.logger = logger;
        this.sendMessageQueue = sendMessageQueue;
        this.tableBlockingQueue = tableBlockingQueue;
        connectionMessageConsumer = messageConsumer;
        this.messageProcessor = messageProcessor;
        this.isRunning = new AtomicBoolean();
    }

    @Override
    public void startAttack(int numberOfThreads)
    {
        isRunning.set(true);

        sendMessageExecutorService = Executors.newFixedThreadPool(numberOfThreads);
        sendMessageExecutorService.execute(
                new SendMessageQueueConsumer(
                        logger,
                        messageProcessor,
                        isRunning::get,
                        sendMessageQueue
                )
        );

        logger.logOutput(DEBUG, "Number of threads attack started with: " + numberOfThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        logger,
                        tableBlockingQueue,
                        isRunning::get,
                        connectionMessageConsumer
                )
        );

        logger.logOutput(DEBUG, "Table thread started.");
    }

    @Override
    public boolean isRunning()
    {
        return isRunning.get();
    }

    @Override
    public void stopAttack()
    {
        isRunning.set(false);

        sendMessageExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "sendMessageExecutorService shutdown? " + sendMessageExecutorService.isShutdown());

        tableExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "tableExecutorService shutdown? " + tableExecutorService.isShutdown());

        sendMessageQueue.clear();
        tableBlockingQueue.clear();
    }
}
