# WebSocket Turbo Intruder
Extension to fuzz WebSocket messages using custom Python code

## Usage
1. Right-click on a WebSockets message and go to `Extensions > WebSocket Turbo Intruder > Send to WebSocket Turbo Intruder`
2. Select a template from the drop-down list
3. Adjust Python code to suit your use case
4. Start attack

Note: This will use a new WebSocket connection to send messages down.

## Documentation

### `queue_websockets(base_websocket, payload)`
`websocket_connection`: This object has one available method - `create()`. Use this to create a WebSocket connection.
The `create` method takes `base_websocket` as an argument.

Once you've created your WebSocket connection, you can queue messages to send down this connection.

Use the `queue()` method on this object.
- `queue(String payload)`: Send payload with no comment set
- `queue(String payload, String comment)`: Send payload with custom comment

`payload` is the contents of the WebSocket message editor in the top half of your screen. You can manually change this, or you can manipulate the String contents in your Python code.

### `handle_outgoing_message(websocket_message)`
Use this method to conditionally add outgoing messages to the results table.

### `handle_incoming_message(websocket_message)`
Use this method to conditionally add incoming messages to the results table.


### `websocket_message`
Methods:
- `getPayload()`: Retrieve the String payload that was sent/received
- `getDirection()`: Retrieves a `burp.api.montoya.websocket.Direction`
- `getLength()`: Retrieves the length of the message
- `getDateTime()`: Retrieves the `java.time.LocalDateTime` that was set on the object
- `getComment()`: Retrieves the comment that was set on the message
- `setComment(String comment)`: Allows you to set a comment on the object
- `getConnection()`: Retrieves the Connection so that you can `queue()` additional messages