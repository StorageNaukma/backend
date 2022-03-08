package org.example;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class DBListener implements javax.servlet.ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DB.connect();
        ItemsTable.create();
        GroupsTable.create();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DB.close();
    }
}