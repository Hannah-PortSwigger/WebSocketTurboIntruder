package ui;

import attack.AttackManager;
import attack.AttackScriptExecutor;
import attack.AttackStatus;
import burp.WebSocketFuzzer;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.PendingMessages;
import data.WebSocketConnectionMessage;
import logger.Logger;
import python.ConnectionFactory;
import ui.attack.WebSocketAttackPanel;
import ui.attack.table.WebSocketMessageTableModel;
import ui.editor.WebSocketEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javax.swing.SwingUtilities.invokeLater;

public class WebSocketFrame extends JFrame
{
    private static final String ATTACK_PANEL_NAME = "attackPanel";
    private static final String EDITOR_PANEL_NAME = "editorPanel";

    private final Logger logger;
    private final UserInterface userInterface;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final WebSockets webSockets;
    private final WebSocketMessage webSocketMessage;

    private AttackManager attackManager;

    public WebSocketFrame(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            WebSockets webSockets,
            WebSocketMessage webSocketMessage
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.webSockets = webSockets;
        this.webSocketMessage = webSocketMessage;

        initComponents();

        this.addWindowListener(
            new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent e)
                {
                    attackManager.stopAttack();
                }
            }
        );
    }

    private void initComponents()
    {
        String titleString = WebSocketFuzzer.EXTENSION_NAME + " - " + webSocketMessage.upgradeRequest().url();

        this.setTitle(titleString);
        this.setPreferredSize(new Dimension(800, 600));

        CardLayout cardLayout = new CardLayout();
        JPanel cardDeck = new JPanel(cardLayout);

        AtomicReference<AttackManager> attackManagerReference = new AtomicReference<>(); //TODO can we get rid of circular dependency?

        AttackStatus attackStatus = () -> attackManagerReference.get().isRunning();

        PendingMessages pendingMessages = new PendingMessages(logger, attackStatus);
        MessagesToDisplay messagesToDisplay = new MessagesToDisplay(logger, attackStatus);

        WebSocketMessageTableModel webSocketMessageTableModel = new WebSocketMessageTableModel();

        Consumer<ConnectionMessage> messageConsumer = connectionMessage -> invokeLater(() -> webSocketMessageTableModel.add(connectionMessage));


        AttackScriptExecutor scriptExecutor = new AttackScriptExecutor(
                logger,
                messagesToDisplay,
                new ConnectionFactory(
                        logger,
                        webSockets,
                        pendingMessages
                )
        );

        Consumer<WebSocketConnectionMessage> messageProcessor = scriptExecutor::processMessage;

        attackManager = new AttackManager(
                logger,
                pendingMessages,
                messagesToDisplay,
                messageConsumer,
                messageProcessor
        );

        attackManagerReference.set(attackManager);

        PanelSwitcher panelSwitcher = new PanelSwitcher()
        {
            @Override
            public void showAttackPanel()
            {
                showPanel(ATTACK_PANEL_NAME);
            }

            @Override
            public void showEditorPanel()
            {
                showPanel(EDITOR_PANEL_NAME);
            }

            private void showPanel(String panelName)
            {
                invokeLater(() -> cardLayout.show(cardDeck, panelName));
            }
        };

        cardDeck.add(
                new WebSocketEditorPanel(
                        logger,
                        userInterface,
                        fileLocationConfiguration,
                        attackManager,
                        scriptExecutor,
                        webSocketMessage,
                        panelSwitcher
                ),
                EDITOR_PANEL_NAME
        );

        cardDeck.add(
                new WebSocketAttackPanel(
                        userInterface,
                        attackManager,
                        panelSwitcher,
                        webSocketMessageTableModel
                ),
                ATTACK_PANEL_NAME
        );

        this.getContentPane().add(cardDeck);
        this.pack();
        this.toFront();
        this.setVisible(true);
    }
}
