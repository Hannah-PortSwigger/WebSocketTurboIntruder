package attack;

import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static logger.LoggerLevel.DEBUG;

public class AttackHandler
{
    private final Logger logger;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final AttackStatus attackStatus;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final Consumer<WebSocketConnectionMessage> messageProcessor;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackHandler(
            Logger logger,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            BlockingQueue<ConnectionMessage> tableBlockingQueue,
            AttackStatus attackStatus,
            Consumer<ConnectionMessage> messageConsumer,
            Consumer<WebSocketConnectionMessage> messageProcessor)
    {
        this.logger = logger;
        this.sendMessageQueue = sendMessageQueue;
        this.tableBlockingQueue = tableBlockingQueue;
        this.attackStatus = attackStatus;
        this.connectionMessageConsumer = messageConsumer;
        this.messageProcessor = messageProcessor;
    }

    public void startConsumers(int numberOfSendThreads)
    {
        sendMessageExecutorService = Executors.newFixedThreadPool(numberOfSendThreads);
        sendMessageExecutorService.execute(new SendMessageQueueConsumer(logger, messageProcessor, attackStatus, sendMessageQueue));

        logger.logOutput(DEBUG, "Number of threads attack started with: " + numberOfSendThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        logger,
                        tableBlockingQueue,
                        attackStatus,
                        connectionMessageConsumer
                )
        );

        logger.logOutput(DEBUG, "Table thread started.");
    }

    public void shutdownConsumers()
    {
        sendMessageExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "sendMessageExecutorService shutdown? " + sendMessageExecutorService.isShutdown());

        tableExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "tableExecutorService shutdown? " + tableExecutorService.isShutdown());

        sendMessageQueue.clear();
        tableBlockingQueue.clear();
    }
}