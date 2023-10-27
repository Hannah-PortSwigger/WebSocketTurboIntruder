package ui.attack;

import attack.AttackHandler;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import logger.Logger;
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
    private final UserInterface userInterface;
    private final CardLayout cardLayout;
    private final JPanel cardDeck;
    private final AtomicBoolean isProcessing;
    private WebSocketMessageTableModel messageTableModel;

    public WebSocketAttackPanel(
            Logger logger,
            UserInterface userInterface,
            CardLayout cardLayout,
            JPanel cardDeck,
            AttackHandler attackHandler,
            BlockingQueue<WebSocketConnectionMessage> sendMessageQueue,
            BlockingQueue<ConnectionMessage> tableBlockingQueue,
            AtomicBoolean isProcessing,
            AtomicBoolean isRunning
    )
    {
        super(new BorderLayout());

        this.userInterface = userInterface;
        this.cardLayout = cardLayout;
        this.cardDeck = cardDeck;
        this.isProcessing = isProcessing;

        initComponents();

        ExecutorService sendMessageExecutorService = Executors.newSingleThreadExecutor();
        sendMessageExecutorService.execute(new SendMessageQueueConsumer(logger, isProcessing, sendMessageQueue, attackHandler));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new TableBlockingQueueConsumer(logger, tableBlockingQueue, messageTableModel, isRunning));
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

        return new WebSocketMessageTable(messageTableModel, webSocketMessageEditor);
    }

    private WebSocketMessageEditor getWebSocketMessageEditor()
    {
        return userInterface.createWebSocketMessageEditor(EditorOptions.READ_ONLY);
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
