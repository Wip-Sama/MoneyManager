package org.wip.moneymanager.utility;

import javafx.beans.binding.StringBinding;
import org.wip.moneymanager.model.Data;

public class AlertMessages {

    public static StringBinding getOldPasswordEmptyMessage() {
        return Data.lsp.lsb("alert.old_password_empty");
    }

    public static StringBinding getOldPasswordIncorrectMessage() {
        return Data.lsp.lsb("alert.old_password_incorrect");
    }

    public static StringBinding getUsernameExistsMessage() {
        return Data.lsp.lsb("alert.username_exists");
    }

    public static StringBinding getPasswordEmptyMessage() {
        return Data.lsp.lsb("alert.password_empty");
    }

    public static StringBinding getPasswordSpecialMessage() {
        return Data.lsp.lsb("alert.password_special");
    }

    public static StringBinding getSuccessMessage() {
        return Data.lsp.lsb("alert.success");
    }

    public static StringBinding getErrorCheckPasswordMessage() {
        return Data.lsp.lsb("alert.error_check_password");
    }

    public static StringBinding getErrorSavePasswordMessage() {
        return Data.lsp.lsb("alert.error_save_password");
    }
}

