module com.cities_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;

    requires io.github.cdimascio.dotenv.java; 

    opens com.cities_project to javafx.fxml, javafx.base;
    exports com.cities_project;
}