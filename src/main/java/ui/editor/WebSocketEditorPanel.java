package ui.editor;

import attack.AttackHandler;
import burp.WebSocketFuzzer;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.Theme;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import logger.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class WebSocketEditorPanel extends JPanel
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final Persistence persistence;
    private final CardLayout cardLayout;
    private final JPanel cardDeck;
    private final AttackHandler attackHandler;
    private final WebSocketMessage originalWebSocketMessage;
    private JComboBox<Path> scriptComboBox;
    private WebSocketMessageEditor webSocketsMessageEditor;
    private HttpRequestEditor upgradeHttpMessageEditor;
    private JSpinner numberOfThreadsSpinner;

    public WebSocketEditorPanel(
            Logger logger,
            UserInterface userInterface,
            Persistence persistence,
            CardLayout cardLayout,
            JPanel cardDeck,
            AttackHandler attackHandler,
            WebSocketMessage originalWebSocketMessage)
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.persistence = persistence;
        this.cardLayout = cardLayout;
        this.cardDeck = cardDeck;
        this.attackHandler = attackHandler;
        this.originalWebSocketMessage = originalWebSocketMessage;

        this.setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents()
    {
        JSplitPane editableEditors = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getWebSocketMessageEditor(), getUpgradeHttpMessageEditor());
        editableEditors.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editableEditors, getPythonCodeEditor());
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
            Path path = scriptComboBox.getItemAt(scriptComboBox.getSelectedIndex());

            if (!path.toString().contains(".py"))
            {
                rSyntaxTextArea.setText(null);
            }
            else if (path.toString().startsWith(WebSocketFuzzer.DEFAULT_SCRIPT_DIRECTORY))
            {
                String data = null;

                try (InputStream stream = WebSocketEditorPanel.class.getResourceAsStream(path.toString()))
                {
                    if (stream != null)
                    {
                        data = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    }
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

                rSyntaxTextArea.setText(data);
            }
            else
            {
                String content;
                try
                {
                    content = Files.readString(path);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

                rSyntaxTextArea.setText(content);
            }
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

    private JComboBox<Path> getScriptComboBox()
    {
        List<Path> pathList = getPathList();

        return new JComboBox<>(pathList.toArray(Path[]::new));
    }

    private List<Path> getPathList()
    {
        String websocketScriptsPath = persistence.preferences().getString("websocketsScriptsPath");
        List<Path> pathList = new ArrayList<>();

        if (WebSocketFuzzer.DEFAULT_SCRIPT_DIRECTORY.equals(websocketScriptsPath))
        {
            URL url = WebSocketEditorPanel.class.getResource(websocketScriptsPath);
            if (url != null)
            {
                Stream<Path> stream  = null;
                try
                {
                    URI uri = url.toURI();

                    try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                        stream = Files.walk(Paths.get(uri));

                        stream.forEach(pathList::add);
                    }
                } catch (IOException | URISyntaxException e)
                {
                    throw new RuntimeException(e);
                } finally
                {
                    if (stream != null)
                    {
                        stream.close();
                    }
                }
            }
        }
        else
        {
            try (Stream<Path> stream = Files.walk(Paths.get(websocketScriptsPath)))
            {
                stream.forEach(pathList::add);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return pathList;
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
                persistence.preferences().setString("websocketsScriptsPath", file.getAbsolutePath());

                int originalSize = scriptComboBox.getItemCount();

                List<Path> pathList = getPathList();

                for (Path path : pathList)
                {
                    scriptComboBox.addItem(path);
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

    private JButton getAttackButton(RSyntaxTextArea rSyntaxTextArea)
    {
        JButton attackButton = new JButton("Attack");
        attackButton.addActionListener(l -> {
            String payload = webSocketsMessageEditor.getContents().toString();
            HttpRequest upgradeRequest = upgradeHttpMessageEditor.getRequest();

            String jythonCode = rSyntaxTextArea.getText();

            new Thread(() -> {
                try
                {
                    attackHandler.executeJython(payload, upgradeRequest, jythonCode);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(this, "Jython code error. Please review.\r\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                    logger.logError("Jython code error. Please review.\r\n" + e);
                }
            }).start();

            attackHandler.startConsumers((int) numberOfThreadsSpinner.getValue());

            SwingUtilities.invokeLater(() -> cardLayout.show(cardDeck, "attackPanel"));
        });

        return attackButton;
    }
}
