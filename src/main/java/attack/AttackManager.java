package attack;

import data.AttackDetails;
import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.PendingMessages;
import logger.Logger;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static logger.LoggerLevel.DEBUG;

public class AttackManager implements AttackStarter, AttackStopper
{
    private final Logger logger;
    private final PendingMessages pendingMessages;
    private final MessagesToDisplay messagesToDisplay;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final AttackScriptExecutor scriptExecutor;
    private final AttackState attackState;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackManager(
            Logger logger,
            PendingMessages pendingMessages,
            MessagesToDisplay messagesToDisplay,
            Consumer<ConnectionMessage> messageConsumer,
            AttackScriptExecutor scriptExecutor,
            AttackState attackState
    )
    {
        this.logger = logger;
        this.pendingMessages = pendingMessages;
        this.messagesToDisplay = messagesToDisplay;
        this.connectionMessageConsumer = messageConsumer;
        this.scriptExecutor = scriptExecutor;
        this.attackState = attackState;
    }

    @Override
    public void startAttack(AttackDetails attackDetails)
    {
        attackState.newAttack();

        sendMessageExecutorService = newFixedThreadPool(attackDetails.numberOfThreads());
        sendMessageExecutorService.execute(
                new SendMessageQueueConsumer(
                        scriptExecutor::processMessage,
                        attackState,
                        pendingMessages
                )
        );

        logger.logOutput(DEBUG, "Number of threads attack started with: " + attackDetails.numberOfThreads());

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        messagesToDisplay,
                        attackState,
                        connectionMessageConsumer
                )
        );

        logger.logOutput(DEBUG, "Table thread started.");

        scriptExecutor.startAttack(attackDetails.payload(), attackDetails.upgradeRequest(), attackDetails.script());
    }

    @Override
    public void stopAttack()
    {
        attackState.endAttack();

        scriptExecutor.stopAttack();

        sendMessageExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "sendMessageExecutorService shutdown? " + sendMessageExecutorService.isShutdown());

        tableExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "tableExecutorService shutdown? " + tableExecutorService.isShutdown());

        pendingMessages.clear();
        messagesToDisplay.clear();
    }
}
