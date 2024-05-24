package python;

import burp.api.montoya.websocket.WebSockets;
import data.PendingMessages;
import logger.Logger;

import java.util.function.Supplier;

public class ConnectionFactoryFactory
{
    private final Logger logger;
    private final WebSockets webSockets;
    private final PendingMessages pendingMessages;
    private final Supplier<Integer> attackIdSupplier;

    public ConnectionFactoryFactory(
            Logger logger,
            WebSockets webSockets,
            PendingMessages pendingMessages,
            Supplier<Integer> attackIdSupplier
    )
    {
        this.logger = logger;
        this.webSockets = webSockets;
        this.pendingMessages = pendingMessages;
        this.attackIdSupplier = attackIdSupplier;
    }

    public ConnectionFactory create()
    {
        return new ConnectionFactory(logger, webSockets, pendingMessages, attackIdSupplier.get());
    }
}
