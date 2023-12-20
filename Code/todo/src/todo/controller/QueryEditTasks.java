package todo.controller;

import java.util.*;
import java.text.DateFormat;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;
import todo.view.TaskDetailsDialog;
import todo.view.TasksWindow;
import todo.model.DatabaseException;
import todo.model.TaskManager;
import todo.model.ModelException;
import todo.model.Task;

public class QueryEditTasks implements ActionListener {
    
    private TasksWindow view;
    private QueryEditTasks controller;
    private TaskManager model;
    
    public QueryEditTasks(TasksWindow view, TaskManager model) {
        this.view = view;
        this.model = model;
        view.addActionListener(this);
        try {
            listTasks();
        }
        catch (DatabaseException e) {
            view.setStatus(e.getMessage(), true);
        }
    }

    public void listTasks() throws DatabaseException {
            view.setTaskList(model.listAllTasks(true));
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            if (false)
                ; // don't do anything
            else if (e.getActionCommand().equals("newTask")) {
                editTask(true);
            }
            else if (e.getActionCommand().equals("editTask")) {
                editTask(false);
            }
            else if (e.getActionCommand().equals("saveTask")) {
                saveTask();
            }
            else if (e.getActionCommand().equals("markTask")) {
                markTasks();
            }
            else if (e.getActionCommand().equals("removeTask")) {
                removeTasks();
            }
            else if (e.getActionCommand().equals("showAlerts")) {
                showAlerts();
            }
        }
        catch (Exception ex) {
            view.setStatus(ex.getMessage(), true);
        }
        view.setCursor(null);
    }
    
    private TaskDetailsDialog taskDetailsDialog;
    
    private void editTask(boolean newTask) {
        taskDetailsDialog = new TaskDetailsDialog(view, true);
        taskDetailsDialog.setNewTask(newTask);
        if (newTask)
            taskDetailsDialog.setTask(new Task());
        else
            taskDetailsDialog.setTask(view.getSelectedTask());
        taskDetailsDialog.addActionListener(this);
        taskDetailsDialog.setVisible(true);        
    }
    
    private void saveTask() {
        Task task = taskDetailsDialog.getTask();
        try {
            if (taskDetailsDialog.isNewTask())
                model.addTask(task);
            else
                model.updateTask(task);
            taskDetailsDialog.dispose();
            taskDetailsDialog = null;
            listTasks();
        }
        catch (ModelException e) {
            taskDetailsDialog.setMessage(e.getMessage(), true);
        }
    }
    
    private void markTasks() {
        Task[] tasks = view.getSelectedTasks();
        try {
            for (Task aTask : tasks)
                model.markAsCompleted(aTask.getId(), !aTask.isCompleted());
            listTasks();
        }
        catch (ModelException e) {
            taskDetailsDialog.setMessage(e.getMessage(), true);
        }
    }
    
    private void removeTasks() {
        Task[] tasks = view.getSelectedTasks();
        int removedCount = 0;
        try {
            for (Task aTask : tasks) {
                int response = JOptionPane.showConfirmDialog(view,
                        "Are you sure you want to remove task\n["
                        + aTask.getDescription() + "] ?",
                        "Remove Task",
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    model.removeTask(aTask.getId());
                    removedCount++;
                }
            }
            if (removedCount > 0) {
                listTasks();
                if (taskDetailsDialog.isDisplayable())
                    taskDetailsDialog.dispose();
            }
        }
        catch (ModelException e) {
            taskDetailsDialog.setMessage(e.getMessage(), true);
        }
    }
    
    public void showAlerts() {
        try {
            List<Task> tasks = model.listTasksWithAlert();
            for(Task aTask : tasks) {
                DateFormat df = DateFormat.getDateInstance();
                JOptionPane.showMessageDialog(view,
                        "This task has less than " + aTask.getDaysBefore()
                        + " days left:\n"
                        + "[" + aTask.getDescription() + "]\n"
                        + "the due date is " + df.format(aTask.getDueDate()) + "\n",
                        "Alert", JOptionPane.INFORMATION_MESSAGE);                
            }
            if (tasks.size() == 0)
                view.setStatus("There are no task alerts for today.",
                        false);
        }
        catch (ModelException e) {
            view.setStatus(e.getMessage(), true);
        }
    }
    
}
