package org.wip.moneymanager.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database implements AutoCloseable {

    protected Connection con = null;

    public Connection getConnection() {
        return con;
    }

    protected final ExecutorService executorService = Executors.newCachedThreadPool();

    protected Database(String db) {
        try {
            String url = "jdbc:sqlite:" + db;
            con = DriverManager.getConnection(url);
            if(isConnected())
                System.out.println("Database "+db.substring(0, db.length()-3)+" connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if(con != null)
                con.close();
        } catch (SQLException ignored) {
        }
    }

    protected <T> Task<T> asyncCall(Callable<T> callable) {
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };
    }
}