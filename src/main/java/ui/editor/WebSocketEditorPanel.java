package ui.editor;

import attack.AttackScriptExecutor;
import attack.AttackStarter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Theme;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import config.FileLocationConfiguration;
import logger.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import script.Script;
import script.ScriptLoaderFacade;
import ui.PanelSwitcher;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;

public class WebSocketEditorPanel extends JPanel
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final AttackStarter attackStarter;
    private final AttackScriptExecutor scriptExecutor;
    private final WebSocketMessage originalWebSocketMessage;
    private final PanelSwitcher panelSwitcher;
    private final ScriptLoaderFacade scriptLoader;
    private JComboBox<Script> scriptComboBox;
    private WebSocketMessageEditor webSocketsMessageEditor;
    private HttpRequestEditor upgradeHttpMessageEditor;
    private JSpinner numberOfThreadsSpinner;

    public WebSocketEditorPanel(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            AttackStarter attackStarter,
            AttackScriptExecutor scriptExecutor,
            WebSocketMessage originalWebSocketMessage,
            PanelSwitcher panelSwitcher
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.attackStarter = attackStarter;
        this.scriptExecutor = scriptExecutor;
        this.originalWebSocketMessage = originalWebSocketMessage;
        this.panelSwitcher = panelSwitcher;
        this.scriptLoader = new ScriptLoaderFacade(fileLocationConfiguration);

        this.setLayout(new BorderLayout());

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
        webSocketsMessageEditor.setContents(originalWebSocketMessage.payload());

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

        RSyntaxTextArea rSyntaxTextArea = getRSyntaxTextArea();
        RTextScrollPane scrollableCodeEditor = new RTextScrollPane(rSyntaxTextArea);

        scriptComboBox.addActionListener(l -> {
            Script script = scriptComboBox.getItemAt(scriptComboBox.getSelectedIndex());

            rSyntaxTextArea.setText(script.content());
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

        scriptComboBox = getScriptComboBox();
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

    private JComboBox<Script> getScriptComboBox()
    {
        List<Script> scriptList = scriptLoader.loadScripts();

        return new JComboBox<>(scriptList.toArray(new Script[0]));
    }

    private JButton getScriptsDirectoryButton()
    {
        JButton selectScriptsDirectoryButton = new JButton("Choose scripts directory");
        selectScriptsDirectoryButton.addActionListener(l -> {
            JFileChooser scriptsFileChooser = new JFileChooser();
            scriptsFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = scriptsFileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION)
            {
                File file = scriptsFileChooser.getSelectedFile();
                fileLocationConfiguration.setWebSocketScriptPath(file.getAbsolutePath());

                int originalSize = scriptComboBox.getItemCount();

                List<Script> scriptList = scriptLoader.loadScripts();

                for (Script script : scriptList)
                {
                    scriptComboBox.addItem(script);
                }

                for (int i=0; i < originalSize; i++)
                {
                    scriptComboBox.removeItemAt(0);
                }
            }
        });

        return selectScriptsDirectoryButton;
    }

    private RSyntaxTextArea getRSyntaxTextArea()
    {
        javax.swing.text.JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);

        RSyntaxTextArea codeEditor = new RSyntaxTextArea(20, 60);

        codeEditor.setEditable(true);
        codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        codeEditor.setAntiAliasingEnabled(true);
        codeEditor.setAutoIndentEnabled(true);
        codeEditor.setPaintTabLines(true);
        codeEditor.setTabSize(4);
        codeEditor.setTabsEmulated(true);
        codeEditor.setEOLMarkersVisible(false);
        codeEditor.setWhitespaceVisible(false);

        if (userInterface.currentTheme() == Theme.DARK)
        {
            try
            {
                org.fife.ui.rsyntaxtextarea.Theme rSyntaxTextAreaTheme = org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
                rSyntaxTextAreaTheme.apply(codeEditor);
            }
            catch (IOException e)
            {
                logger.logError("Unable to apply dark theme.");
            }
        }

        return codeEditor;
    }

    private JButton getAttackButton(JTextComponent scriptTextComponent)
    {
        JButton attackButton = new JButton("Attack");
        attackButton.addActionListener(l -> {
            String payload = webSocketsMessageEditor.getContents().toString();
            HttpRequest upgradeRequest = upgradeHttpMessageEditor.getRequest();

            String script = scriptTextComponent.getText();

            attackStarter.startAttack((int) numberOfThreadsSpinner.getValue());

            newSingleThreadExecutor().submit(() ->
            {
                try
                {
                    scriptExecutor.startAttack(payload, upgradeRequest, script);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(this, "Jython code error. Please review.\r\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                    logger.logError("Jython code error. Please review.\r\n" + e);
                }
            });

            panelSwitcher.showAttackPanel();
        });

        return attackButton;
    }
}
