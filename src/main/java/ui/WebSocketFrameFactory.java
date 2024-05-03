package ui;

import attack.AttackManager;
import attack.AttackScriptExecutor;
import attack.AttackStatus;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import data.InitialWebSocketMessage;
import data.MessagesToDisplay;
import data.PendingMessages;
import logger.Logger;
import python.ConnectionFactoryFactory;
import ui.attack.table.WebSocketMessageTableModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketFrameFactory
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final WebSockets webSockets;

    public WebSocketFrameFactory(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            WebSockets webSockets
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.webSockets = webSockets;
    }

    public WebSocketFrame from(InitialWebSocketMessage webSocketMessage)
    {
        AtomicBoolean isAttackRunning = new AtomicBoolean();
        AtomicInteger attackId = new AtomicInteger();

        AttackStatus attackStatus = new AttackStatus() //TODO object?
        {
            @Override
            public boolean isRunning()
            {
                return isAttackRunning.get();
            }

            @Override
            public boolean isCurrentAttackId(int id)
            {
                return attackId.get() == id;
            }
        };

        MessagesToDisplay messagesToDisplay = new MessagesToDisplay(logger, attackStatus);
        PendingMessages pendingMessages = new PendingMessages(logger, attackStatus);

        AttackScriptExecutor scriptExecutor = new AttackScriptExecutor(
                logger,
                messagesToDisplay,
                new ConnectionFactoryFactory(
                        logger,
                        webSockets,
                        pendingMessages,
                        attackId::get
                ),
                attackId
        );

        WebSocketMessageTableModel webSocketMessageTableModel = new WebSocketMessageTableModel();

        AttackManager attackManager = new AttackManager(
                logger,
                pendingMessages,
                messagesToDisplay,
                webSocketMessageTableModel::add,
                scriptExecutor,
                attackStatus,
                isAttackRunning
        );

        WebSocketFrame webSocketFrame = new WebSocketFrame(
                logger,
                userInterface,
                fileLocationConfiguration,
                webSocketMessage,
                scriptExecutor,
                webSocketMessageTableModel,
                attackManager
        );

        webSocketFrame.addWindowListener(
                new WindowAdapter()
                {
                    @Override
                    public void windowClosed(WindowEvent e)
                    {
                        attackManager.stopAttack();
                    }
                }
        );

        return webSocketFrame;
    }
}
