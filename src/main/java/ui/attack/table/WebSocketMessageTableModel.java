package ui.attack.table;

import data.ConnectionMessage;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WebSocketMessageTableModel extends AbstractTableModel
{
    private final List<ConnectionMessage> connectionMessageList;

    public WebSocketMessageTableModel()
    {
        this.connectionMessageList = new ArrayList<>();
    }

    @Override
    public int getRowCount()
    {
        return connectionMessageList.size();
    }

    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
        {
            case 0 -> "Message ID";
            case 1 -> "Direction";
            case 2 -> "Length";
            case 3 -> "Time";
            case 4 -> "Comment";
            default -> "";
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        ConnectionMessage webSocketConnectionMessage = connectionMessageList.get(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> rowIndex;
            case 1 -> webSocketConnectionMessage.getDirection().name();
            case 2 -> webSocketConnectionMessage.getLength();
            case 3 -> webSocketConnectionMessage.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
            case 4 -> webSocketConnectionMessage.getComment();
            default -> "";
        };
    }

    public void add(ConnectionMessage connectionMessage)
    {
        int index = connectionMessageList.size();
        connectionMessageList.add(connectionMessage);
        fireTableRowsInserted(index, index);
    }

    public ConnectionMessage get(int rowIndex)
    {
        return connectionMessageList.get(rowIndex);
    }

    public void clear()
    {
        int rows = getRowCount();

        if (rows == 0)
        {
            return;
        }

        connectionMessageList.clear();
        fireTableRowsDeleted(0, rows-1);
    }
}
