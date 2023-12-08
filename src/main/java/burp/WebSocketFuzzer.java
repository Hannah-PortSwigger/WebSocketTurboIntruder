package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.websocket.WebSockets;
import logger.Logger;
import logger.LoggerLevel;

import static utils.Utilities.generateMenu;
import static utils.Utilities.initializeDefaultDirectory;

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
        WebSocketFuzzerFrames frames = new WebSocketFuzzerFrames(logger);

        initializeDefaultDirectory(logger, persistence);

        userInterface.menuBar().registerMenu(generateMenu(logger, persistence, frames::close));

        userInterface.registerContextMenuItemsProvider(
                new WebSocketContextMenuItemsProvider(
                        logger,
                        userInterface,
                        persistence,
                        websockets,
                        frames::add
                )
        );

        extension.registerUnloadingHandler(frames::close);

        extension.setName(EXTENSION_NAME);

        String extensionVersion = WebSocketFuzzer.class.getPackage().getImplementationVersion();
        logger.logOutput(LoggerLevel.DEFAULT, EXTENSION_NAME + " v" + extensionVersion);
    }
}