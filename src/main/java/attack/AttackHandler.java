package attack;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.WebSockets;
import connection.ConnectionFactory;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AttackHandler
{
    private final Logger logger;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final AttackStatus attackStatus;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final AttackScriptExecutor scriptExecutor;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackHandler(
            Logger logger,
            WebSockets webSockets,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            BlockingQueue<ConnectionMessage> tableBlockingQueue,
            AttackStatus attackStatus,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        this.logger = logger;
        this.sendMessageQueue = sendMessageQueue;
        this.tableBlockingQueue = tableBlockingQueue;
        this.attackStatus = attackStatus;
        this.connectionMessageConsumer = messageConsumer;

        this.scriptExecutor = new AttackScriptExecutor(
                logger,
                tableBlockingQueue,
                new ConnectionFactory(logger, webSockets, sendMessageQueue, attackStatus)
        );
    }

    public void startAttack(String payload, HttpRequest upgradeRequest, String script)
    {
        scriptExecutor.startAttack(payload, upgradeRequest, script);
    }

    public void processMessage(WebSocketConnectionMessage webSocketConnectionMessage)
    {
        scriptExecutor.processMessage(webSocketConnectionMessage);
    }

    public void startConsumers(int numberOfSendThreads)
    {
        sendMessageExecutorService = Executors.newFixedThreadPool(numberOfSendThreads);
        sendMessageExecutorService.execute(new SendMessageQueueConsumer(logger, this, attackStatus, sendMessageQueue));

        logger.logOutput(LoggerLevel.DEBUG, "Number of threads attack started with: " + numberOfSendThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        logger,
                        tableBlockingQueue,
                        attackStatus,
                        connectionMessageConsumer
                )
        );

        logger.logOutput(LoggerLevel.DEBUG, "Table thread started.");
    }

    public void shutdownConsumers()
    {
        sendMessageExecutorService.shutdownNow();
        logger.logOutput(LoggerLevel.DEBUG, "sendMessageExecutorService shutdown? " + sendMessageExecutorService.isShutdown());

        tableExecutorService.shutdownNow();
        logger.logOutput(LoggerLevel.DEBUG, "tableExecutorService shutdown? " + tableExecutorService.isShutdown());

        sendMessageQueue.clear();
        tableBlockingQueue.clear();
    }
}