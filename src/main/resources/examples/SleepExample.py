import time

def queue_websockets(upgrade_request, message):
    connection1 = websocket_connection.create(upgrade_request)

    for i in range(10):
        connection1.queue(message)
        time.sleep(2)

def handle_outgoing_message(websocket_message):
    results_table.add(websocket_message)

def handle_incoming_message(websocket_message):
    results_table.add(websocket_message)