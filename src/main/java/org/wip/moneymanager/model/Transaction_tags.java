package org.wip.moneymanager.model;

import java.sql.ResultSet;
import java.util.Objects;

public final class Transaction_tags {
    private final UserDatabase db;
    private final int transaction;
    private final int tag;

    public Transaction_tags(int transaction, int tag, UserDatabase db) {
        this.transaction = transaction;
        this.tag = tag;
        this.db = db;
    }

    public Transaction_tags(ResultSet rs, UserDatabase db) throws Exception {
        // Prendiamo il db solo per prassi, per tenere tutte le classi simili in caso di futuri cambiamenti
        this(
                rs.getInt("transaction"),
                rs.getInt("tag"),
                db
        );
    }

    public int transaction() {
        return transaction;
    }

    public int tag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transaction_tags) obj;
        return this.transaction == that.transaction &&
                this.tag == that.tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, tag);
    }

    @Override
    public String toString() {
        return "Transaction_tags[" +
                "transaction=" + transaction + ", " +
                "tag=" + tag + ']';
    }

}
