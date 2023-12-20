package todo.model;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class TaskManager {
    
    private Parameters params;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    
    public TaskManager(Parameters params) throws DatabaseException {
        this.params = params;
        connect();
    }

    public void reconnect(String database) throws DatabaseException {
        disconnect();
        params.setDatabase(database);
        connect();
    }
    
    private void connect() throws DatabaseException {
        try {
            Class.forName(params.getJdbcDriver());
            String url = params.getJdbcUrl();
            con = DriverManager.getConnection(params.getJdbcUrl(), "sa", "");
            if (!checkTables())
                createTables();
        }
        catch (DatabaseException e) {
            throw new DatabaseException("Cannot initialize the database tables", e.getCause());
        }
        catch (ClassNotFoundException e) {
            throw new DatabaseException("Cannot load the database driver", e);
        }
        catch (SQLException e) {
            throw new DatabaseException("Cannot open the database", e);
        }
    }
    
    private boolean checkTables() {
        try {
            String sql = "SELECT COUNT(*) FROM todo";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
        finally {
            cleanUp();
        }
    }
            
    private void createTables() throws DatabaseException {
        update ("CREATE TABLE todo (" +
            "id IDENTITY, " +
            "description VARCHAR(100), " +
            "priority INTEGER, " +
            "completed BOOLEAN, " +
            "dueDate DATE, " +
            "alert BOOLEAN, " +
            "daysBefore INTEGER, " +
            "obs VARCHAR(250) " +
            ")");
    }

    public void disconnect() {
        try {
            if (con != null)
                con.close();
            con = null;
        }
        catch (SQLException e) {
            // ignores the exception
        }
    }
    
    private void cleanUp() {
        try {
            if (rs != null)
                rs.close();
            rs = null;
            if (stmt != null)
                stmt.close();
            stmt = null;    
        }
        catch (SQLException e) {
            // ignores the exception
        }
    }    
    
    private void update(String sql) throws DatabaseException {
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            throw new DatabaseException("Cannot modify the database", e);
        }
        finally {
            cleanUp();
        }
    }

    private PreparedStatement prepare(String sql) throws SQLException {
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            stmt = pst;
            return pst;
        }
        finally {
            cleanUp();
        }
    }

    private List<Task> query(String where, String orderBy) throws DatabaseException {
        List<Task> result = new ArrayList<Task>();
        try {
            String sql = "SELECT id, description, priority, completed, " +
                "dueDate, alert, daysBefore, obs FROM todo ";
            if (where != null)
                sql += "WHERE " + where + " ";
            if (orderBy != null)
                sql += "ORDER BY " + orderBy;
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt(1));
                task.setDescription(rs.getString(2));
                task.setPriority(rs.getInt(3));
                task.setCompleted(rs.getBoolean(4));
                task.setDueDate(rs.getDate(5));
                task.setAlert(rs.getBoolean(6));
                task.setDaysBefore(rs.getInt(7));
                task.setObs(rs.getString(8));
                result.add(task);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Cannot fetch from database", e);
        }
        finally {
            cleanUp();
        }
        return result;
    }

    private void modify(String sql, Task task) throws DatabaseException {
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            stmt = pst;
            pst.setString(1, task.getDescription());
            pst.setInt(2, task.getPriority());
            pst.setBoolean(3, task.isCompleted());
            if (task.getDueDate() == null)
                pst.setDate(4, null);
            else
                pst.setDate(4, new Date(task.getDueDate().getTime()));
            pst.setBoolean(5, task.getAlert());
            pst.setInt(6, task.getDaysBefore());
            pst.setString(7, task.getObs());
            pst.executeUpdate();
        }
        catch (SQLException e) {
            throw new DatabaseException("Cannot update the database", e);
        }
        finally {
            cleanUp();
        }
    }

    public List<Task> listAllTasks(boolean priorityOrDate) throws DatabaseException {
        return query(null, priorityOrDate ?
            "priority, dueDate, description" : "dueDate, priority, description");
    }

    public List<Task> listTasksWithAlert() throws ModelException {
        return query("alert = true AND "
                + "datediff('dd', CURRENT_TIMESTAMP, CAST(dueDate AS TIMESTAMP)) <= daysBefore",
                "dueDate, priority, description");
    }

    public void addTask(Task task) throws ValidationException, DatabaseException {
        validate(task);
        String sql = "INSERT INTO todo (" +
            "description, priority, completed, dueDate, alert," +
            "daysBefore, obs) VALUES (?, ?, ?, ?, ?, ?, ?)";
        modify(sql, task);
    }
    
    public void updateTask(Task task) throws ValidationException, DatabaseException {
        validate(task);
        String sql = "UPDATE todo SET " +
            "description = ?, priority = ?, completed = ?, dueDate = ?, " +
            "alert = ?, daysBefore = ?, obs = ? " +
            "WHERE id = " + task.getId();
        modify(sql, task);
    }

    public void markAsCompleted(int id, boolean completed) throws DatabaseException {
        update("UPDATE todo SET completed = " + completed + " " +
                "WHERE id = " + id);
    }
    
    public void removeTask(int id) throws DatabaseException {
        update("DELETE FROM todo WHERE id = " + id);
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    private void validate(Task task) throws ValidationException {
        if (isEmpty(task.getDescription()))
            throw new ValidationException("Must provide a task description");
    }

}
