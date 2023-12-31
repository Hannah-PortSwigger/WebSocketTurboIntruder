package ui;

import attack.AttackHandler;
import burp.WebSocketFuzzer;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketFrame extends JFrame
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final Persistence persistence;
    private final WebSockets webSockets;
    private final WebSocketMessage webSocketMessage;
    private final AtomicBoolean isAttackRunning;
    private AttackHandler attackHandler;

    public WebSocketFrame(
            Logger logger,
            UserInterface userInterface,
            Persistence persistence,
            WebSockets webSockets,
            WebSocketMessage webSocketMessage
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.persistence = persistence;
        this.webSockets = webSockets;
        this.webSocketMessage = webSocketMessage;

        isAttackRunning = new AtomicBoolean(true);

        initComponents();

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                isAttackRunning.set(false);
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

        attackHandler = new AttackHandler(logger, webSockets, sendMessageQueue, tableBlockingQueue, webSocketMessageTableModel, isAttackRunning);

        cardDeck.add(new WebSocketEditorPanel(logger, userInterface, persistence, cardLayout, cardDeck, attackHandler, webSocketMessage), "editorPanel");
        cardDeck.add(new WebSocketAttackPanel(userInterface, cardLayout, cardDeck, attackHandler), "attackPanel");

        this.getContentPane().add(cardDeck);
        this.pack();
        this.toFront();
        this.setVisible(true);
    }
}
