package org.wip.moneymanager.model;

import javafx.concurrent.Task;
import org.wip.moneymanager.components.Tag;
import org.wip.moneymanager.model.DBObjects.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.sql.Statement;
import java.util.stream.Collectors;

public class UserDatabase extends Database {
    private static UserDatabase instance = null;

    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    public UserDatabase() {
        super("data/user_dbs/" + Data.dbUser.id() + ".db");
    }

    public Task<Boolean> createTag(String name, String color) {
        return asyncCall(() -> {
            if (isConnected()) {
                String checkQuery = "SELECT COUNT(*) FROM Tag WHERE name = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, name);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                rs.close();
                checkStmt.close();

                if (count > 0) {
                    return false;
                }

                String insertQuery = "INSERT INTO Tag (name, color) VALUES (?, ?);";
                PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                insertStmt.setString(1, name);
                insertStmt.setString(2, color);
                insertStmt.executeUpdate();
                insertStmt.close();

                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeTag(String name) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query1 = "DELETE FROM Transactions_Tags WHERE Tag_id = (SELECT id FROM Tag WHERE name = ?);";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, name);
                stmt1.executeUpdate();
                stmt1.close();

                String query2 = "DELETE FROM Tag WHERE name = ?;";
                PreparedStatement stmt2 = con.prepareStatement(query2);
                stmt2.setString(1, name);
                stmt2.executeUpdate();
                stmt2.close();

                return true;
            }
            return false;
        });
    }

    public Task<List<dbTag>> getAllTag() {
        return asyncCall(() -> {
            List<dbTag> dbTags = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Tag;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    dbTags.add(new dbTag(rs, this));
                }
                stmt.close();
            }
            return dbTags;
        });
    }

    public Task<List<dbCategory>> getAllCategories(int type) {
        return asyncCall(() -> {
            List<dbCategory> categories = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE type == ? AND parent_category is null;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, type);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    categories.add(new dbCategory(rs, this));
                }
                stmt.close();
            }
            return categories;
        });
    }

    public Task<dbCategory> getCategory(String name, int type) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE name = ? AND type = ? AND parent_category is null;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                }
                stmt.close();
            }
            return dbCategory;
        });
    }

    public Task<dbCategory> getSubcategory(String name, int type, int parent_category) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE name = ? AND type = ? AND parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.setInt(3, parent_category);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                }
                stmt.close();
            }
            return dbCategory;
        });
    }

    public Task<Boolean> createCategory(String name, int type) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Categories (name, type) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> createSubcategory(String name, int type, int parent_category) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Categories (name, type, parent_category) VALUES (?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.setInt(3, parent_category);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> forceRemoveCategory(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                getAllSubcategories(id).get().forEach(sub -> removeTransactionsWithCategory(sub.id()).run());
                removeTransactionsWithCategory(id).run();

                String remove_subcategory = "DELETE FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt3 = con.prepareStatement(remove_subcategory);
                stmt3.setInt(1, id);
                stmt3.executeUpdate();
                stmt3.close();

                String remove_category = "DELETE FROM Categories WHERE id = ?;";
                PreparedStatement stmt4 = con.prepareStatement(remove_category);
                stmt4.setInt(1, id);
                stmt4.executeUpdate();
                stmt4.close();
                return true;
            }
            return false;
        });
    }

    public Task<List<dbCategory>> getAllSubcategories(int parent_category) {
        return asyncCall(() -> {
            List<dbCategory> categories = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, parent_category);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    categories.add(new dbCategory(rs, this));
                }
                stmt.close();
            }
            return categories;
        });
    }

    public Task<List<dbAccount>> getAllAccounts() {
        return asyncCall(() -> {
            List<dbAccount> accounts = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Accounts;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    accounts.add(new dbAccount(rs, this));
                }
                stmt.close();
            }
            return accounts;
        });
    }

    public Task<List<String>> getAllAccountNames() {
        return asyncCall(() -> {
            List<String> accountNames = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT name FROM Accounts;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    accountNames.add(rs.getString("name"));
                }
                stmt.close();
            }
            return accountNames;
        });
    }

    public Task<List<String>> getMainCategoryNamesByType(int type) {
        return asyncCall(() -> {
            List<String> mainCategoryNames = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT name FROM Categories WHERE parent_category IS NULL AND type = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, type); // Imposta il parametro del tipo
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    mainCategoryNames.add(rs.getString("name"));
                }
                stmt.close();
            }
            return mainCategoryNames;
        });
    }

    public Task<List<String>> getMainCategoryNames() {
        return asyncCall(() -> {
            List<String> mainCategoryNames = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT name FROM Categories WHERE parent_category IS NULL;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    mainCategoryNames.add(rs.getString("name"));
                }
                stmt.close();
            }
            return mainCategoryNames;
        });
    }


    public Task<List<String>> getSubCategoriesByMainCategory(String mainCategoryName) {
        return asyncCall(() -> {
            List<String> subCategoryNames = new ArrayList<>();
            if (isConnected()) {
                // Trova l'ID della categoria principale
                String mainCategoryQuery = "SELECT id FROM Categories WHERE name = ? AND parent_category IS NULL;";
                PreparedStatement mainStmt = con.prepareStatement(mainCategoryQuery);
                mainStmt.setString(1, mainCategoryName);
                ResultSet mainRs = mainStmt.executeQuery();
                if (mainRs.next()) {
                    int mainCategoryId = mainRs.getInt("id");
                    mainStmt.close();

                    // Trova le sottocategorie
                    String subCategoryQuery = "SELECT name FROM Categories WHERE parent_category = ?;";
                    PreparedStatement subStmt = con.prepareStatement(subCategoryQuery);
                    subStmt.setInt(1, mainCategoryId);
                    ResultSet subRs = subStmt.executeQuery();
                    while (subRs.next()) {
                        subCategoryNames.add(subRs.getString("name"));
                    }
                    subStmt.close();
                } else {
                    mainStmt.close();
                    throw new IllegalArgumentException("Main category not found or not a root category");
                }
            }
            return subCategoryNames;
        });
    }


    public Task<Boolean> removeAccount(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                // Rimuovi tutte le transazioni in cui il conto appare come Account_id o Second_account_id
                String query1 = "DELETE FROM Transactions WHERE account = ? OR second_account = ?;";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setInt(1, id);
                stmt1.setInt(2, id);
                stmt1.executeUpdate();
                stmt1.close();

                // Ora elimina il conto dalla tabella Accounts
                String query2 = "DELETE FROM Accounts WHERE id = ?;";
                PreparedStatement stmt2 = con.prepareStatement(query2);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();

                return true;
            }
            return false;
        });
    }

    public Task<Boolean> addAccount(String name, int type, double balance, int creationDate, int includeIntoTotals, String currency) {
        return asyncCall(() -> {
            try {
                String query = "INSERT INTO Accounts (name, type, balance, creation_date, include_into_totals , currency) VALUES (?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setInt(2, type);
                stmt.setDouble(3, balance);
                stmt.setInt(4, creationDate);
                stmt.setInt(5, includeIntoTotals);
                stmt.setString(6, currency);
                int rowsAffected = stmt.executeUpdate();
                stmt.close();


                return true;
            } catch (SQLException e) {
                System.err.println("SQL Error during account insertion: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    public Task<Boolean> removeTransaction(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT type, amount, account, second_account FROM Transactions WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet resultSet = stmt.executeQuery();

                if (!resultSet.next()) {
                    return false;
                }

                int type = resultSet.getInt("type");
                double amount = resultSet.getDouble("amount");
                int accountId = resultSet.getInt("account");
                Integer secondAccountId = resultSet.getInt("second_account");

                stmt.close();

                // Aggiorna i bilanci dei conti coinvolti
                boolean success = true;


                if (type == 0 || type == 1) {
                    success &= updateAccountBalance(accountId, amount, false).get(); // Il get() blocca finché non ottieni il risultato
                } else if (type == 2) {
                    success &= updateAccountBalance(accountId, amount, true).get(); // Sottrai dal conto di origine
                    success &= updateAccountBalance(secondAccountId, amount, false).get(); // Aggiungi al conto di destinazione
                }

                if (!success) {
                    return false;
                }

                String query1 = "DELETE FROM Transactions_Tags WHERE Transaction_id = ?;";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                stmt1.close();

                String query2 = "DELETE FROM Transactions WHERE id = ?;";
                PreparedStatement stmt2 = con.prepareStatement(query2);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();

                return true;
            }
            return false;
        });
    }


    public String getCurrencyFromAccount(int accountId) throws SQLException {
        String query = "SELECT currency FROM Accounts WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("currency");
            }
        }
        return null;
    }

    public Task<Boolean> removeTransactionsWithCategory(int categoryId) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT id FROM Transactions WHERE category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();
                List<Integer> transactionIds = new ArrayList<>();
                while (rs.next()) {
                    transactionIds.add(rs.getInt("id"));
                }
                stmt.close();

                for (Integer transactionId : transactionIds) {
                    removeTransaction(transactionId).run();
                }

                return true;
            }
            return false;
        });
    }

    public Task<List<TransactionByDate>> getAllDaysOfTransaction(String selectedCategory, String selectedAccount, List<String> selectedTags) {
        return asyncCall(() -> {
            List<TransactionByDate> transactionByDateList = new ArrayList<>();
            Integer categoryId = getCategoryIdByName(selectedCategory);
            Integer accountId = getAccountIdByName(selectedAccount);
            List<Integer> tagIds = getTagIdsByNames(selectedTags);

            StringBuilder queryBuilder = new StringBuilder("SELECT t.id, t.date FROM Transactions t WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (categoryId != null) {
                queryBuilder.append(" AND t.category = ?");
                params.add(categoryId);
            }

            if (accountId != null) {
                queryBuilder.append(" AND (t.account = ? OR t.second_account = ?)");
                params.add(accountId);
                params.add(accountId);  // Per second_account
            }

            if (!tagIds.isEmpty()) {
                queryBuilder.append(" AND EXISTS (SELECT 1 FROM Transactions_Tags tt WHERE t.id = tt.transaction_id AND tt.tag_id IN (");
                for (int i = 0; i < tagIds.size(); i++) {
                    queryBuilder.append("?");
                    if (i < tagIds.size() - 1) {
                        queryBuilder.append(",");
                    }
                    params.add(tagIds.get(i));
                }
                queryBuilder.append("))");
            }


            String query = queryBuilder.toString();

            if (isConnected()) {
                PreparedStatement stmt = con.prepareStatement(query);

                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Integer transactionId = rs.getInt("id");
                    Integer date = rs.getInt("date");

                    TransactionByDate transactionByDate = findTransactionByDate(transactionByDateList, date);
                    if (transactionByDate == null) {
                        transactionByDate = new TransactionByDate(date);
                        transactionByDateList.add(transactionByDate);
                    }

                    transactionByDate.addTransactionId(transactionId);
                }
                stmt.close();
            }

            return transactionByDateList;
        });
    }

    public Task<List<dbTransaction>> getAllTransactionsWithCategory(int categoryId) {
        return asyncCall(() -> {
            List<dbTransaction> transactions = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transactions WHERE category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    transactions.add(new dbTransaction(rs, this));
                }
                stmt.close();
            }
            return transactions;
        });
    }

    private TransactionByDate findTransactionByDate(List<TransactionByDate> transactionByDateList, Integer date) {
        for (TransactionByDate tbd : transactionByDateList) {
            if (tbd.getDate().equals(date)) {
                return tbd;
            }
        }
        return null;
    }




    public Integer getCategoryIdByName(String categoryName) throws SQLException {
        if (categoryName == null) {
            return null;
        }

        String query = "SELECT id FROM Categories WHERE name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return null;
    }

    public Integer getAccountIdByName(String accountName) throws SQLException {
        if (accountName == null) {
            return null;
        }

        String query = "SELECT id FROM Accounts WHERE name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, accountName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return null;
    }

    private List<Integer> getTagIdsByNames(List<String> tagNames) throws SQLException {
        List<Integer> tagIds = new ArrayList<>();
        if (tagNames == null || tagNames.isEmpty()) {
            return tagIds;
        }

        // Crea una query con il numero corretto di "?" come segnaposto per i parametri
        String query = "SELECT id FROM Tag WHERE name IN (" + String.join(",", Collections.nCopies(tagNames.size(), "?")) + ")";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            // Imposta i parametri per la query preparata
            for (int i = 0; i < tagNames.size(); i++) {
                stmt.setString(i + 1, tagNames.get(i));
            }

            // Esegui la query
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tagIds.add(rs.getInt("id"));
            }
        }

        return tagIds;
    }

    public Task<List<dbTransaction>> fillCard(Integer unix) {
        return asyncCall(() -> {
            List<dbTransaction> transactionDates = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transactions WHERE date = ?;";
                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setInt(1, unix);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            transactionDates.add(new dbTransaction(rs, this));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Database not connected.");
            }
            return transactionDates;
        });

    }

    public Task<String> getNameAccountFromId(Integer id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT name FROM Accounts WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    stmt.close();
                    return name;
                }
                stmt.close();
            }
            throw new IllegalArgumentException("Account not found");
        });
    }

    public Task<List<dbTag>> getTagFromTransaction(Integer transactionId) {
        return asyncCall(() -> {
            List<dbTag> tagList = new ArrayList<>();
            if (isConnected()) {
                try {
                    String queryTags = "SELECT Tag_id FROM Transactions_tags WHERE Transaction_id = ?;";
                    PreparedStatement stmtTags = con.prepareStatement(queryTags);
                    stmtTags.setInt(1, transactionId);
                    ResultSet rsTags = stmtTags.executeQuery();
                    List<Integer> tagIds = new ArrayList<>();
                    while (rsTags.next()) {
                        tagIds.add(rsTags.getInt("tag_id"));
                    }
                    if (!tagIds.isEmpty()) {
                        String queryDetails = "SELECT id, name, color FROM Tag WHERE id IN (" +
                                String.join(",", Collections.nCopies(tagIds.size(), "?")) + ");";
                        PreparedStatement stmtDetails = con.prepareStatement(queryDetails);
                        for (int i = 0; i < tagIds.size(); i++) {
                            stmtDetails.setInt(i + 1, tagIds.get(i));
                        }
                        ResultSet rsDetails = stmtDetails.executeQuery();
                        while (rsDetails.next()) {
                            dbTag tag = new dbTag(rsDetails, this);
                            tagList.add(tag);
                        }
                        rsDetails.close();
                        stmtDetails.close();
                    }
                    rsTags.close();
                    stmtTags.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return tagList;
        });
    }


    public String getCategoryNameById(int categoryId) throws SQLException {
        String query = "SELECT name FROM Categories WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public String getAccountNameById(int accountName) throws SQLException {
        String query = "SELECT name FROM Accounts WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, accountName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public String getMainCategoryName(String subcategoryName) throws SQLException {
        String mainCategoryName = null;

        if (isConnected()) {
            String query = "SELECT c.name FROM Categories c " +
                    "INNER JOIN Categories sub ON c.id = sub.parent_category " +
                    "WHERE sub.name = ?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, subcategoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                mainCategoryName = rs.getString("name");
            }

            stmt.close();
        }

        return mainCategoryName;
    }


    public Task<Boolean> updateAccountBalance(int accountId, double amount, boolean isCredit) {
        return asyncCall(() -> {
            String updateBalanceQuery = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement updateStmt = con.prepareStatement(updateBalanceQuery)) {
                updateStmt.setDouble(1, isCredit ? amount : -amount);
                updateStmt.setInt(2, accountId);
                int rowsAffected = updateStmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }



    public Task<Boolean> addNewTransaction(int date, int type, double amount, String account, String secondAccount, String note, String category, List<Tag> listTags) {
        return asyncCall(() -> {
            List<String> tags = new ArrayList<>();
            for (Tag t : listTags) {
                tags.add(t.getTag());
            }

            Integer accountId = getAccountIdByName(account);
            List<Integer> tagIds = getTagIdsByNames(tags);

            if (type == 0 || type == 1) {
                Integer secondAccountId = null;
                Integer categoryId = getCategoryIdByName(category);

                String query = "INSERT INTO Transactions (date, type, amount, account, second_account, note, category) VALUES (?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, date);
                stmt.setInt(2, type);
                stmt.setDouble(3, amount);
                stmt.setInt(4, accountId);
                stmt.setNull(5, java.sql.Types.INTEGER); // second_account è null
                stmt.setString(6, note != null ? note : null); // Nota opzionale
                stmt.setInt(7, categoryId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int transactionId = generatedKeys.getInt(1);

                        linkTagsToTransaction(transactionId, tagIds);
                    }
                    generatedKeys.close();
                    updateAccountBalance(accountId, amount, type == 0); // true per entrata, false per uscita
                }
                stmt.close();

                return rowsAffected > 0;
            } else if (type == 2) {
                Integer secondAccountId = getAccountIdByName(secondAccount);
                Integer categoryId = null;

                String query = "INSERT INTO Transactions (date, type, amount, account, second_account, note, category) VALUES (?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, date);
                stmt.setInt(2, type);
                stmt.setDouble(3, amount);
                stmt.setInt(4, accountId);
                stmt.setInt(5, secondAccountId);
                stmt.setString(6, note != null ? note : null); // Nota opzionale
                stmt.setNull(7, java.sql.Types.INTEGER); // categoria è null

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int transactionId = generatedKeys.getInt(1);

                        linkTagsToTransaction(transactionId, tagIds);
                    }
                    generatedKeys.close();
                    updateAccountBalance(accountId, amount, false);
                    updateAccountBalance(secondAccountId, amount, true);
                }
                stmt.close();

                return rowsAffected > 0;
            }
            return false;
        });
    }

    public void linkTagsToTransaction(int transactionId, List<Integer> tagIds) throws SQLException {

        String query = "INSERT INTO Transactions_Tags (Transaction_id, Tag_id) VALUES (?, ?);";
        PreparedStatement stmt = con.prepareStatement(query);

        for (Integer tagId : tagIds) {
            stmt.setInt(1, transactionId);
            stmt.setInt(2, tagId);
            stmt.addBatch();
        }

        stmt.executeBatch();
        stmt.close();
    }

    public Task<Boolean> updateTransactionTags(int transactionId, List<String> currentTagNames, List<Integer> previousTagIds) {
        return asyncCall(() -> {
            if (isConnected()) {
                try {
                    List<Integer> currentTagIds = getTagIdsByNames(currentTagNames);

                    // Determina quali tag devono essere aggiunti
                    System.out.println("Current Tag IDs: " + currentTagIds);
                    System.out.println("Previous Tag IDs: " + previousTagIds);

                    // Determina quali tag devono essere aggiunti
                    Set<Integer> currentTagSet = new HashSet<>(currentTagIds);
                    Set<Integer> previousTagSet = new HashSet<>(previousTagIds);

                    List<Integer> tagsToAdd = currentTagSet.stream()
                            .filter(tagId -> !previousTagSet.contains(tagId))
                            .collect(Collectors.toList());

                    // Determina quali tag devono essere rimossi
                    List<Integer> tagsToRemove = previousTagSet.stream()
                            .filter(tagId -> !currentTagSet.contains(tagId))
                            .collect(Collectors.toList());

                    // Stampa per il debug
                    System.out.println("Tags to Add: " + tagsToAdd);
                    System.out.println("Tags to Remove: " + tagsToRemove);

                    // Inserisci i nuovi tag
                    String insertQuery = "INSERT INTO Transactions_Tags (Transaction_id, Tag_id) VALUES (?, ?);";
                    try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                        for (Integer tagId : tagsToAdd) {
                            insertStmt.setInt(1, transactionId);
                            insertStmt.setInt(2, tagId);
                            insertStmt.executeUpdate();
                        }
                    }

                    // Rimuovi i tag eliminati
                    String deleteQuery = "DELETE FROM Transactions_Tags WHERE Transaction_id = ? AND Tag_id = ?;";
                    try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                        for (Integer tagId : tagsToRemove) {
                            deleteStmt.setInt(1, transactionId);
                            deleteStmt.setInt(2, tagId);
                            deleteStmt.executeUpdate();
                        }
                    }
                    return true;
                } catch (SQLException e) {
                    con.rollback(); // Rollback in caso di errore
                    e.printStackTrace();
                    return false;
                } finally {
                    con.setAutoCommit(true); // Ripristina l'autocommit
                }
            }
            return false;
        });
    }

    public Task<List<dbTransaction>> getAllTransactions(List<Integer> transactionIds) {
        return asyncCall(() -> {
            List<dbTransaction> transactions = new ArrayList<>();

            if (isConnected()) {
                // Creiamo la query con un IN per gli ID delle transazioni
                StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Transactions WHERE id IN (");

                // Aggiungiamo i placeholder per ogni ID (?)
                for (int i = 0; i < transactionIds.size(); i++) {
                    queryBuilder.append("?");
                    if (i < transactionIds.size() - 1) {
                        queryBuilder.append(",");
                    }
                }
                queryBuilder.append(");");

                String query = queryBuilder.toString();

                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    // Impostiamo i parametri della query
                    for (int i = 0; i < transactionIds.size(); i++) {
                        stmt.setInt(i + 1, transactionIds.get(i)); // Imposta gli ID delle transazioni
                    }

                    // Eseguiamo la query
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            // Creiamo l'oggetto dbTransaction per ogni riga del ResultSet
                            transactions.add(new dbTransaction(rs, this));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Database not connected.");
            }

            return transactions;
        });
    }
}





