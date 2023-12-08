package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.persistence.Preferences;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import logger.Logger;
import logger.LoggerLevel;

import static utils.Utilities.generateMenu;

public class WebSocketFuzzer implements BurpExtension
{
    public static final String EXTENSION_NAME = "WebSocket Turbo Intruder";

    @Override
    public void initialize(MontoyaApi api)
    {
        Extension extension = api.extension();
        Preferences preferences = api.persistence().preferences();
        UserInterface userInterface = api.userInterface();
        WebSockets websockets = api.websockets();

        Logger logger = new Logger(api.logging());
        WebSocketFuzzerFrames frames = new WebSocketFuzzerFrames(logger);

        FileLocationConfiguration fileLocationConfiguration = new FileLocationConfiguration(logger, preferences);

        userInterface.menuBar().registerMenu(generateMenu(logger, fileLocationConfiguration, frames::close));

        userInterface.registerContextMenuItemsProvider(
                new WebSocketContextMenuItemsProvider(
                        logger,
                        userInterface,
                        fileLocationConfiguration,
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