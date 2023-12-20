package todo.model;

import java.io.File;
import java.util.prefs.*;

public class Parameters {
    
    private static final String jdbcDriver = "org.hsqldb.jdbcDriver";
    private static final String defaultUrl = "jdbc:hsqldb:";
    private static final String defaultOptions = ";shutdown=true";
    private static final String defaultDatabase = "db/todo";

    private Preferences prefs = Preferences.userNodeForPackage(Parameters.class);
    
    public String getDatabase() {
        return prefs.get("database", System.getProperty("user.home")
                + File.separator + defaultDatabase);
    }

    public void setDatabase(String database) {
        prefs.put("database", database);
    }
    
    public String getJdbcUrl() {
        return defaultUrl + getDatabase() + defaultOptions;
    }
    
    public String getJdbcDriver() {
        return jdbcDriver;
    }
}
