package ui.attack;

import attack.AttackHandler;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import queue.SendMessageQueueConsumer;
import queue.TableBlockingQueueConsumer;
import ui.attack.table.WebSocketMessageTable;
import ui.attack.table.WebSocketMessageTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketAttackPanel extends JPanel
{
    private final MontoyaApi api;
    private final CardLayout cardLayout;
    private final JPanel cardDeck;
    private final WebSocketMessage baseWebSocketMessage;
    private final AtomicBoolean isProcessing;
    private WebSocketMessageTableModel messageTableModel;

    public WebSocketAttackPanel(MontoyaApi api, CardLayout cardLayout, JPanel cardDeck, AttackHandler attackHandler, BlockingQueue<WebSocketConnectionMessage> sendMessageQueue, BlockingQueue<ConnectionMessage> tableBlockingQueue, WebSocketMessage baseWebSocketMessage, AtomicBoolean isProcessing, AtomicBoolean isRunning)
    {
        super(new BorderLayout());

        this.api = api;
        this.cardLayout = cardLayout;
        this.cardDeck = cardDeck;
        this.baseWebSocketMessage = baseWebSocketMessage;
        this.isProcessing = isProcessing;

        initComponents();

        ExecutorService sendMessageExecutorService = Executors.newSingleThreadExecutor();
        sendMessageExecutorService.execute(new SendMessageQueueConsumer(api, isProcessing, sendMessageQueue, attackHandler));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new TableBlockingQueueConsumer(api, tableBlockingQueue, messageTableModel, isRunning));
    }

    private void initComponents()
    {
        this.add(getWebSocketMessageDisplay(), BorderLayout.CENTER);
        this.add(getHaltConfigureButton(), BorderLayout.SOUTH);
    }

    private Component getWebSocketMessageDisplay()
    {
        WebSocketMessageEditor webSocketMessageEditor = getWebSocketMessageEditor();
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getWebSocketMessageTable(webSocketMessageEditor), webSocketMessageEditor.uiComponent());
    }

    private Component getWebSocketMessageTable(WebSocketMessageEditor webSocketMessageEditor)
    {
        messageTableModel = new WebSocketMessageTableModel();
        WebSocketMessageTable webSocketMessageTable = new WebSocketMessageTable(messageTableModel, webSocketMessageEditor);

        return webSocketMessageTable;
    }

    private WebSocketMessageEditor getWebSocketMessageEditor()
    {
        return api.userInterface().createWebSocketMessageEditor(EditorOptions.READ_ONLY);
    }

    private Component getHaltConfigureButton()
    {
        JButton haltConfigureButton = new JButton("Halt");
        haltConfigureButton.addActionListener(l -> {
            if (isProcessing.get())
            {
                isProcessing.set(false);
                haltConfigureButton.setText("Configure");
            }
            else
            {
                cardLayout.show(cardDeck, "editorPanel");
                messageTableModel.clear();

                haltConfigureButton.setText("Halt");

                isProcessing.set(true);
            }
        });

        return haltConfigureButton;
    }
}
