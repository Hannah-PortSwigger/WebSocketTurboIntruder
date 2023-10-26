package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import ui.WebSocketFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WebSocketContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final MontoyaApi api;
    private final List<JFrame> frameList;

    public WebSocketContextMenuItemsProvider(MontoyaApi api, List<JFrame> frameList)
    {
        this.api = api;
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
            frameList.add(new WebSocketFrame(api, webSocketMessage));
        }
    }
}
