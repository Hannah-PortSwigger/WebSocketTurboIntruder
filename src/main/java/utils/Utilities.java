package utils;

import burp.WebSocketFuzzer;
import config.FileLocationConfiguration;
import logger.Logger;

import javax.swing.*;

public class Utilities
{
    public static JMenu generateMenu(Logger logger, FileLocationConfiguration fileLocationConfiguration, Runnable unloadAction)
    {
        JMenuItem resetDefaultScriptsMenuItem = new JMenuItem("Reset scripts directory to default.");
        resetDefaultScriptsMenuItem.addActionListener(l -> fileLocationConfiguration.setWebSocketScriptPath());

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
