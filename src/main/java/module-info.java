module org.wip.moneymanager {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fluentui;
    requires org.bouncycastle.provider;

    requires java.sql;
    requires spring.core;
    requires java.desktop;
    requires spring.security.crypto;

    opens org.wip.moneymanager to javafx.fxml;
    exports org.wip.moneymanager;
    opens org.wip.moneymanager.components to javafx.fxml;
    exports org.wip.moneymanager.components;
    opens org.wip.moneymanager.pages to javafx.fxml;
    exports org.wip.moneymanager.pages;
}