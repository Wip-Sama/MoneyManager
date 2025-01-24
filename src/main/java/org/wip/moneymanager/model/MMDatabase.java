package org.wip.moneymanager.model;

import javafx.concurrent.Task;
import org.wip.moneymanager.model.DBObjects.dbCurrency;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.utility.Encrypter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MMDatabase extends Database {
    private static MMDatabase instance = null;

    public static MMDatabase getInstance() {
        if (instance == null) {
            instance = new MMDatabase();
        }
        return instance;
    }

    private MMDatabase() {
        super("data/money_manager.db");
    }

    public Task<Boolean> checkUpdate_date() {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT CASE WHEN EXISTS (SELECT 1 FROM Currency WHERE update_date < date('now', 'start of day')) THEN 1 ELSE 0 END AS result;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                int res = rs.getInt("result");
                stmt.close();
                return res == 1;
            }
            return false;
        });
    }

    public Task<Boolean> createCurrency(String name, double value) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Currency (name, value) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setDouble(2, value);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> deleteCurrency(String name) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Currency WHERE name=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public double getCurrency(String name) {
        if (isConnected()) {
            try {
                String query = "SELECT value FROM Currency WHERE LOWER(name) = LOWER(?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name.trim());

                ResultSet rs = stmt.executeQuery();
                double currency = 0;
                if (rs.next()) {
                    currency = rs.getDouble("value");
                }
                stmt.close();
                return currency;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }


    public double convertCurrency(String from, String to, double amount) {
        double fromValue = getCurrency(from);
        double toValue = getCurrency(to);
        return ((amount * fromValue) / toValue);
    }


    public Task<List<dbCurrency>> getAllCurrency() {
        return asyncCall(() -> {
            List<dbCurrency> currencies = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Currency;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    currencies.add(new dbCurrency(rs));
                }
                stmt.close();
            }
            return currencies;
        });
    }

    public Task<List<String>> getAllCurrencyName() {
        return asyncCall(() -> {
            List<String> currencies = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT name FROM Currency;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    currencies.add(rs.getString("name"));
                }
                stmt.close();
            }
            return currencies;
        });
    }

    public Task<Boolean> checkPassword(String username, String password) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT password_hash from Users where username=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                String passwd = rs.getString("password_hash");

                if (rs.next()) {
                    return Encrypter.check_string_bcrypt(password, passwd);
                }
                stmt.close();
            }
            return false;
        });
    }

    public Task<Boolean> createUser(String username, String password) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Users (username, password_hash) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, Encrypter.encrypt_string_bcrypt(password));
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1); // Ottieni l'ID generato
                    rs.close();
                    stmt.close();
                    createNewUserDB(userId);
                }
                rs.close();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> deleteUser(String username) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Users WHERE username=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<dbUser> getUser(String username) {
        return asyncCall(() -> {
            dbUser dbUser = null;
            if (isConnected()) {
                String query = "SELECT * FROM Users where username=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbUser = new dbUser(rs);
                }
                stmt.close();
            }
            return dbUser;
        });
    }

    public Task<Boolean> userExists(String username) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT COUNT(*) AS count FROM Users WHERE username = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                boolean exists = rs.getInt("count") > 0;
                stmt.close();
                return exists;
            }
            return false;
        });
    }

    public Task<dbUser> getUser(int uid) {
        return asyncCall(() -> {
            dbUser dbUser = null;
            if (isConnected()) {
                String query = "SELECT * FROM Users where id=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, uid);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbUser = new dbUser(rs);
                }
                stmt.close();
            }
            return dbUser;
        });
    }
}
