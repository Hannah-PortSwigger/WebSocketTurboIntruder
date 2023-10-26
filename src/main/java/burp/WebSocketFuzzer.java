package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;
import burp.api.montoya.websocket.WebSocket;

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
        api.extension().setName(EXTENSION_NAME);

        List<JFrame> frameList = new ArrayList<>();

        if (api.persistence().preferences().getString("websocketsScriptsPath") == null)
        {
            api.persistence().preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY);
        }

        MenuItem resetDefaultScriptsMenuItem = BasicMenuItem
                .basicMenuItem("Reset scripts directory to default.")
                .withAction(() ->
                        {
                            api.persistence().preferences().setString("websocketsScriptsPath", DEFAULT_SCRIPT_DIRECTORY);
                        }
                );

        MenuItem closeAllFramesMenuItem = BasicMenuItem
                .basicMenuItem("Close all " + EXTENSION_NAME + " windows.")
                .withAction(() ->
                        {
                            closeAllFrames(frameList);
                        }
                );

        api.userInterface().menuBar().registerMenu(Menu.menu(EXTENSION_NAME).withMenuItems(resetDefaultScriptsMenuItem, closeAllFramesMenuItem));

        api.userInterface().registerContextMenuItemsProvider(new WebSocketContextMenuItemsProvider(api, frameList));

        api.extension().registerUnloadingHandler(new WebSocketExtensionUnloadingHandler(frameList));

        api.logging().logToOutput(EXTENSION_NAME + " - Loaded");
    }
}
