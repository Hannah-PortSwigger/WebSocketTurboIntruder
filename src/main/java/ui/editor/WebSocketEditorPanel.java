package ui.editor;

import attack.AttackStarter;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import config.FileLocationConfiguration;
import data.AttackDetails;
import data.InitialWebSocketMessage;
import logger.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import script.Script;
import script.ScriptLoaderFacade;
import ui.PanelSwitcher;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;

public class WebSocketEditorPanel extends JPanel
{
    private final UserInterface userInterface;
    private final InitialWebSocketMessage originalWebSocketMessage;
    private final ThemeAwareRSTAFactory rstaFactory;
    private final WebSocketEditorController controller;

    private JComboBox<Script> scriptComboBox;
    private WebSocketMessageEditor webSocketsMessageEditor;
    private HttpRequestEditor upgradeHttpMessageEditor;
    private JSpinner numberOfThreadsSpinner;

    public WebSocketEditorPanel(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            AttackStarter attackStarter,
            InitialWebSocketMessage originalWebSocketMessage,
            PanelSwitcher panelSwitcher
    )
    {
        super(new BorderLayout());

        this.userInterface = userInterface;
        this.originalWebSocketMessage = originalWebSocketMessage;
        ScriptLoaderFacade scriptLoader = new ScriptLoaderFacade(fileLocationConfiguration);
        this.rstaFactory = new ThemeAwareRSTAFactory(userInterface, logger);

        controller = new WebSocketEditorController(attackStarter, panelSwitcher, fileLocationConfiguration, scriptLoader);

        initComponents();
    }

    private void initComponents()
    {
        JSplitPane editableEditors = new JSplitPane(HORIZONTAL_SPLIT, getWebSocketMessageEditor(), getUpgradeHttpMessageEditor());
        editableEditors.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(VERTICAL_SPLIT, editableEditors, getPythonCodeEditor());
        splitPane.setResizeWeight(0.3);

        this.add(splitPane, BorderLayout.CENTER);
    }

    private Component getWebSocketMessageEditor()
    {
        webSocketsMessageEditor = userInterface.createWebSocketMessageEditor();
        webSocketsMessageEditor.setContents(originalWebSocketMessage.message());

        return webSocketsMessageEditor.uiComponent();
    }

    private Component getUpgradeHttpMessageEditor()
    {
        upgradeHttpMessageEditor = userInterface.createHttpRequestEditor();
        upgradeHttpMessageEditor.setRequest(originalWebSocketMessage.upgradeRequest());

        return upgradeHttpMessageEditor.uiComponent();
    }

    private Component getPythonCodeEditor()
    {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(getButtonPanel(), BorderLayout.NORTH);

        RSyntaxTextArea rSyntaxTextArea = rstaFactory.build();
        RTextScrollPane scrollableCodeEditor = new RTextScrollPane(rSyntaxTextArea);

        Script script = scriptComboBox.getItemAt(scriptComboBox.getSelectedIndex());
        rSyntaxTextArea.setText(script.content());

        scriptComboBox.addActionListener(l -> {
            Script newScript = scriptComboBox.getItemAt(scriptComboBox.getSelectedIndex());
            rSyntaxTextArea.setText(newScript.content());
        });

        scrollableCodeEditor.setLineNumbersEnabled(true);
        panel.add(scrollableCodeEditor, BorderLayout.CENTER);

        JButton attackButton = getAttackButton(rSyntaxTextArea);

        panel.add(attackButton, BorderLayout.SOUTH);

        return panel;
    }

    private Component getButtonPanel()
    {
        JPanel buttonPanel = new JPanel();

        scriptComboBox = new JComboBox<>(controller.loadScripts());
        buttonPanel.add(scriptComboBox);

        JButton selectScriptsDirectoryButton = getScriptsDirectoryButton();
        buttonPanel.add(selectScriptsDirectoryButton);

        JLabel threadsLabel = new JLabel("Number of threads:");
        buttonPanel.add(threadsLabel);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 50, 1);
        numberOfThreadsSpinner = new JSpinner(spinnerModel);
        buttonPanel.add(numberOfThreadsSpinner);

        return buttonPanel;
    }

    private JButton getScriptsDirectoryButton()
    {
        JButton selectScriptsDirectoryButton = new JButton("Choose scripts directory");
        selectScriptsDirectoryButton.addActionListener(l -> {
            JFileChooser scriptsFileChooser = new JFileChooser();
            scriptsFileChooser.setFileSelectionMode(DIRECTORIES_ONLY);

            int option = scriptsFileChooser.showOpenDialog(this);

            if (option == APPROVE_OPTION)
            {
                File file = scriptsFileChooser.getSelectedFile();
                Script[] scripts = controller.loadScripts(file);

                scriptComboBox.setModel(new DefaultComboBoxModel<>(scripts));
            }
        });

        return selectScriptsDirectoryButton;
    }

    private JButton getAttackButton(JTextComponent scriptTextComponent)
    {
        JButton attackButton = new JButton("Attack");
        attackButton.addActionListener(l -> {
            AttackDetails attackDetails = new AttackDetails(
                    (int) numberOfThreadsSpinner.getValue(),
                    webSocketsMessageEditor.getContents().toString(),
                    upgradeHttpMessageEditor.getRequest(),
                    scriptTextComponent.getText()
            );

            try
            {
                controller.startAttack(attackDetails);
            }
            catch (Exception e)
            {
                showMessageDialog(
                        this,
                        "Jython code error. Please review.\n" + e.getMessage(),
                        "Error",
                        ERROR_MESSAGE
                );
            }
        });

        return attackButton;
    }
}
