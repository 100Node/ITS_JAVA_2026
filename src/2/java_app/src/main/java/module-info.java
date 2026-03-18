module com.java_gui_app_db {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    requires io.github.cdimascio.dotenv.java; 

    opens com.java_gui_app_db to javafx.fxml, javafx.base;
    exports com.java_gui_app_db;
}