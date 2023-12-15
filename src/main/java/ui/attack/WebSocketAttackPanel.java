package ui.attack;

import attack.AttackHandler;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import ui.PanelSwitcher;
import ui.attack.table.WebSocketMessageTable;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketAttackPanel extends JPanel
{
    private final UserInterface userInterface;
    private final AttackHandler attackHandler;
    private final AtomicBoolean isAttackRunning;
    private final PanelSwitcher panelSwitcher;

    public WebSocketAttackPanel(
            UserInterface userInterface,
            AttackHandler attackHandler,
            PanelSwitcher panelSwitcher)
    {
        super(new BorderLayout());

        this.userInterface = userInterface;
        this.attackHandler = attackHandler;

        isAttackRunning = attackHandler.getIsAttackRunning();
        this.panelSwitcher = panelSwitcher;

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
                panelSwitcher.showEditorPanel();
            }
        });

        return haltConfigureButton;
    }
}
