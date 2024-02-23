package ui;

import attack.AttackManager;
import attack.AttackScriptExecutor;
import attack.AttackStatus;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import data.MessagesToDisplay;
import data.PendingMessages;
import logger.Logger;
import python.ConnectionFactory;
import ui.attack.table.WebSocketMessageTableModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

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

    public WebSocketFrame from(WebSocketMessage webSocketMessage)
    {
        AtomicReference<AttackManager> attackManagerReference = new AtomicReference<>();
        AttackStatus attackStatus = () -> attackManagerReference.get().isRunning();

        MessagesToDisplay messagesToDisplay = new MessagesToDisplay(logger, attackStatus);
        PendingMessages pendingMessages = new PendingMessages(logger, attackStatus);

        AttackScriptExecutor scriptExecutor = new AttackScriptExecutor(
                logger,
                messagesToDisplay,
                new ConnectionFactory(
                        logger,
                        webSockets,
                        pendingMessages
                )
        );

        WebSocketMessageTableModel webSocketMessageTableModel = new WebSocketMessageTableModel();

        AttackManager attackManager = new AttackManager(
                logger,
                pendingMessages,
                messagesToDisplay,
                webSocketMessageTableModel::add,
                scriptExecutor::processMessage
        );

        attackManagerReference.set(attackManager);

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
