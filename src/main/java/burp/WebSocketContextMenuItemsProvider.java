package burp;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import ui.WebSocketFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WebSocketContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final List<JFrame> frameList;
    private final Logging logging;
    private final UserInterface userInterface;
    private final Persistence persistence;
    private final WebSockets webSockets;

    public WebSocketContextMenuItemsProvider(
            Logging logging,
            UserInterface userInterface,
            Persistence persistence,
            WebSockets webSockets,
            List<JFrame> frameList
    )
    {
        this.logging = logging;
        this.userInterface = userInterface;
        this.persistence = persistence;
        this.webSockets = webSockets;
        this.frameList = frameList;
    }

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
            frameList.add(new WebSocketFrame(logging, userInterface, persistence, webSockets, webSocketMessage));
        }
    }
}
