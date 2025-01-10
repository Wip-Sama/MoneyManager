package org.wip.moneymanager.model;

import javafx.concurrent.Task;
import org.wip.moneymanager.components.Tag;
import org.wip.moneymanager.model.DBObjects.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.Statement;

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
                String query = "INSERT INTO Tag (name, color) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, color);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeTag(String name) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Tag WHERE name=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllTag() {
        return asyncCall(() -> {
            if (isConnected()) {
                // WHERE id != -1 non serve ma altrimenti intellij eprnede che la query pulisce il db come un errore
                String query = "DELETE FROM Tag WHERE id != -1;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<dbTag> getTag(String name) {
        return asyncCall(() -> {
            dbTag dbTag = null;
            if (isConnected()) {
                String query = "SELECT * FROM Tag WHERE name = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbTag = new dbTag(rs, this);
                }
                stmt.close();
            }
            return dbTag;
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

    public Task<List<dbTransaction>> getAlltransactionBetween(int start, int end) {
        return asyncCall(() -> {
            List<dbTransaction> dbTransactions = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transactions WHERE date BETWEEN ? AND ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, start);
                stmt.setInt(2, end);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    dbTransactions.add(new dbTransaction(rs, this));
                }
                stmt.close();
            }
            return dbTransactions;
        });
    }

    public Task<Boolean> createTransaction_tags(int transaction, int tag) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "INSERT INTO Transaction_tags (transaction, tag) VALUES (?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                stmt.setInt(2, tag);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeTransaction_tags(int transaction, int tag) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Transaction_tags WHERE 'transaction' = ? AND tag = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                stmt.setInt(1, tag);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllTransaction_tags() {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Transaction_tags;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<List<dbTransaction_tags>> getAllTransaction_tags_per_transaction(int transaction) {
        return asyncCall(() -> {
            List<dbTransaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags WHERE 'transaction' = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, transaction);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tt.add(new dbTransaction_tags(rs, this));
                }
                stmt.close();
            }
            return tt;
        });
    }

    public Task<List<dbTransaction_tags>> getAllTransaction_tags() {
        return asyncCall(() -> {
            List<dbTransaction_tags> tt = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT * FROM Transaction_tags;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tt.add(new dbTransaction_tags(rs, this));
                }
                stmt.close();
            }
            return tt;
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

    public Task<dbCategory> getCategory(int id) {
        return asyncCall(() -> {
            dbCategory dbCategory = null;
            if (isConnected()) {
                String query = "SELECT * FROM Categories WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCategory = new dbCategory(rs, this);
                }
                stmt.close();
            }
            return dbCategory;
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
                String remove_from_transactions = "UPDATE Transactions SET category = null WHERE category = ?;";
                PreparedStatement stmt1 = con.prepareStatement(remove_from_transactions);
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                stmt1.close();

                Task<List<dbCategory>> subc = getAllSubcategories(id);
                subc.run();
                subc.get().forEach(sub -> forceRemoveCategory(sub.id()));

                String remove_subcategory = "DELETE FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt2 = con.prepareStatement(remove_subcategory);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                stmt2.close();

                String remove_category = "DELETE FROM Categories WHERE id = ?;";
                PreparedStatement stmt3 = con.prepareStatement(remove_category);
                stmt3.setInt(1, id);
                stmt3.executeUpdate();
                stmt3.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeCategory(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Categories WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }

    public Task<Boolean> removeAllSubcategories(int parent_category) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Categories WHERE parent_category = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, parent_category);
                stmt.executeUpdate();
                stmt.close();
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


    public Task<dbAccount> getAccount(int id) {
        return asyncCall(() -> {
            dbAccount dbAccount = null;
            if (isConnected()) {
                String query = "SELECT * FROM Accounts WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbAccount = new dbAccount(rs, this);
                }
                stmt.close();
            }
            return dbAccount;
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
                String query = "DELETE FROM Accounts WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();
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


    //Tutti metodi per la schermata che servono alla schermata transictions


    //Entrate e Spese
    public Task<Boolean> addTransaction(int date, int type, double amount, int account, String note, Integer category) {
        return asyncCall(() -> {
            try {
                String query = "INSERT INTO Transactions (date, type, amount, account, note, category) VALUES (?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, date);
                stmt.setInt(2, type);
                stmt.setDouble(3, amount);
                stmt.setInt(4, account);

                if (note != null && !note.isEmpty()) {
                    stmt.setString(5, note);
                } else {
                    stmt.setNull(5, Types.VARCHAR);
                }


                if (category != null) {
                    stmt.setInt(6, category);
                } else {
                    stmt.setNull(6, java.sql.Types.INTEGER);
                }

                int rowsAffected = stmt.executeUpdate();
                stmt.close();

                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("SQL Error during transaction insertion: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }


    //metodo per trasferimenti con aggiornamento dei bilanci, metto commenti per farti capire gli step
    public Task<Boolean> addTransferWithBalanceUpdate(int date, double amount, int account, int secondAccount, String note) {
        return asyncCall(() -> {
            try {
                // Step 1: Inserire il trasferimento nella tabella Transactions
                String insertTransactionQuery = "INSERT INTO Transactions (date, type, amount, account, second_account, note) VALUES (?, ?, ?, ?, ?, ?);";
                PreparedStatement transactionStmt = con.prepareStatement(insertTransactionQuery);
                transactionStmt.setInt(1, date);
                transactionStmt.setInt(2, 2); // Tipo 2: trasferimento
                transactionStmt.setDouble(3, amount);
                transactionStmt.setInt(4, account);
                transactionStmt.setInt(5, secondAccount);

                if (note != null && !note.isEmpty()) {
                    transactionStmt.setString(6, note);
                } else {
                    transactionStmt.setNull(6, java.sql.Types.VARCHAR);
                }

                int rowsTransaction = transactionStmt.executeUpdate();
                transactionStmt.close();

                // Step 2: Aggiornare il saldo dell'account di origine
                String updateSourceAccountQuery = "UPDATE Accounts SET balance = balance - ? WHERE id = ?;";
                PreparedStatement sourceAccountStmt = con.prepareStatement(updateSourceAccountQuery);
                sourceAccountStmt.setDouble(1, amount);
                sourceAccountStmt.setInt(2, account);
                int rowsSourceAccount = sourceAccountStmt.executeUpdate();
                sourceAccountStmt.close();

                // Step 3: Aggiornare il saldo dell'account di destinazione
                String updateDestinationAccountQuery = "UPDATE Accounts SET balance = balance + ? WHERE id = ?;";
                PreparedStatement destinationAccountStmt = con.prepareStatement(updateDestinationAccountQuery);
                destinationAccountStmt.setDouble(1, amount);
                destinationAccountStmt.setInt(2, secondAccount);
                int rowsDestinationAccount = destinationAccountStmt.executeUpdate();
                destinationAccountStmt.close();

                // Verifica che tutte le operazioni abbiano avuto successo
                return rowsTransaction > 0 && rowsSourceAccount > 0 && rowsDestinationAccount > 0;
            } catch (SQLException e) {
                System.err.println("SQL Error during transfer with balance update: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }


    //metodo per i tags commento tutto per farti capire qualcosa
    public Task<Boolean> linkTagToTransaction(String transactionName, String tagName) {
        return asyncCall(() -> {
            if (isConnected()) {
                try {
                    // Recupera l'ID della transazione in base al nome
                    String transactionIdQuery = "SELECT id FROM Transactions WHERE note = ?;";
                    PreparedStatement transactionStmt = con.prepareStatement(transactionIdQuery);
                    transactionStmt.setString(1, transactionName);
                    ResultSet transactionRs = transactionStmt.executeQuery();

                    if (!transactionRs.next()) {
                        transactionStmt.close();
                        System.err.println("Transaction not found for name: " + transactionName);
                        return false;
                    }
                    int transactionId = transactionRs.getInt("id");
                    transactionStmt.close();

                    // Recupera l'ID del tag in base al nome
                    String tagIdQuery = "SELECT id FROM Tag WHERE name = ?;";
                    PreparedStatement tagStmt = con.prepareStatement(tagIdQuery);
                    tagStmt.setString(1, tagName);
                    ResultSet tagRs = tagStmt.executeQuery();

                    if (!tagRs.next()) {
                        tagStmt.close();
                        System.err.println("Tag not found for name: " + tagName);
                        return false;
                    }
                    int tagId = tagRs.getInt("id");
                    tagStmt.close();

                    // Collega l'ID della transazione con l'ID del tag
                    String linkQuery = "INSERT INTO Transaction_tags (transaction, tag) VALUES (?, ?);";
                    PreparedStatement linkStmt = con.prepareStatement(linkQuery);
                    linkStmt.setInt(1, transactionId);
                    linkStmt.setInt(2, tagId);
                    int rowsInserted = linkStmt.executeUpdate();
                    linkStmt.close();

                    return rowsInserted > 0;
                } catch (SQLException e) {
                    System.err.println("SQL Error during linking tag to transaction: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        });
    }

    public Task<String> getIDAccountFromName(String name) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT id FROM Accounts WHERE name = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String id = rs.getString("id");
                    stmt.close();
                    return id;
                }
                stmt.close();
            }
            throw new IllegalArgumentException("Account not found");
        });
    }

    public Task<Boolean> removeTransaction(int id) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "DELETE FROM Transactions WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();
                return true;
            }
            return false;
        });
    }


    public Task<List<Integer>> getAllDaysOfTransaction() {
        return asyncCall(() -> {
            List<Integer> transactionDates = new ArrayList<>();
            if (isConnected()) {
                String query = "SELECT date FROM Transactions;";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    transactionDates.add(rs.getInt("date")); // Legge la colonna 'date'

                }
                stmt.close();
            }

            return transactionDates;
        });

    }

    public Task<List<TransactionByDate>> getAllDaysOfTransaction(String selectedCategory, String selectedAccount, List<String> selectedTags) {
        return asyncCall(() -> {
            List<TransactionByDate> transactionByDateList = new ArrayList<>();

            // Recupera gli ID della categoria, account e tag
            Integer categoryId = getCategoryIdByName(selectedCategory);
            Integer accountId = getAccountIdByName(selectedAccount);
            List<Integer> tagIds = getTagIdsByNames(selectedTags);

            // Inizializza la parte base della query
            StringBuilder queryBuilder = new StringBuilder("SELECT t.id, t.date FROM Transactions t WHERE 1=1");

            // Lista per i parametri di filtro da passare alla PreparedStatement
            List<Object> params = new ArrayList<>();

            // Aggiungi il filtro per la categoria se l'ID è valido
            if (categoryId != null) {
                queryBuilder.append(" AND t.category = ?");
                params.add(categoryId);
            }

            // Aggiungi il filtro per l'account se l'ID è valido
            if (accountId != null) {
                queryBuilder.append(" AND (t.account = ? OR t.second_account = ?)");
                params.add(accountId);
                params.add(accountId);  // Per second_account
            }

            // Aggiungi il filtro per i tag se ci sono tag selezionati
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

            // Aggiungi la parte finale della query per eseguire la selezione
            String query = queryBuilder.toString();

            if (isConnected()) {
                // Esegui la query preparata
                PreparedStatement stmt = con.prepareStatement(query);

                // Imposta i parametri della query
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Integer transactionId = rs.getInt("id");
                    Integer date = rs.getInt("date");

                    // Trova o crea una entry per quella data
                    TransactionByDate transactionByDate = findTransactionByDate(transactionByDateList, date);
                    if (transactionByDate == null) {
                        transactionByDate = new TransactionByDate(date);
                        transactionByDateList.add(transactionByDate);
                    }

                    // Aggiungi l'ID della transazione alla lista della data corrispondente
                    transactionByDate.addTransactionId(transactionId);
                }
                stmt.close();
            }

            return transactionByDateList;
        });
    }

    // Metodo per trovare una data esistente o crearne una nuova
    private TransactionByDate findTransactionByDate(List<TransactionByDate> transactionByDateList, Integer date) {
        for (TransactionByDate tbd : transactionByDateList) {
            if (tbd.getDate().equals(date)) {
                return tbd;
            }
        }
        return null; // Se non esiste, ritorna null
    }




    private Integer getCategoryIdByName(String categoryName) throws SQLException {
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

    private Integer getAccountIdByName(String accountName) throws SQLException {
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
                stmt.setString(i + 1, tagNames.get(i)); // Imposta ogni nome del tag come parametro
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

    public Task<String> getCategoryFromId(Integer category) {
        return asyncCall(() -> {
            if (isConnected()) {
                String query = "SELECT name FROM Categories WHERE id = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, category);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    stmt.close();
                    return name;
                }
                stmt.close();
            }
            throw new IllegalArgumentException("Category not found");
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

    public Task<Boolean> addNewTransaction(int date, int type, double amount, String account, String secondAccount, String note, String category, List<Tag> listTags) {
        return asyncCall(() -> {
            List<String> tags = new ArrayList<>();
            for (Tag t : listTags) {
                tags.add(t.getTag());
                System.out.println(t.getTag());
            }

            // recupera gli ID della categoria, account e tag
            Integer accountId = getAccountIdByName(account);
            List<Integer> tagIds = getTagIdsByNames(tags);

            // Gestione delle transazioni di tipo 0 e 1
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
                    // ottieni l'ID della transazione appena inserita
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int transactionId = generatedKeys.getInt(1);

                        // collega i tag alla transazione
                        linkTagsToTransaction(transactionId, tagIds);
                    }
                    generatedKeys.close();
                }
                stmt.close();

                return rowsAffected > 0;
            }
            // gestione delle transazioni di tipo 2 (trasferimenti)
            else if (type == 2) {
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
                    // ottiene l'ID della transazione appena inserita
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int transactionId = generatedKeys.getInt(1);

                        linkTagsToTransaction(transactionId, tagIds);
                    }
                    generatedKeys.close();
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

}





