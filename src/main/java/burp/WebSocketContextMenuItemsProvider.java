package burp;

import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import data.InitialWebSocketMessage;
import ui.WebSocketFrameFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static burp.WebSocketFuzzer.EXTENSION_NAME;

public class WebSocketContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final Consumer<JFrame> newFrameConsumer;
    private final WebSocketFrameFactory webSocketFrameFactory;

    public WebSocketContextMenuItemsProvider(
            Consumer<JFrame> newFrameConsumer,
            WebSocketFrameFactory webSocketFrameFactory
    )
    {
        this.newFrameConsumer = newFrameConsumer;
        this.webSocketFrameFactory = webSocketFrameFactory;
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

        webSocketMessageList.stream()
                .map(webSocketMessage -> new InitialWebSocketMessage(webSocketMessage.upgradeRequest(), webSocketMessage.payload()))
                .map(webSocketFrameFactory::from)
                .forEach(newFrameConsumer);
    }
}
