package attack;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.Direction;
import connection.Connection;
import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.WebSocketConnectionMessage;
import interpreter.Interpreter;
import logger.Logger;
import python.ConnectionFactoryFactory;
import python.ResultsTable;

import java.time.LocalDateTime;

import static burp.api.montoya.websocket.Direction.CLIENT_TO_SERVER;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class AttackScriptExecutor
{
    private final Logger logger;
    private final MessagesToDisplay messagesToDisplay;
    private final ConnectionFactoryFactory connectionFactoryFactory;

    private Interpreter interpreter;

    public AttackScriptExecutor(
            Logger logger,
            MessagesToDisplay messagesToDisplay,
            ConnectionFactoryFactory connectionFactoryFactory
    )
    {
        this.logger = logger;
        this.messagesToDisplay = messagesToDisplay;
        this.connectionFactoryFactory = connectionFactoryFactory;
    }

    public void startAttack(String message, HttpRequest upgradeRequest, String editorCodeString)
    {
        interpreter = new Interpreter(logger);
        interpreter.setVariable("websocket_connection", connectionFactoryFactory.create());
        interpreter.setVariable("results_table", new ResultsTable(messagesToDisplay));

        interpreter.setVariable("message", message);
        interpreter.setVariable("upgrade_request", upgradeRequest);

        try
        {
            interpreter.execute(editorCodeString);
        }
        catch (Exception e)
        {
            logger.logError("Jython code error. Please review.\r\n" + e);
            throw new IllegalArgumentException(e);
        }

        newSingleThreadExecutor().submit(() -> interpreter.execute("queue_websockets(upgrade_request, message)"));
    }

    public void processMessage(WebSocketConnectionMessage webSocketConnectionMessage)
    {
        String messageParameterName = "websocket_message";
        interpreter.setVariable(messageParameterName, new DecoratedConnectionMessage(webSocketConnectionMessage));

        String callbackMethod = webSocketConnectionMessage.getDirection() == CLIENT_TO_SERVER
                ? "handle_outgoing_message"
                : "handle_incoming_message";

        interpreter.execute(String.format("%s(%s)", callbackMethod, messageParameterName));
    }

    public void stopAttack()
    {
        interpreter.close();
    }

    private static class DecoratedConnectionMessage implements ConnectionMessage
    {
        private final WebSocketConnectionMessage webSocketConnectionMessage;

        DecoratedConnectionMessage(WebSocketConnectionMessage webSocketConnectionMessage)
        {
            this.webSocketConnectionMessage = webSocketConnectionMessage;
        }

        @Override
        public String getMessage()
        {
            return webSocketConnectionMessage.getMessage();
        }

        @Override
        public Direction getDirection()
        {
            return webSocketConnectionMessage.getDirection();
        }

        @Override
        public int getLength()
        {
            return webSocketConnectionMessage.getLength();
        }

        @Override
        public LocalDateTime getDateTime()
        {
            return webSocketConnectionMessage.getDateTime();
        }

        @Override
        public String getComment()
        {
            return webSocketConnectionMessage.getComment();
        }

        @Override
        public Connection getConnection()
        {
            return webSocketConnectionMessage.getConnection();
        }

        @Override
        public void setComment(String comment)
        {
            webSocketConnectionMessage.setComment(comment);
        }
    }
}
