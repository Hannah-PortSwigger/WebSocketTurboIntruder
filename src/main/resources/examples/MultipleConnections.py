def queue_websockets(base_websocket, payload):
    connection1 = websocket_connection.create(base_websocket)
    connection2 = websocket_connection.create(base_websocket)

    for i in range(10):
        connection1.queue(payload)
        connection2.queue("foo")

def handle_outgoing_message(websocket_message):
    results_table.add(websocket_message)

def handle_incoming_message(websocket_message):
    results_table.add(websocket_message)