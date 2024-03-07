def queue_websockets(upgrade_request, message):
    connection1 = websocket_connection.create(upgrade_request)
    connection2 = websocket_connection.create(upgrade_request.withHeader("Cookie", "session=foo"))

    connection1.queue("READY")
    connection2.queue("foo")

def handle_outgoing_message(websocket_message):
    results_table.add(websocket_message)

def handle_incoming_message(websocket_message):
    results_table.add(websocket_message)