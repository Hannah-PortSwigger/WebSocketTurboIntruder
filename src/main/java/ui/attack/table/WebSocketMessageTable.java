package ui.attack.table;

import data.ConnectionMessage;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class WebSocketMessageTable extends JPanel
{
    public WebSocketMessageTable(
            WebSocketMessageTableModel webSocketMessageTableModel,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        super(new BorderLayout());

        JTable webSocketMessageTable = new JTable(webSocketMessageTableModel);
        webSocketMessageTable.setSelectionMode(SINGLE_SELECTION);

        ListSelectionModel selectionModel = webSocketMessageTable.getSelectionModel();
        selectionModel.addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting())
            {
                return;
            }

            ConnectionMessage webSocketConnectionMessage = webSocketMessageTableModel.get(selectionModel.getMaxSelectionIndex());

            if (webSocketConnectionMessage != null)
            {
                messageConsumer.accept(webSocketConnectionMessage);
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem clearTableMenuItem = new JMenuItem("Clear table");
        clearTableMenuItem.addActionListener(l -> invokeLater(webSocketMessageTableModel::clear));

        popupMenu.add(clearTableMenuItem);
        webSocketMessageTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(webSocketMessageTable);

        this.add(scrollPane, BorderLayout.CENTER);
    }
}
