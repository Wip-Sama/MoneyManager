package org.wip.moneymanager.model.types;

import org.wip.moneymanager.model.Data;

public enum AccountType {
    CASH,
    BANK,
    CREDIT_CARD,
    DEBIT_CARD,
    SAVINGS,
    INVESTMENT,
    LOAN,
    OTHER;

    public static AccountType fromString(String account_type) {
        account_type = account_type.toLowerCase();
        if (Data.lsp.lsb("accounttype.cash").get().toLowerCase().equals(account_type)) {
            return AccountType.CASH;
        } else if (Data.lsp.lsb("accounttype.bank").get().toLowerCase().equals(account_type)) {
            return AccountType.BANK;
        } else if (Data.lsp.lsb("accounttype.credit_card").get().toLowerCase().equals(account_type)) {
            return AccountType.CREDIT_CARD;
        } else if (Data.lsp.lsb("accounttype.debit_card").get().toLowerCase().equals(account_type)) {
            return AccountType.DEBIT_CARD;
        } else if (Data.lsp.lsb("accounttype.savings").get().toLowerCase().equals(account_type)) {
            return AccountType.SAVINGS;
        } else if (Data.lsp.lsb("accounttype.investment").get().toLowerCase().equals(account_type)) {
            return AccountType.INVESTMENT;
        } else if (Data.lsp.lsb("accounttype.loan").get().toLowerCase().equals(account_type)) {
            return AccountType.LOAN;
        } else if (Data.lsp.lsb("accounttype.other").get().toLowerCase().equals(account_type)) {
            return AccountType.OTHER;
        }
        return null;
    }

    public static String toString(AccountType account_type) {
        return Data.lsp.lsb("accounttype."+account_type.toString().toLowerCase()).get();
    }

    public static AccountType fromInt(int type) {
        if (type < 0 || type >= values().length) {
            return null;
        }
        return values()[type];
    }
}
