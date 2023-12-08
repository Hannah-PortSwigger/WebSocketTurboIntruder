package utils;

import burp.WebSocketFuzzer;
import burp.api.montoya.persistence.Persistence;
import logger.Logger;
import logger.LoggerLevel;

import javax.swing.*;

public class Utilities
{
    public static void initializeDefaultDirectory(Logger logger, Persistence persistence)
    {
        if (persistence.preferences().getString("websocketsScriptsPath") == null)
        {
            persistence.preferences().setString("websocketsScriptsPath", WebSocketFuzzer.DEFAULT_SCRIPT_DIRECTORY);
            logger.logOutput(LoggerLevel.DEBUG, "Default script directory initialized.");
        }
    }

    public static JMenu generateMenu(Logger logger, Persistence persistence, Runnable unloadAction)
    {
        JMenuItem resetDefaultScriptsMenuItem = new JMenuItem("Reset scripts directory to default.");
        resetDefaultScriptsMenuItem.addActionListener(l -> {
            persistence.preferences().setString("websocketsScriptsPath", WebSocketFuzzer.DEFAULT_SCRIPT_DIRECTORY);
            logger.logOutput(LoggerLevel.DEBUG, "Scripts directory reset to " + WebSocketFuzzer.DEFAULT_SCRIPT_DIRECTORY);
        });

        JMenuItem closeAllFramesMenuItem = new JMenuItem("Close all " + WebSocketFuzzer.EXTENSION_NAME + " windows.");
        closeAllFramesMenuItem.addActionListener(l -> unloadAction.run());

        JCheckBoxMenuItem loggingLevelDebug = new JCheckBoxMenuItem("Debug mode", logger.isDebugLogLevel());
        loggingLevelDebug.addActionListener(l -> logger.setDebugLogLevel(loggingLevelDebug.getState()));

        JMenu menu = new JMenu(WebSocketFuzzer.EXTENSION_NAME);
        menu.add(resetDefaultScriptsMenuItem);
        menu.add(closeAllFramesMenuItem);
        menu.add(loggingLevelDebug);

        return menu;
    }
}
