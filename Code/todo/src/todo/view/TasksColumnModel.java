package todo.view;

import java.awt.*;
import javax.swing.table.*;

public class TasksColumnModel extends DefaultTableColumnModel {
    
    private TableColumn createColumn(int columnIndex, int width, FontMetrics fm, boolean resizeable, String text) {
        int textWidth = fm.stringWidth(text + "  ");
        if (width < textWidth)
            width = textWidth;
        TableColumn col = new TableColumn(columnIndex);
        col.setCellRenderer(new TaskCellRenderer());
        col.setHeaderRenderer(null);
        col.setHeaderValue(text);
        col.setPreferredWidth(width);
        if (!resizeable) {
            col.setMaxWidth(width);
            col.setMinWidth(width);
        }
        col.setResizable(resizeable);
        return col;
    }
    
    public TasksColumnModel(FontMetrics fm) {
        int digit = fm.stringWidth("0");
        int alpha = fm.stringWidth("M");
        addColumn(createColumn(0, 3 * digit, fm, false, "Priority"));
        addColumn(createColumn(1, 20 * alpha, fm, true, "Description"));
        addColumn(createColumn(2, 3 * alpha, fm, false, "Alert?"));
        addColumn(createColumn(3, 10 * digit, fm, false, "Due Date"));
    }
}
