def queue_websockets(upgrade_request, payload):
    connection1 = websocket_connection.create(upgrade_request)

    for i in range(10):
        connection1.queue(payload)

def handle_outgoing_message(websocket_message):
    return

def handle_incoming_message(websocket_message):
    results_table.add(websocket_message)