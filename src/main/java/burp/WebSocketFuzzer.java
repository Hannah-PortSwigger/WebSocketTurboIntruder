package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.websocket.WebSockets;
import logger.Logger;
import logger.LoggerLevel;
import utils.Utilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        WebSockets websockets = api.websockets();

        Logger logger = new Logger(api.logging());

        Utilities.initializeDefaultDirectory(logger, persistence);

        List<JFrame> frameList = new ArrayList<>();

        JMenu menu = Utilities.generateMenu(logger, persistence, frameList);
        userInterface.menuBar().registerMenu(menu);

        userInterface.registerContextMenuItemsProvider(new WebSocketContextMenuItemsProvider(logger, userInterface, persistence, websockets, frameList));
        extension.registerUnloadingHandler(new WebSocketExtensionUnloadingHandler(frameList));

        extension.setName(EXTENSION_NAME);
        String extensionVersion = WebSocketFuzzer.class.getPackage().getImplementationVersion();
        logger.logOutput(LoggerLevel.DEFAULT, EXTENSION_NAME + " v" + extensionVersion);
    }
}