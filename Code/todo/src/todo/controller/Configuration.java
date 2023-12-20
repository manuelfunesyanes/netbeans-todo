package todo.controller;

import java.io.File;
import java.awt.Cursor;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.*;
import todo.view.TasksWindow;
import todo.model.DatabaseException;
import todo.model.TaskManager;
import todo.model.Parameters;

public class Configuration implements ActionListener {

    private TasksWindow view;
    private TaskManager model;
    private Parameters params;
    
    public Configuration(TasksWindow view, TaskManager model, Parameters params) {
        this.view = view;
        this.model = model;
        this.params = params;
        view.addActionListener(this);
        view.addWindowListener(closeDatabase);
    }
    
    private WindowListener closeDatabase = new WindowAdapter() {
        public void windowClosed(WindowEvent e) {
            model.disconnect();
        }
        public void windowClosing(WindowEvent e) {
            model.disconnect();
        }
    };
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            if (false)
                ; // don't do nothing
            else if (e.getActionCommand().equals("createTaskList")) {
                newTaskList();
            }
            else if (e.getActionCommand().equals("openTaskList")) {
                openTaskList();
            }
            else if (e.getActionCommand().equals("about")) {
                sobre();
            }
        }
        catch (Exception ex) {
            view.setStatus(ex.getMessage(), true);
        }
        view.setCursor(null);
    }
    
    private FileFilter hsqlDatabases = new FileFilter() {
        public String getDescription() {
            return "Task Lists - HSQLDB Databases (*.script)";
        }
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            else if (f.getName().endsWith(".script"))
                return true;
            else
                return false;
        }
    };
    
    private String createOpenDatabase(File databaseFile) throws DatabaseException {
        String fileName = databaseFile.getAbsolutePath();
        if (fileName.startsWith("file:"))
            fileName = fileName.substring(5);
        if (fileName.endsWith(".script"))
            fileName = fileName.substring(0, fileName.length() - 7);
        model.reconnect(fileName);
        view.setTaskList(model.listAllTasks(true));
        return fileName;
    }
    
    public void openTaskList(String bd) throws DatabaseException {
        String fileName = "";
        try {
            fileName = createOpenDatabase(new File(bd));
            view.setStatus("Opened task list: " + fileName, false);
        }
        catch (DatabaseException e) {
            view.setStatus("Cannot open task list: " + fileName, true);
        }
    }
    
    private void newTaskList() {
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("New Task List");
        dlg.setFileFilter(hsqlDatabases);
        File dir = new File(params.getDatabase()).getParentFile();
        dlg.setCurrentDirectory(dir);
        if (dlg.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                String arq = createOpenDatabase(dlg.getSelectedFile());
                view.setStatus("Task list created: " + arq, false);
            }
            catch (DatabaseException e) {
                view.setStatus("Cannot create task list", true);
            }
        }
    }
    
    private void openTaskList() {
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("Open Task List");
        dlg.setFileFilter(hsqlDatabases);
        File dir = new File(params.getDatabase()).getParentFile();
        dlg.setCurrentDirectory(dir);
        if (dlg.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                String arq = createOpenDatabase(dlg.getSelectedFile());
                view.setStatus("Task List opened: " + arq, false);
            }
            catch (DatabaseException e) {
                view.setStatus("Cannot open task list", true);
            }
        }
    }
    
    private void sobre() {
        JOptionPane.showMessageDialog(view,
                "Todo - Task List\nRelease 1.0\n\n"
                + "NetBeans Magazine - JavaOne 2006",
                "About Todo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
