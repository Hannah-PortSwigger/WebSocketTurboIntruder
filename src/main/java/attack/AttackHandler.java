package attack;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.Direction;
import connection.Connection;
import connection.ConnectionFactory;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import org.python.util.PythonInterpreter;
import queue.TableBlockingQueueProducer;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AttackHandler
{
    private final MontoyaApi api;
    private final AtomicBoolean isProcessing;
    private final BlockingQueue<ConnectionMessage> tableBlockingQueue;
    private final WebSocketMessage baseWebSocketMessage;
    private final PythonInterpreter interpreter;
//    private final String scriptEnvironment;

    public AttackHandler(MontoyaApi api, AtomicBoolean isProcessing, BlockingQueue<WebSocketConnectionMessage> sendMessageQueue, BlockingQueue<ConnectionMessage> tableBlockingQueue, WebSocketMessage baseWebSocketMessage)
    {
        this.api = api;
        this.isProcessing = isProcessing;
        this.tableBlockingQueue = tableBlockingQueue;
        this.baseWebSocketMessage = baseWebSocketMessage;

        interpreter = new PythonInterpreter();
        interpreter.setOut(api.logging().output());
        interpreter.setErr(api.logging().error());

//        String data = null;
//        try (InputStream stream = WebSocketEditorPanel.class.getResourceAsStream("/ScriptEnvironment.py"))
//        {
//            if (stream != null)
//            {
//                data = IOUtils.toString(stream);
//            }
//        } catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }
//        scriptEnvironment = data;

        interpreter.set("base_websocket", baseWebSocketMessage);

        interpreter.set("websocket_connection", new ConnectionFactory(api, isProcessing, this, sendMessageQueue));

        interpreter.set("results_table", new TableBlockingQueueProducer(api, tableBlockingQueue));

//        interpreter.exec(scriptEnvironment);
    }

    public void executeJython(String payload, String editorCodeString)
    {
        interpreter.set("payload", payload);
        interpreter.exec(editorCodeString);
        interpreter.exec("queue_websockets(base_websocket, payload)");
    }

    public void executeCallback(WebSocketConnectionMessage webSocketConnectionMessage)
    {
        String messageParameterName = "websocket_message";
        interpreter.set(messageParameterName, new DecoratedConnectionMessage(webSocketConnectionMessage));

        String callbackMethod = webSocketConnectionMessage.getDirection() == Direction.CLIENT_TO_SERVER
                ? "handle_outgoing_message"
                : "handle_incoming_message";

        interpreter.exec(String.format("%s(%s", callbackMethod, messageParameterName));
    }

    private static class DecoratedConnectionMessage implements ConnectionMessage
    {
        private final WebSocketConnectionMessage webSocketConnectionMessage;

        DecoratedConnectionMessage(WebSocketConnectionMessage webSocketConnectionMessage)
        {
            this.webSocketConnectionMessage = webSocketConnectionMessage;
        }

        @Override
        public String getPayload()
        {
            return webSocketConnectionMessage.getPayload();
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
