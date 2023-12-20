package todo.view;

import java.util.*;
import java.text.*;
import javax.swing.table.*;
import todo.model.Task;

public class TasksTableModel extends AbstractTableModel {
    
    private List<Task> tasks;
    private List<Task> filteredTasks;
    private boolean showCompleted = true;
    private boolean sortByPriority = true;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    public TasksTableModel(List<Task> tasks) {
        this.tasks = tasks;
        filterTasks();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Task umaTarefa = filteredTasks.get(rowIndex);
        switch(columnIndex) {
            case 0: return umaTarefa.getPriority();
            case 1: return umaTarefa.getDescription();
            case 2: return umaTarefa.getAlert();
            case 3: return umaTarefa.getDueDate();
        }
        return null;
    }

    public int getRowCount() {
        return filteredTasks.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public Task getValoresTarefa(int rowIndex) {
        if (rowIndex > filteredTasks.size() )
            return null;
        else
            return filteredTasks.get(rowIndex);
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
        filterTasks();
    }

    public boolean isSortByPriority() {
        return sortByPriority;
    }

    public void setSortByPriority(boolean sortByPriority) {
        this.sortByPriority = sortByPriority;
        filterTasks();
    }
    
    private void filterTasks() {
        filteredTasks = new ArrayList<Task>();
        for (Task tarefa : tasks) {
            if (!isShowCompleted() && tarefa.isCompleted())
                continue;
            filteredTasks.add(tarefa);
        }
        if (!isSortByPriority())
            Collections.sort(filteredTasks, new Comparator<Task>() {
                public int compare(Task t1, Task t2) {
                    if (t1.getDueDate() == null)
                        return 1;
                    else if (t2.getDueDate() == null)
                        return -1;
                    else if (t1.getDueDate().equals(t2.getDueDate())) {
                        if (t1.getPriority() == t2.getPriority())
                            return t1.getDescription().compareTo(t2.getDescription());
                        else
                            return t1.getPriority() > t2.getPriority() ? 1 : -1;
                    }
                    else
                        return t1.getDueDate().compareTo(t2.getDueDate());
                }
            });
        fireTableDataChanged();
    }
}
