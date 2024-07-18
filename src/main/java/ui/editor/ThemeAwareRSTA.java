package ui.editor;

import burp.api.montoya.ui.Theme;
import burp.api.montoya.ui.UserInterface;
import logger.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.*;
import java.io.IOException;

class ThemeAwareRSTA extends RSyntaxTextArea
{
    private static final String LIGHT = "/org/fife/ui/rsyntaxtextarea/themes/default.xml";
    private static final String DARK = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";

    private final UserInterface userInterface;
    private final Logger logger;

    ThemeAwareRSTA(UserInterface userInterface, Logger logger, int rows, int columns)
    {
        super(rows, columns);

        this.userInterface = userInterface;
        this.logger = logger;

        applyThemeAndFont();
    }

    @Override
    public void updateUI()
    {
        super.updateUI();
        applyThemeAndFont();
    }

    private void applyThemeAndFont()
    {
        if (userInterface == null)
        {
            return;
        }

        try
        {
            String themePath = userInterface.currentTheme() == Theme.LIGHT ? LIGHT : DARK;

            org.fife.ui.rsyntaxtextarea.Theme rSyntaxTextAreaTheme = org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream(themePath));
            rSyntaxTextAreaTheme.apply(this);

            Font font = userInterface.currentEditorFont();
            setFont(font);
        }
        catch (IOException e)
        {
            logger.logError("Unable to apply themes.");
        }
    }
}
