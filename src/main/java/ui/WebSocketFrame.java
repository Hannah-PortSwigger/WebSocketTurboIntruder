package ui;

import attack.AttackHandler;
import attack.AttackManager;
import burp.WebSocketFuzzer;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
import ui.attack.WebSocketAttackPanel;
import ui.attack.table.WebSocketMessageTableModel;
import ui.editor.WebSocketEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private final AttackManager attackManager;

    private AttackHandler attackHandler;

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

        attackManager = new AttackManager();

        initComponents();

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                attackManager.stop();
                attackHandler.shutdownConsumers();
            }
        });
    }

    private void initComponents()
    {
        String titleString = WebSocketFuzzer.EXTENSION_NAME + " - " + webSocketMessage.upgradeRequest().url();

        this.setTitle(titleString);
        this.setPreferredSize(new Dimension(800, 600));

        CardLayout cardLayout = new CardLayout();
        JPanel cardDeck = new JPanel(cardLayout);

        BlockingQueue<WebSocketConnectionMessage> sendMessageQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ConnectionMessage> tableBlockingQueue = new LinkedBlockingQueue<>();

        WebSocketMessageTableModel webSocketMessageTableModel = new WebSocketMessageTableModel();

        Consumer<ConnectionMessage> messageConsumer = connectionMessage -> invokeLater(() -> webSocketMessageTableModel.add(connectionMessage));

        attackHandler = new AttackHandler(
                logger,
                webSockets,
                sendMessageQueue,
                tableBlockingQueue,
                attackManager,
                messageConsumer
        );

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

        cardDeck.add(new WebSocketEditorPanel(logger, userInterface, fileLocationConfiguration, attackHandler, webSocketMessage, panelSwitcher), EDITOR_PANEL_NAME);
        cardDeck.add(new WebSocketAttackPanel(userInterface, attackHandler, attackManager, panelSwitcher, webSocketMessageTableModel), ATTACK_PANEL_NAME);

        this.getContentPane().add(cardDeck);
        this.pack();
        this.toFront();
        this.setVisible(true);
    }
}
