module org.wip.moneymanager {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires java.sql;
    requires java.desktop;
    requires java.net.http;

    requires spring.core;
    requires spring.security.crypto;

    requires org.json;

    opens org.wip.moneymanager to javafx.fxml;
    exports org.wip.moneymanager;
    opens org.wip.moneymanager.components to javafx.fxml;
    exports org.wip.moneymanager.components;
    opens org.wip.moneymanager.pages to javafx.fxml;
    exports org.wip.moneymanager.pages;
    opens org.wip.moneymanager.model.DBObjects to javafx.fxml;
    exports org.wip.moneymanager.model.DBObjects;
    exports org.wip.moneymanager.model.types;
    opens org.wip.moneymanager.model.types to javafx.fxml;
}