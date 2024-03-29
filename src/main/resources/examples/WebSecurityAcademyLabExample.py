def queue_websockets(upgrade_request, message):
    connection1 = websocket_connection.create(upgrade_request)

    connection1.queue("READY")

def handle_outgoing_message(websocket_message):
    results_table.add(websocket_message)

def handle_incoming_message(websocket_message):
    # Warning: will continue sending messages until attack paused.
    if "Hal Pline" in websocket_message.getMessage():
        websocket_message.getConnection().queue("{\"message\":\"foo\"}")
    results_table.add(websocket_message)