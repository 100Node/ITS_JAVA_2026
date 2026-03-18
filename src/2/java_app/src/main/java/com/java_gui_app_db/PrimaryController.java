package com.java_gui_app_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import io.github.cdimascio.dotenv.Dotenv;

public class PrimaryController {

    @FXML private TextField searchField;
    @FXML private TableView<CityModel> tableView;
    @FXML private TableColumn<CityModel, String> colCity;
    @FXML private TableColumn<CityModel, String> colAdminName;
    @FXML private TableColumn<CityModel, Integer> colPopulation;
    @FXML private TableColumn<CityModel, Double> colLat;
    @FXML private TableColumn<CityModel, Double> colLng;

    private final Dotenv dotenv = Dotenv.load();

    private final String DB_URL = dotenv.get("DB_URL");
    private final String USER = dotenv.get("DB_USER");
    private final String PASS = dotenv.get("DB_PASS", "");


    private ObservableList<CityModel> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colAdminName.setCellValueFactory(new PropertyValueFactory<>("adminName"));
        colPopulation.setCellValueFactory(new PropertyValueFactory<>("population"));
        colLat.setCellValueFactory(new PropertyValueFactory<>("lat"));
        colLng.setCellValueFactory(new PropertyValueFactory<>("lng"));

        FilteredList<CityModel> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(city -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (city.getCity().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } 
                else if (city.getAdminName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });

        SortedList<CityModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    @FXML
    private void loadDataFromDatabase() {
        masterData.clear(); 
        String query = "SELECT city, admin_name, population, lat, lng FROM ua";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                masterData.add(new CityModel(
                        rs.getString("city"),
                        rs.getDouble("lat"),
                        rs.getDouble("lng"),
                        rs.getString("admin_name"),
                        rs.getInt("population")
                ));
            }
            System.out.println("Data loaded successfully. Number of cities: " + masterData.size());

        } catch (Exception e) {
            System.err.println("An error occurred while connecting to the database or executing the query:");
            e.printStackTrace();
        }
    }
}