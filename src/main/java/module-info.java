module passwordapp {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.bouncycastle.provider;
    requires javafx.graphics;

    opens passwordapp to javafx.fxml;
    exports passwordapp;
}
