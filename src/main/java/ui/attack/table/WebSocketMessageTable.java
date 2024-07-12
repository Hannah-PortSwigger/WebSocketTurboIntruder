package ui.attack.table;

import burp.api.montoya.websocket.Direction;
import data.ConnectionMessage;
import utils.IconFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.function.Consumer;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static utils.IconType.LEFT_ARROW;
import static utils.IconType.RIGHT_ARROW;

public class WebSocketMessageTable extends JPanel
{
    public WebSocketMessageTable(
            WebSocketMessageTableModel webSocketMessageTableModel,
            IconFactory iconFactory,
            Consumer<ConnectionMessage> messageConsumer
    )
    {
        super(new BorderLayout());

        JTable webSocketMessageTable = new JTable(webSocketMessageTableModel);
        webSocketMessageTable.setSelectionMode(SINGLE_SELECTION);
        webSocketMessageTable.setDefaultRenderer(Direction.class, new DirectionCellRenderer(iconFactory));

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

    private static class DirectionCellRenderer extends DefaultTableCellRenderer
    {
        private final IconFactory iconFactory;

        public DirectionCellRenderer(IconFactory iconFactory)
        {
            this.iconFactory = iconFactory;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (component instanceof JLabel label && value instanceof Direction direction)
            {
                String text = switch (direction)
                {
                    case SERVER_TO_CLIENT -> "To client";
                    case CLIENT_TO_SERVER -> "To server";
                };

                Icon icon = switch (direction)
                {
                    case SERVER_TO_CLIENT -> iconFactory.scaledIconFor(LEFT_ARROW, label.getFont().getSize());
                    case CLIENT_TO_SERVER -> iconFactory.scaledIconFor(RIGHT_ARROW, label.getFont().getSize());
                };

                label.setIcon(icon);
                label.setText(text);
            }

            return component;
        }
    }
}
