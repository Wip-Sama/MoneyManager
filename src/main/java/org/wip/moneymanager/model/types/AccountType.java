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
        return switch (account_type) {
            case CASH -> Data.lsp.lsb("accounttype.cash").get();
            case BANK -> Data.lsp.lsb("accounttype.bank").get();
            case CREDIT_CARD -> Data.lsp.lsb("accounttype.credit_card").get();
            case DEBIT_CARD -> Data.lsp.lsb("accounttype.debit_card").get();
            case SAVINGS -> Data.lsp.lsb("accounttype.savings").get();
            case INVESTMENT -> Data.lsp.lsb("accounttype.investment").get();
            case LOAN -> Data.lsp.lsb("accounttype.loan").get();
            case OTHER -> Data.lsp.lsb("accounttype.other").get();
        };
    }

    public static AccountType fromInt(int account_type) {
        return switch (account_type) {
            case 0 -> AccountType.CASH;
            case 1 -> AccountType.BANK;
            case 2 -> AccountType.CREDIT_CARD;
            case 3 -> AccountType.DEBIT_CARD;
            case 4 -> AccountType.SAVINGS;
            case 5 -> AccountType.INVESTMENT;
            case 6 -> AccountType.LOAN;
            case 7 -> AccountType.OTHER;
            default -> null;
        };
    }
}
