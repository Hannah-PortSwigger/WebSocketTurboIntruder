package ui;

import attack.AttackHandler;
import burp.WebSocketFuzzer;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import ui.attack.WebSocketAttackPanel;
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
    private final MontoyaApi api;
    private final WebSocketMessage webSocketMessage;
    private final AtomicBoolean isProcessing;
    private final AtomicBoolean isRunning;

    public WebSocketFrame(MontoyaApi api, WebSocketMessage webSocketMessage)
    {
        this.api = api;
        this.webSocketMessage = webSocketMessage;

        isProcessing = new AtomicBoolean(true);
        isRunning = new AtomicBoolean(true);

        initComponents();

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                isRunning.set(false);
            }
        });
    }

    private void initComponents()
    {
        //TODO decide which title is more useful to the user
        String titleString = WebSocketFuzzer.EXTENSION_NAME + " - " + webSocketMessage.upgradeRequest().httpService().host();
//            String titleString = WebSocketFuzzer.EXTENSION_NAME + " - " + webSocketMessage.upgradeRequest().url();

        this.setTitle(titleString);
        this.setPreferredSize(new Dimension(800, 600));

        CardLayout cardLayout = new CardLayout();
        JPanel cardDeck = new JPanel(cardLayout);

        BlockingQueue<WebSocketConnectionMessage> sendMessageQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ConnectionMessage> tableBlockingQueue = new LinkedBlockingQueue<>();

        AttackHandler attackHandler = new AttackHandler(api, isProcessing, sendMessageQueue, tableBlockingQueue, webSocketMessage);

        cardDeck.add(new WebSocketEditorPanel(api, cardLayout, cardDeck, attackHandler, tableBlockingQueue, webSocketMessage), "editorPanel");
        cardDeck.add(new WebSocketAttackPanel(api, cardLayout, cardDeck, attackHandler, sendMessageQueue, tableBlockingQueue, webSocketMessage, isProcessing, isRunning), "attackPanel");

        this.getContentPane().add(cardDeck);
        this.pack();
        this.toFront();
        this.setVisible(true);
    }
}
