package ui;

import attack.AttackManager;
import attack.AttackStatus;
import burp.api.montoya.ui.UserInterface;
import config.FileLocationConfiguration;
import data.InitialWebSocketMessage;
import logger.Logger;
import ui.attack.WebSocketAttackPanel;
import ui.attack.table.WebSocketMessageTableModel;
import ui.editor.WebSocketEditorPanel;

import javax.swing.*;
import java.awt.*;

import static burp.WebSocketFuzzer.EXTENSION_NAME;
import static javax.swing.SwingUtilities.invokeLater;

public class WebSocketFrame extends JFrame
{
    private static final String ATTACK_PANEL_NAME = "attackPanel";
    private static final String EDITOR_PANEL_NAME = "editorPanel";
    private static final double WIDTH_RATIO = 0.5;
    private static final double HEIGHT_RATIO = 0.65;

    WebSocketFrame(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            InitialWebSocketMessage webSocketMessage,
            WebSocketMessageTableModel webSocketMessageTableModel,
            AttackManager attackManager,
            AttackStatus attackStatus)
    {
        String titleString = EXTENSION_NAME + " - " + webSocketMessage.upgradeRequest().url();

        setTitle(titleString);

        Frame burpFrame = userInterface.swingUtils().suiteFrame();

        Dimension dimension = burpFrame.getSize();

        int width = (int) (dimension.width * WIDTH_RATIO);
        int height = (int) (dimension.height * HEIGHT_RATIO);

        setPreferredSize(new Dimension(width, height));

        CardLayout cardLayout = new CardLayout();
        JPanel cardDeck = new JPanel(cardLayout);

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
                        webSocketMessage,
                        panelSwitcher
                ),
                EDITOR_PANEL_NAME
        );

        cardDeck.add(
                new WebSocketAttackPanel(
                        userInterface,
                        attackManager,
                        attackStatus,
                        panelSwitcher,
                        webSocketMessageTableModel
                ),
                ATTACK_PANEL_NAME
        );

        this.getContentPane().add(cardDeck);
        this.pack();
        this.toFront();
        this.setLocationRelativeTo(burpFrame);
        this.setVisible(true);
    }
}
