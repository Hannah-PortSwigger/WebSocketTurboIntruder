package ui.attack;

import attack.AttackHandler;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import ui.attack.table.WebSocketMessageTable;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketAttackPanel extends JPanel
{
    private final UserInterface userInterface;
    private final CardLayout cardLayout;
    private final JPanel cardDeck;
    private final AttackHandler attackHandler;
    private final AtomicBoolean isAttackRunning;

    public WebSocketAttackPanel(
            UserInterface userInterface,
            CardLayout cardLayout,
            JPanel cardDeck,
            AttackHandler attackHandler
    )
    {
        super(new BorderLayout());

        this.userInterface = userInterface;
        this.cardLayout = cardLayout;
        this.cardDeck = cardDeck;
        this.attackHandler = attackHandler;

        isAttackRunning = attackHandler.getIsAttackRunning();

        initComponents();
    }

    private void initComponents()
    {
        this.add(getWebSocketMessageDisplay(), BorderLayout.CENTER);
        this.add(getHaltConfigureButton(), BorderLayout.SOUTH);
    }

    private Component getWebSocketMessageDisplay()
    {
        WebSocketMessageEditor webSocketMessageEditor = getWebSocketMessageEditor();
        HttpRequestEditor upgradeRequestEditor = getUpgradeRequestEditor();

        JSplitPane webSocketInformationDisplay = new JSplitPane(JSplitPane.VERTICAL_SPLIT, webSocketMessageEditor.uiComponent(), upgradeRequestEditor.uiComponent());
        webSocketInformationDisplay.setResizeWeight(0.5);
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getWebSocketMessageTable(webSocketMessageEditor, upgradeRequestEditor), webSocketInformationDisplay);
    }

    private Component getWebSocketMessageTable(WebSocketMessageEditor webSocketMessageEditor, HttpRequestEditor upgradeRequestEditor)
    {
        return new WebSocketMessageTable(attackHandler.getWebSocketMessageTableModel(), webSocketMessageEditor, upgradeRequestEditor);
    }

    private WebSocketMessageEditor getWebSocketMessageEditor()
    {
        return userInterface.createWebSocketMessageEditor(EditorOptions.READ_ONLY);
    }

    private HttpRequestEditor getUpgradeRequestEditor()
    {
        return userInterface.createHttpRequestEditor(EditorOptions.READ_ONLY);
    }

    private Component getHaltConfigureButton()
    {
        JButton haltConfigureButton = new JButton("Halt");
        haltConfigureButton.addActionListener(l -> {
            if (isAttackRunning.get())
            {
                isAttackRunning.set(false);

                attackHandler.shutdownConsumers();

                haltConfigureButton.setText("Configure");
            }
            else
            {
                attackHandler.getWebSocketMessageTableModel().clear();

                haltConfigureButton.setText("Halt");

                isAttackRunning.set(true);

                cardLayout.show(cardDeck, "editorPanel");
            }
        });

        return haltConfigureButton;
    }
}
