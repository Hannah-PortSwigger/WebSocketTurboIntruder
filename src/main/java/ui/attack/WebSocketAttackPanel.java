package ui.attack;

import attack.AttackManager;
import attack.AttackStatus;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import data.ConnectionMessage;
import ui.PanelSwitcher;
import ui.attack.table.WebSocketMessageTable;
import ui.attack.table.WebSocketMessageTableModel;
import utils.IconFactory;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static burp.api.montoya.core.ByteArray.byteArray;
import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;

public class WebSocketAttackPanel extends JPanel
{
    private final UserInterface userInterface;
    private final AttackManager attackManager;
    private final AttackStatus attackStatus;
    private final PanelSwitcher panelSwitcher;
    private final WebSocketMessageTableModel tableModel;
    private final IconFactory iconFactory;

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
        this.iconFactory = new IconFactory(userInterface);

        initComponents();
    }

    private void initComponents()
    {
        WebSocketMessageEditor webSocketMessageEditor = userInterface.createWebSocketMessageEditor(READ_ONLY);
        HttpRequestEditor upgradeRequestEditor = userInterface.createHttpRequestEditor(READ_ONLY);

        JSplitPane webSocketInformationDisplay = new JSplitPane(
                VERTICAL_SPLIT,
                webSocketMessageEditor.uiComponent(),
                upgradeRequestEditor.uiComponent()
        );
        webSocketInformationDisplay.setResizeWeight(0.5);

        Consumer<ConnectionMessage> selectedMessageConsumer = connectionMessage -> {
            webSocketMessageEditor.setContents(byteArray(connectionMessage.getMessage()));
            upgradeRequestEditor.setRequest(connectionMessage.getConnection().upgradeRequest());
        };

        JSplitPane attackPanelSplitPane = new JSplitPane(
                HORIZONTAL_SPLIT,
                new WebSocketMessageTable(userInterface, tableModel, iconFactory, selectedMessageConsumer),
                webSocketInformationDisplay
        );
        attackPanelSplitPane.setResizeWeight(0.5);

        this.add(attackPanelSplitPane, CENTER);
        this.add(getHaltConfigureButton(), SOUTH);
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
