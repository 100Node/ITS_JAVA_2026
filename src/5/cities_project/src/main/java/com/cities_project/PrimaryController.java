package com.cities_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.collections.transformation.FilteredList;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import io.github.cdimascio.dotenv.Dotenv;

public class PrimaryController implements Initializable {

    @FXML private TextField tfId, tfName, tfLat, tfLon;
    @FXML private ComboBox<String> cbRegion;
    @FXML private Label lblPhotoPath;
    @FXML private TableView<Site> tvSites;
    @FXML private TableColumn<Site, Integer> colId;
    @FXML private TableColumn<Site, String> colName, colRegion, colPhoto;
    @FXML private TableColumn<Site, Double> colLat, colLon;
    

    private final Dotenv dotenv = Dotenv.load();

    private final String DB_URL = dotenv.get("DB_URL");
    private final String USER = dotenv.get("DB_USER");
    private final String PASS = dotenv.get("DB_PASS", "");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLat.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        colLon.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        colRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
        colPhoto.setCellValueFactory(new PropertyValueFactory<>("image"));

        ObservableList<String> regions = FXCollections.observableArrayList(
                "AR Crimea", "Cherkasy", "Chernihiv", "Chernivtsi", "Dnipropetrovsk", 
                "Donetsk", "Ivano-Frankivsk", "Kharkiv", "Kherson", "Khmelnytskyi", 
                "Kirovohrad", "Kyiv", "Kyiv City", "Luhansk", "Lviv", "Mykolaiv", 
                "Odesa", "Poltava", "Rivne", "Sevastopol City", "Sumy", "Ternopil", 
                "Vinnytsia", "Volyn", "Zakarpattia", "Zaporizhzhia", "Zhytomyr"
        );

        FilteredList<String> filteredRegions = new FilteredList<>(regions, p -> true);

        cbRegion.setEditable(true);

        cbRegion.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            String selected = cbRegion.getSelectionModel().getSelectedItem();
            
            if (selected == null || !selected.equals(cbRegion.getEditor().getText())) {
                filteredRegions.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    return item.toLowerCase().contains(newValue.toLowerCase());
                });
                
                cbRegion.show(); 
            }
        });

        cbRegion.setItems(filteredRegions);

        showSites();
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not connect to the database.");
            return null;
        }
    }

    public void showSites() {
        ObservableList<Site> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM sites";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Site(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("latitude"), rs.getDouble("longitude"),
                        rs.getString("region"), rs.getString("image")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        tvSites.setItems(list);
    }

    @FXML
    private void handleInsert() {
        String query = "INSERT INTO sites (name, latitude, longitude, region, image) VALUES (?, ?, ?, ?, ?)";
        executeQuery(query, "inserted", false);
    }

    @FXML
    private void handleUpdate() {
        String query = "UPDATE sites SET name=?, latitude=?, longitude=?, region=?, image=? WHERE id=?";
        executeQuery(query, "updated", true);
    }

    @FXML
    private void handleDelete() {
        if (tfId.getText().isEmpty()) return;
        String query = "DELETE FROM sites WHERE id=" + tfId.getText();
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(query);
            showSites();
            handleClear();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleClear() {
        tfId.clear();
        tfName.clear();
        tfLat.clear();
        tfLon.clear();
        cbRegion.setValue(null);
        lblPhotoPath.setText("No file selected");
    }

    @FXML
    private void handleRefresh() {
        showSites();
        handleClear();
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            lblPhotoPath.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleMouseAction() {
        Site site = tvSites.getSelectionModel().getSelectedItem();
        if (site != null) {
            tfId.setText(String.valueOf(site.getId()));
            tfName.setText(site.getName());
            tfLat.setText(String.valueOf(site.getLatitude()));
            tfLon.setText(String.valueOf(site.getLongitude()));
            cbRegion.setValue(site.getRegion());
            lblPhotoPath.setText(site.getImage());
        }
    }

    private void executeQuery(String query, String action, boolean isUpdate) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, tfName.getText());
            ps.setDouble(2, Double.parseDouble(tfLat.getText()));
            ps.setDouble(3, Double.parseDouble(tfLon.getText()));
            ps.setString(4, cbRegion.getValue());
            ps.setString(5, lblPhotoPath.getText().equals("No file selected") ? "" : lblPhotoPath.getText());
            
            if (isUpdate) {
                ps.setInt(6, Integer.parseInt(tfId.getText()));
            }
            
            ps.executeUpdate();
            showSites();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Please check the input data (e.g., coordinates must be numbers).");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}