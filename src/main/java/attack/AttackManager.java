package attack;

import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.PendingMessages;
import logger.Logger;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static logger.LoggerLevel.DEBUG;

public class AttackManager implements AttackStarter, AttackStopper
{
    private final Logger logger;
    private final PendingMessages pendingMessages;
    private final MessagesToDisplay messagesToDisplay;
    private final Consumer<ConnectionMessage> connectionMessageConsumer;
    private final AttackScriptExecutor scriptExecutor;
    private final AttackStatus attackStatus;
    private final AtomicBoolean isRunning;

    private ExecutorService sendMessageExecutorService;
    private ExecutorService tableExecutorService;

    public AttackManager(
            Logger logger,
            PendingMessages pendingMessages,
            MessagesToDisplay messagesToDisplay,
            Consumer<ConnectionMessage> messageConsumer,
            AttackScriptExecutor scriptExecutor,
            AttackStatus attackStatus,
            AtomicBoolean isAttackRunning
    )
    {
        this.logger = logger;
        this.pendingMessages = pendingMessages;
        this.messagesToDisplay = messagesToDisplay;
        this.connectionMessageConsumer = messageConsumer;
        this.scriptExecutor = scriptExecutor;
        this.attackStatus = attackStatus;
        this.isRunning = isAttackRunning;
    }

    @Override
    public void startAttack(int numberOfThreads)
    {
        isRunning.set(true);

        sendMessageExecutorService = Executors.newFixedThreadPool(numberOfThreads);
        sendMessageExecutorService.execute(
                new SendMessageQueueConsumer(
                        scriptExecutor::processMessage,
                        attackStatus,
                        pendingMessages
                )
        );

        logger.logOutput(DEBUG, "Number of threads attack started with: " + numberOfThreads);

        tableExecutorService = Executors.newSingleThreadExecutor();
        tableExecutorService.execute(
                new TableBlockingQueueConsumer(
                        messagesToDisplay,
                        attackStatus,
                        connectionMessageConsumer
                )
        );

        logger.logOutput(DEBUG, "Table thread started.");
    }

    public boolean isAttackRunning()
    {
        return attackStatus.isRunning();
    }

    @Override
    public void stopAttack()
    {
        isRunning.set(false);

        scriptExecutor.stopAttack();

        sendMessageExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "sendMessageExecutorService shutdown? " + sendMessageExecutorService.isShutdown());

        tableExecutorService.shutdownNow();
        logger.logOutput(DEBUG, "tableExecutorService shutdown? " + tableExecutorService.isShutdown());

        pendingMessages.clear();
        messagesToDisplay.clear();
    }
}
