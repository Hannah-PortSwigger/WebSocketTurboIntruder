package queue;

import burp.api.montoya.MontoyaApi;
import data.ConnectionMessage;
import data.WebSocketConnectionMessage;
import ui.attack.table.WebSocketMessageTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableBlockingQueueConsumer implements Runnable
{
    private final MontoyaApi api;
    private final BlockingQueue<ConnectionMessage> queue;
    private final WebSocketMessageTableModel tableModel;
    private final AtomicBoolean isRunning;

    public TableBlockingQueueConsumer(MontoyaApi api, BlockingQueue<ConnectionMessage> queue, WebSocketMessageTableModel tableModel, AtomicBoolean isRunning)
    {
        this.api = api;
        this.queue = queue;
        this.tableModel = tableModel;
        this.isRunning = isRunning;
    }

    @Override
    public void run()
    {
        while (isRunning.get())
        {
            try
            {
                ConnectionMessage connectionMessage = queue.take();
                EventQueue.invokeLater(() -> tableModel.add(connectionMessage));
            } catch (InterruptedException e)
            {
                api.logging().logToError("Error taking from tableBlockingQueue.");
            }
        }
    }
}
