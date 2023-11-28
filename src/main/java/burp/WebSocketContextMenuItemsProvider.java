package burp;

import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import logger.Logger;
import ui.WebSocketFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WebSocketContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final Persistence persistence;
    private final WebSockets webSockets;
    private final List<JFrame> frameList;

    public WebSocketContextMenuItemsProvider(
            Logger logger,
            UserInterface userInterface,
            Persistence persistence,
            WebSockets webSockets,
            List<JFrame> frameList
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.persistence = persistence;
        this.webSockets = webSockets;
        this.frameList = frameList;
    }

    @Override
    public List<Component> provideMenuItems(WebSocketContextMenuEvent event)
    {
        JMenuItem sendToContextMenuItem = new JMenuItem("Send to " + WebSocketFuzzer.EXTENSION_NAME);
        sendToContextMenuItem.addActionListener(l -> performAction(event));

        return List.of(sendToContextMenuItem);
    }

    private void performAction(WebSocketContextMenuEvent event)
    {
        List<WebSocketMessage> webSocketMessageList = event.messageEditorWebSocket().isPresent() ? List.of(event.messageEditorWebSocket().get().webSocketMessage()) : event.selectedWebSocketMessages();

        for(WebSocketMessage webSocketMessage : webSocketMessageList)
        {
            frameList.add(new WebSocketFrame(logger, userInterface, persistence, webSockets, webSocketMessage));
        }
    }
}
