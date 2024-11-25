package org.wip.moneymanager.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database implements AutoCloseable {

    protected Connection con = null;
    private static final String TEMPLATE_PATH = "src/main/resources/org/wip/moneymanager/databases/user_template.db";
    private static final String USER_DB_FOLDER = "Data/user_dbs";
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
            if(con != null) {
                con.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public static void createNewUserDB(int id) throws IOException {
        Path templatePath = Paths.get(TEMPLATE_PATH); // Percorso assoluto o relativo del template
        Path userDbPath = Paths.get(USER_DB_FOLDER, id + ".db"); // Destinazione


        try {
            Files.copy(templatePath, userDbPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File db copy success: " + userDbPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Errore during copy db", e);
        }
    }


    protected <T> Task<T> asyncCall(Callable<T> callable) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };
        new Thread(task).start();  // Start the task in a new thread
        return task;
    }


}