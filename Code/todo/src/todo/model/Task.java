package todo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;

public class Task implements Serializable {

    private int id;
    private String description;
    private int priority;
    private Date dueDate;
    private boolean alert;
    private int daysBefore;
    private String obs;
    private boolean completed;

    private Calendar getTodayDate() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now;
    }
    
    public boolean isLate() {
        Date dueDate = getDueDate();
        if (dueDate == null)
            return false;
        else {
            return dueDate.compareTo(getTodayDate().getTime()) < 0;
        }        
    }
    
    public boolean hasAlert() {
        Date dueDate = getDueDate();
        if (!getAlert() || dueDate == null)
            return false;
        else {
            DateFormat df = DateFormat.getDateInstance();
            
            Calendar dueDay = Calendar.getInstance();
            dueDay.setTime(dueDate);
            int dias = dueDay.get(Calendar.DAY_OF_YEAR) - getTodayDate().get(Calendar.DAY_OF_YEAR);
            return dias <= getDaysBefore();
        }        
    }
    
    public Task() {
        setCompleted(false);
        setAlert(false);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean getAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
