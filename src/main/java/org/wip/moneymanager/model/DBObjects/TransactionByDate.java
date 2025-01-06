package org.wip.moneymanager.model.DBObjects;

import java.util.ArrayList;
import java.util.List;

public class TransactionByDate {
    private Integer date;
    private List<Integer> transactionIds;

    public TransactionByDate(Integer date) {
        this.date = date;
        this.transactionIds = new ArrayList<>();
    }

    public Integer getDate() {
        return date;
    }

    public List<Integer> getTransactionIds() {
        return transactionIds;
    }

    public void addTransactionId(Integer transactionId) {
        this.transactionIds.add(transactionId);
    }
}
