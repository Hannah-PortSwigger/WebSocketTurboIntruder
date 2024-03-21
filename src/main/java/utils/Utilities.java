package utils;

import burp.WebSocketFuzzer;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import config.FileLocationConfiguration;
import logger.Logger;

import javax.swing.*;

import static burp.api.montoya.core.ByteArray.byteArray;

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

    public static ByteArray insertPlaceholder(ByteArray initialPayload, Range range, String placeholder)
    {
        ByteArray prependArr = range.startIndexInclusive() == 0 ? byteArray() : initialPayload.subArray(0, range.startIndexInclusive());
        ByteArray percentArr = byteArray(placeholder);
        ByteArray postpendArr = range.endIndexExclusive() == initialPayload.length() ? byteArray() : initialPayload.subArray(range.endIndexExclusive(), initialPayload.length());

        return prependArr.withAppended(percentArr).withAppended(postpendArr);
    }
}
