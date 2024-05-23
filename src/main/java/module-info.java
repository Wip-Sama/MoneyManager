module org.wip.moneymanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.wip.moneymanager to javafx.fxml;
    exports org.wip.moneymanager;
}