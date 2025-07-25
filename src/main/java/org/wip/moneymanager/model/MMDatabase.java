package org.wip.moneymanager.model;

import javafx.concurrent.Task;
import org.wip.moneymanager.model.DBObjects.dbCurrency;
import org.wip.moneymanager.model.DBObjects.dbUser;
import org.wip.moneymanager.utility.Encrypter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    public Task<dbCurrency> getCurrency(String name) {
        return asyncCall(() -> {
            dbCurrency currency = null;
            if (isConnected()) {
                String query = "SELECT * FROM Currency where name=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currency = new dbCurrency(rs);
                }
                stmt.close();
            }
            return currency;
        });
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
                stmt.close();
                if (rs.next()) {
                    return Encrypter.check_string_bcrypt(password, passwd);
                }
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
