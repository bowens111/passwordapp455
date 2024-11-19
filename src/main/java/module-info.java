module passwordapp {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens passwordapp to javafx.fxml;
    exports passwordapp;
}
