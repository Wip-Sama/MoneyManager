module org.wip.moneymanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fluentui;
    requires java.desktop;

    opens org.wip.moneymanager to javafx.fxml;
    exports org.wip.moneymanager;
    opens org.wip.moneymanager.components to javafx.fxml;
    exports org.wip.moneymanager.components;
}