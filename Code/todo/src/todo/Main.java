package todo;

/*
 * Main.java
 *
 * Created on 20 de Dezembro de 2005, 19:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import todo.view.TasksWindow;
import todo.controller.QueryEditTasks;
import todo.controller.Configuration;
import todo.model.Parameters;
import todo.model.TaskManager;

/**
 *
 * @author lozano
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            System.err.println("Usage: java todo.Main <*.script file>");
            System.exit(1);
        }
        
        //UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
        
        TasksWindow view = new TasksWindow();
        Parameters params = new Parameters();
        TaskManager model = new TaskManager(params);
        QueryEditTasks tasksController = new QueryEditTasks(view, model);
        Configuration configurationController = new Configuration(view, model, params);
        if (args.length == 1)
            configurationController.openTaskList(args[0]);
        view.setVisible(true);
        tasksController.showAlerts();
    }
    
}
