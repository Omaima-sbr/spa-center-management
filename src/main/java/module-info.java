module com.mycompany.spa2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 
    opens com.mycompany.spa2 to javafx.fxml;
    exports com.mycompany.spa2;
}
