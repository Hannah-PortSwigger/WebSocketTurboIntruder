package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;
import burp.api.montoya.websocket.WebSockets;

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

        extension.setName(EXTENSION_NAME);

        List<JFrame> frameList = new ArrayList<>();


        if (persistence.preferences().getString("websocketsScriptsPath") == null)
        {
            persistence.preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY);
        }

        MenuItem resetDefaultScriptsMenuItem = BasicMenuItem
                .basicMenuItem("Reset scripts directory to default.")
                .withAction(() -> persistence.preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY));

        MenuItem closeAllFramesMenuItem = BasicMenuItem
                .basicMenuItem("Close all " + EXTENSION_NAME + " windows.")
                .withAction(() -> closeAllFrames(frameList));

        userInterface.menuBar().registerMenu(Menu.menu(EXTENSION_NAME).withMenuItems(resetDefaultScriptsMenuItem, closeAllFramesMenuItem));

        userInterface.registerContextMenuItemsProvider(new WebSocketContextMenuItemsProvider(logging, userInterface, persistence, websockets, frameList));

        extension.registerUnloadingHandler(new WebSocketExtensionUnloadingHandler(frameList));

        logging.logToOutput(EXTENSION_NAME + " - Loaded");
    }
}