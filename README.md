# WebSocket Turbo Intruder
Extension to fuzz WebSocket messages using custom Python code

## Usage
1. Right-click on a WebSockets message and go to `Extensions > WebSocket Turbo Intruder > Send to WebSocket Turbo Intruder`
   - Highlighting a portion of the WebSockets message and then sending to WSTI will result in the highlighted content being replaced by `%s`
3. Select a template from the drop-down list
4. Adjust Python code to suit your use case
5. Start attack

Note: This will use a new WebSocket connection to send messages down.

## Documentation

### `queue_websockets(base_websocket, message)`
`websocket_connection`: This object has one available method - `create()`. Use this to create a WebSocket connection.
The `create` method takes `base_websocket` as an argument.

Once you've created your WebSocket connection, you can queue messages to send down this connection.

Use the `queue()` method on this object.
- `queue(String message)`: Send the message
- `queue(String message, String replacement)`: Send message. `replacement` will replace all instances of `%s` in your message
- `queueWithComment(String message, String comment)`: Send message with provided comment
- `queueWithComment(String message, String replacement, String comment)`: Send message with provided comment. `replacement` will replace all instances of `%s` in your message

`message` is the contents of the WebSocket message editor in the top half of your screen. You can manually change this, or you can manipulate the String contents in your Python code.

### `handle_outgoing_message(websocket_message)`
Use this method to conditionally add outgoing messages to the results table.

### `handle_incoming_message(websocket_message)`
Use this method to conditionally add incoming messages to the results table.


### `websocket_message`
Methods:
- `getMessage()`: Retrieve the String message that was sent/received
- `getDirection()`: Retrieves a `burp.api.montoya.websocket.Direction`
- `getLength()`: Retrieves the length of the message
- `getDateTime()`: Retrieves the `java.time.LocalDateTime` that was set on the object
- `getComment()`: Retrieves the comment that was set on the message
- `setComment(String comment)`: Allows you to set a comment on the object
- `getConnection()`: Retrieves the Connection so that you can `queue()` additional messages
