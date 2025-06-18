module com.example.stanek {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.stanek to javafx.fxml;
    exports com.example.stanek;
}