module org.wip.moneymanager {
    requires javafx.fxml;

    requires java.sql;
    requires java.net.http;

    requires spring.security.crypto;

    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires eu.hansolo.fx.charts;

    requires org.json;
    requires jdk.jfr;
    requires java.smartcardio;
    requires spring.core;
    requires javafx.controls;
    requires java.desktop;

    opens org.wip.moneymanager.model to javafx.fxml;
    exports org.wip.moneymanager.model;

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
    exports org.wip.moneymanager.Controller;
    opens org.wip.moneymanager.Controller to javafx.fxml;
    exports org.wip.moneymanager.popUp;
    opens org.wip.moneymanager.popUp to javafx.fxml;

}