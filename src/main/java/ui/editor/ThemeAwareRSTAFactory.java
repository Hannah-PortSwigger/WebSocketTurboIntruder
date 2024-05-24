package ui.editor;

import burp.api.montoya.ui.Theme;
import burp.api.montoya.ui.UserInterface;
import logger.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.IOException;

import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PYTHON;

public class ThemeAwareRSTAFactory
{
    private static final String PATH = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";

    private final UserInterface userInterface;
    private final Logger logger;

    public ThemeAwareRSTAFactory(UserInterface userInterface, Logger logger)
    {
        this.userInterface = userInterface;
        this.logger = logger;
    }

    RSyntaxTextArea build()
    {
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);

        RSyntaxTextArea codeEditor = new RSyntaxTextArea(20, 60);

        codeEditor.setEditable(true);
        codeEditor.setSyntaxEditingStyle(SYNTAX_STYLE_PYTHON);
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
                org.fife.ui.rsyntaxtextarea.Theme rSyntaxTextAreaTheme = org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream(PATH));
                rSyntaxTextAreaTheme.apply(codeEditor);
            }
            catch (IOException e)
            {
                logger.logError("Unable to apply dark theme.");
            }
        }

        return codeEditor;
    }
}
