package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.websocket.WebSockets;
import logger.Logger;
import logger.LoggerLevel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static utils.Utilities.closeAllFrames;

public class WebSocketFuzzer implements BurpExtension
{
    public static final String EXTENSION_NAME = "WebSocket Turbo Intruder";
    public static final String DEFAULT_SCRIPT_DIRECTORY = "/examples/";

    @Override
    public void initialize(MontoyaApi api)
    {
        Extension extension = api.extension();
        Persistence persistence = api.persistence();
        UserInterface userInterface = api.userInterface();
        Logging logging = api.logging();
        WebSockets websockets = api.websockets();

        List<JFrame> frameList = new ArrayList<>();

        extension.setName(EXTENSION_NAME);

        initializeDefaultDirectory(logging, persistence);

        Logger logger = new Logger(logging);
        JMenu menu = generateMenu(logger, persistence, frameList);
        userInterface.menuBar().registerMenu(menu);

        userInterface.registerContextMenuItemsProvider(new WebSocketContextMenuItemsProvider(logging, userInterface, persistence, websockets, frameList));

        extension.registerUnloadingHandler(new WebSocketExtensionUnloadingHandler(frameList));

        logging.logToOutput(EXTENSION_NAME + " - Loaded");
    }

    private void initializeDefaultDirectory(Logging logging, Persistence persistence)
    {
        if (persistence.preferences().getString("websocketsScriptsPath") == null)
        {
            persistence.preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY);
            logging.logToOutput("Default script directory initialized.");
        }
    }

    private JMenu generateMenu(Logger logger, Persistence persistence, List<JFrame> frameList)
    {
        JMenuItem resetDefaultScriptsMenuItem = new JMenuItem("Reset scripts directory to default.");
        resetDefaultScriptsMenuItem.addActionListener(l -> {
            persistence.preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY);
            logger.logOutput(LoggerLevel.DEBUG, "Scripts directory reset to " + DEFAULT_SCRIPT_DIRECTORY);
        });

        JMenuItem closeAllFramesMenuItem = new JMenuItem("Close all " + EXTENSION_NAME + " windows.");
        closeAllFramesMenuItem.addActionListener(l -> {
            closeAllFrames(frameList);
            logger.logOutput(LoggerLevel.DEBUG, "All " + EXTENSION_NAME + " windows closed.");
        });

        JMenu menu = new JMenu(EXTENSION_NAME);
        menu.add(resetDefaultScriptsMenuItem);
        menu.add(closeAllFramesMenuItem);

        return menu;
    }
}