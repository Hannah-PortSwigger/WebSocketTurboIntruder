package attack;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.Direction;
import burp.api.montoya.websocket.WebSockets;
import connection.Connection;
import connection.ConnectionFactory;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
import logger.LoggerLevel;
import org.python.util.PythonInterpreter;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;
import queue.TableBlockingQueueProducer;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AttackHandler
{
    private final Logger logger;
    private final BlockingQueue<WebSocketConnectionMessage> sendMessageQueue;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final AtomicBoolean isAttackRunning;
    private final PythonInterpreter interpreter;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackHandler(
            Logger logger,
            WebSockets webSockets,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            BlockingQueue<ConnectionMessage> tableBlockingQueue,
            AtomicBoolean isAttackRunning,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        this.logger = logger;
        this.sendMessageQueue = sendMessageQueue;
        this.tableBlockingQueue = tableBlockingQueue;
        this.isAttackRunning = isAttackRunning;
        this.connectionMessageConsumer = messageConsumer;

        interpreter = new PythonInterpreter();

        interpreter.setOut(logger.outputStream());
        interpreter.setErr(logger.errorStream());

        interpreter.set("websocket_connection", new ConnectionFactory(logger, webSockets, sendMessageQueue, isAttackRunning));
        interpreter.set("results_table", new TableBlockingQueueProducer(logger, tableBlockingQueue));
    }

    public void executeJython(String payload, HttpRequest upgradeRequest, String editorCodeString)
    {
        interpreter.set("payload", payload);
        interpreter.set("upgrade_request", upgradeRequest);
        interpreter.exec(editorCodeString);
        interpreter.exec("queue_websockets(upgrade_request, payload)");
    }

    public void executeCallback(WebSocketConnectionMessage webSocketConnectionMessage)
    {
        String messageParameterName = "websocket_message";
        interpreter.set(messageParameterName, new DecoratedConnectionMessage(webSocketConnectionMessage));

        String callbackMethod = webSocketConnectionMessage.getDirection() == Direction.CLIENT_TO_SERVER
                ? "handle_outgoing_message"
                : "handle_incoming_message";

        interpreter.exec(String.format("%s(%s)", callbackMethod, messageParameterName));
    }

    public AtomicBoolean getIsAttackRunning()
    {
        return isAttackRunning;
    }

    public void startConsumers(int numberOfSendThreads)
    {
        sendMessageExecutorService = Executors.newFixedThreadPool(numberOfSendThreads);
        sendMessageExecutorService.execute(new SendMessageQueueConsumer(logger, this, isAttackRunning, sendMessageQueue));

        logger.logOutput(LoggerLevel.DEBUG, "Number of threads attack started with: " + numberOfSendThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        logger,
                        tableBlockingQueue,
                        isAttackRunning,
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

    private static class DecoratedConnectionMessage implements ConnectionMessage
    {
        private final WebSocketConnectionMessage webSocketConnectionMessage;

        DecoratedConnectionMessage(WebSocketConnectionMessage webSocketConnectionMessage)
        {
            this.webSocketConnectionMessage = webSocketConnectionMessage;
        }

        @Override
        public String getPayload()
        {
            return webSocketConnectionMessage.getPayload();
        }

        @Override
        public Direction getDirection()
        {
            return webSocketConnectionMessage.getDirection();
        }

        @Override
        public int getLength()
        {
            return webSocketConnectionMessage.getLength();
        }

        @Override
        public LocalDateTime getDateTime()
        {
            return webSocketConnectionMessage.getDateTime();
        }

        @Override
        public String getComment()
        {
            return webSocketConnectionMessage.getComment();
        }

        @Override
        public Connection getConnection()
        {
            return webSocketConnectionMessage.getConnection();
        }

        @Override
        public void setComment(String comment)
        {
            webSocketConnectionMessage.setComment(comment);
        }
    }
}
