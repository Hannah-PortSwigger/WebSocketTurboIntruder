package ui.attack;

import attack.AttackManager;
import attack.AttackStatus;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import ui.PanelSwitcher;
import ui.attack.table.WebSocketMessageTable;
import ui.attack.table.WebSocketMessageTableModel;

import javax.swing.*;
import java.awt.*;

public class WebSocketAttackPanel extends JPanel
{
    private final UserInterface userInterface;
    private final AttackManager attackManager;
    private final AttackStatus attackStatus;
    private final PanelSwitcher panelSwitcher;
    private final WebSocketMessageTableModel tableModel;

    public WebSocketAttackPanel(
            UserInterface userInterface,
            AttackManager attackManager,
            AttackStatus attackStatus,
            PanelSwitcher panelSwitcher,
            WebSocketMessageTableModel tableModel
    )
    {
        super(new BorderLayout());

        this.userInterface = userInterface;
        this.attackManager = attackManager;
        this.attackStatus = attackStatus;
        this.panelSwitcher = panelSwitcher;
        this.tableModel = tableModel;

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

        JSplitPane attackPanelSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getWebSocketMessageTable(webSocketMessageEditor, upgradeRequestEditor), webSocketInformationDisplay);
        attackPanelSplitPane.setResizeWeight(0.5);

        return attackPanelSplitPane;
    }

    private Component getWebSocketMessageTable(WebSocketMessageEditor webSocketMessageEditor, HttpRequestEditor upgradeRequestEditor)
    {
        return new WebSocketMessageTable(tableModel, webSocketMessageEditor, upgradeRequestEditor);
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
            if (attackStatus.isRunning())
            {
                attackManager.stopAttack();
                haltConfigureButton.setText("Configure");
            }
            else
            {
                tableModel.clear();

                haltConfigureButton.setText("Halt");
                panelSwitcher.showEditorPanel();
            }
        });

        return haltConfigureButton;
    }
}
