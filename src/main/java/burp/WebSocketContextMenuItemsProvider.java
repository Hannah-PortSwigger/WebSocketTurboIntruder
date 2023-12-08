package burp;

import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import logger.Logger;
import ui.WebSocketFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static burp.WebSocketFuzzer.EXTENSION_NAME;

public class WebSocketContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final WebSockets webSockets;
    private final Consumer<JFrame> newFrameConsumer;

    public WebSocketContextMenuItemsProvider(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            WebSockets webSockets,
            Consumer<JFrame> newFrameConsumer
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.webSockets = webSockets;
        this.newFrameConsumer = newFrameConsumer;
    }

    @Override
    public List<Component> provideMenuItems(WebSocketContextMenuEvent event)
    {
        JMenuItem sendToContextMenuItem = new JMenuItem("Send to " + EXTENSION_NAME);
        sendToContextMenuItem.addActionListener(l -> performAction(event));

        return List.of(sendToContextMenuItem);
    }

    private void performAction(WebSocketContextMenuEvent event)
    {
        List<WebSocketMessage> webSocketMessageList = event.messageEditorWebSocket().isPresent()
                ? List.of(event.messageEditorWebSocket().get().webSocketMessage())
                : event.selectedWebSocketMessages();

        for(WebSocketMessage webSocketMessage : webSocketMessageList)
        {
            newFrameConsumer.accept(new WebSocketFrame(logger, userInterface, fileLocationConfiguration, webSockets, webSocketMessage));
        }
    }
}
