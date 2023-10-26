package ui.attack.table;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.ui.editor.WebSocketMessageEditor;
import data.ConnectionMessage;

import javax.swing.*;
import java.awt.*;

public class WebSocketMessageTable extends JPanel
{
    public WebSocketMessageTable(
            WebSocketMessageTableModel webSocketMessageTableModel,
            WebSocketMessageEditor webSocketMessageEditor
    )
    {
        super(new BorderLayout());

        JTable webSocketMessageTable = new JTable(webSocketMessageTableModel)
        {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
            {
                ConnectionMessage webSocketConnectionMessage = webSocketMessageTableModel.get(rowIndex);

                webSocketMessageEditor.setContents(ByteArray.byteArray(webSocketConnectionMessage.getPayload()));

                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };


        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem clearTableMenuItem = new JMenuItem("Clear table");
        clearTableMenuItem.addActionListener(l -> EventQueue.invokeLater(webSocketMessageTableModel::clear));

        popupMenu.add(clearTableMenuItem);
        webSocketMessageTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(webSocketMessageTable);

        this.add(scrollPane, BorderLayout.CENTER);
    }
}
