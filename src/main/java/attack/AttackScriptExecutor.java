package attack;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.websocket.Direction;
import connection.Connection;
import data.ConnectionMessage;
import data.MessagesToDisplay;
import data.WebSocketConnectionMessage;
import interpreter.Interpreter;
import logger.Logger;
import python.ConnectionFactory;
import python.ResultsTable;

import java.time.LocalDateTime;

import static burp.api.montoya.websocket.Direction.CLIENT_TO_SERVER;

public class AttackScriptExecutor
{
    private final Logger logger;
    private final MessagesToDisplay messagesToDisplay;
    private final ConnectionFactory connectionFactory;

    private Interpreter interpreter;

    public AttackScriptExecutor(Logger logger, MessagesToDisplay messagesToDisplay, ConnectionFactory connectionFactory)
    {
        this.logger = logger;
        this.messagesToDisplay = messagesToDisplay;
        this.connectionFactory = connectionFactory;
    }

    public void startAttack(String message, HttpRequest upgradeRequest, String editorCodeString)
    {
        interpreter = new Interpreter(logger);
        interpreter.setVariable("websocket_connection", connectionFactory);
        interpreter.setVariable("results_table", new ResultsTable(messagesToDisplay));

        interpreter.setVariable("message", message);
        interpreter.setVariable("upgrade_request", upgradeRequest);

        interpreter.execute(editorCodeString);
        interpreter.execute("queue_websockets(upgrade_request, message)");
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
