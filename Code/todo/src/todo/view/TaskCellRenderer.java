package todo.view;

import java.util.*;
import java.text.DateFormat;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.table.*;
import todo.model.Task;

public class TaskCellRenderer extends DefaultTableCellRenderer {
    
    public TaskCellRenderer() {
        super();
    }

    private Color corTarefa(Task task) {
        if (task.isCompleted())
            return Color.CYAN;
        else {
            Date dueDate = task.getDueDate();
            if (dueDate == null)
                return Color.WHITE;
            else if (task.isLate())
                return Color.PINK;
            else if (task.hasAlert())
                return Color.YELLOW;
            else
                return Color.WHITE;
        }        
    }
    
    private Object format(Object value) {
        if (value instanceof Date) {
            Date d = (Date)value;
            DateFormat df = DateFormat.getDateInstance();
            return df.format(d);
        }
        else if (value instanceof Boolean)
            return (Boolean)value ? "Y" : "N";
        else
            return value;
    }
    
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        value = format(value);
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column != 1)
            label.setHorizontalAlignment(JLabel.CENTER);
        TableModel tm = table.getModel();
        Task tarefa = ((TasksTableModel)tm).getValoresTarefa(row);
        if (isSelected) {
            label.setForeground(corTarefa(tarefa));
            label.setBackground(Color.GRAY);
        }
        else {
            label.setForeground(Color.BLACK);
            label.setBackground(corTarefa(tarefa));
        }
        return label;
    }
    
}
