package todo.view;

import java.awt.event.*;
import java.awt.Window;
import java.util.*;

public class ActionSupport implements ActionListener {
    
    private Window window;
    
    /** Creates a new instance of ActionSupport */
    public ActionSupport(Window window) {
        this.window = window;
    }

    private List<ActionListener> listeners = new ArrayList<ActionListener>();
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    public void fireActionEvent(ActionEvent e) {
        Iterator<ActionListener> it = listeners.iterator();
        while (it.hasNext()) {
            ActionListener listener = it.next();
            listener.actionPerformed(new ActionEvent(window, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireActionEvent(e);
    }
    
}
