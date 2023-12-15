package ui;

import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.WebSockets;
import config.FileLocationConfiguration;
import logger.Logger;

public class WebSocketFrameFactory
{
    private final Logger logger;
    private final UserInterface userInterface;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final WebSockets webSockets;

    public WebSocketFrameFactory(
            Logger logger,
            UserInterface userInterface,
            FileLocationConfiguration fileLocationConfiguration,
            WebSockets webSockets
    )
    {
        this.logger = logger;
        this.userInterface = userInterface;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.webSockets = webSockets;
    }

    public WebSocketFrame from(WebSocketMessage webSocketMessage)
    {
        return new WebSocketFrame(
                logger,
                userInterface,
                fileLocationConfiguration,
                webSockets,
                webSocketMessage
        );
    }
}
