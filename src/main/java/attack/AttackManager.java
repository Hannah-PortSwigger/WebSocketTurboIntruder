package attack;

import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.PendingMessages;
import data.WebSocketConnectionMessage;
import logger.Logger;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static logger.LoggerLevel.DEBUG;

public class AttackManager implements AttackStarter, AttackStatus, AttackStopper
{
    private final Logger logger;
    private final PendingMessages pendingMessages;
    private final MessagesToDisplay messagesToDisplay;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final Consumer<WebSocketConnectionMessage> messageProcessor;
    private final AtomicBoolean isRunning;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackManager(
            Logger logger,
            PendingMessages pendingMessages,
            MessagesToDisplay messagesToDisplay,
            Consumer<ConnectionMessage> messageConsumer,
            Consumer<WebSocketConnectionMessage> messageProcessor)
    {
        this.logger = logger;
        this.pendingMessages = pendingMessages;
        this.messagesToDisplay = messagesToDisplay;
        this.connectionMessageConsumer = messageConsumer;
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
                        messageProcessor,
                        isRunning::get,
                        pendingMessages
                )
        );

        logger.logOutput(DEBUG, "Number of threads attack started with: " + numberOfThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        messagesToDisplay,
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

        pendingMessages.clear();
        messagesToDisplay.clear();
    }
}
