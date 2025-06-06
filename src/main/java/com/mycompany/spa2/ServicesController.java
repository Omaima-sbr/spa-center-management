package com.mycompany.spa2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException; // Pour IOException
import javafx.scene.Parent; // Pour Parent
import javafx.scene.control.Alert; // Pour Alert
import javafx.scene.control.Alert.AlertType; // Pour AlertType
import java.sql.*;

public class ServicesController {

    @FXML
    private TableView<Service> ServicesTable;

    @FXML
    private TableColumn<Service, Integer> idCol;

    @FXML
    private TableColumn<Service, String> nomCol;

    @FXML
    private TableColumn<Service, String> descriptionCol;

    @FXML
    private TableColumn<Service, Integer> dureeCol;

    @FXML
    private TableColumn<Service, Double> prixCol;

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField dureeField;

    @FXML
    private TextField prixField;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;
        @FXML
    private Button btnRDV;
    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRevenir;
    
       @FXML
    private Button btnRechercher;
    
    @FXML
    private Button btnReinitialiser;
    
     
    
    @FXML
private TextField rechercheServices; 
    
 @FXML   
private void handleRDV(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("RendezVous.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnRDV.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "IImpossible d'ouvrir la page des rendez-vous.");
      
}}


@FXML
private void rechercherServices() {
    String searchQuery = rechercheServices.getText().trim();
    
    if (searchQuery.isEmpty()) {
        loadServicesData();
        return;
    }

    String sql = "SELECT * FROM services WHERE nom LIKE ? OR description LIKE ?";
    
    try (Connection conn = new Data().getConnexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + searchQuery + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);

        try (ResultSet rs = stmt.executeQuery()) {
            ObservableList<Service> searchResults = FXCollections.observableArrayList();

            while (rs.next()) {
                Service service = new Service(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getInt("duree"),
                    rs.getDouble("prix")
                );
                searchResults.add(service);
            }

            ServicesTable.setItems(searchResults);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche des services");
    }
}

/**
 * Charger tous les services sans filtrage.
 */
private void loadServicesData() {
    String sql = "SELECT * FROM services";
    
    try (Connection connectDB = new Data().getConnexion();
         Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        ObservableList<Service> servicesList = FXCollections.observableArrayList();

        while (rs.next()) {
            Service service = new Service(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getInt("duree"),
                rs.getDouble("prix")
            );
            servicesList.add(service);
        }

        ServicesTable.setItems(servicesList);
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des services");
    }
}

/**
 * Réinitialiser la recherche et afficher tous les services.
 */
@FXML
private void reinitialiserRecherche() {
    rechercheServices.clear();
    loadServicesData();
}
     @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        dureeCol.setCellValueFactory(new PropertyValueFactory<>("duree"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));

        chargerDonnees();
        gererSelectionService();

    }

    private final ObservableList<Service> services = FXCollections.observableArrayList();

        private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void chargerDonnees() {
        String query = "SELECT * FROM services";

        Data connectNow = new Data();
        Connection conn = connectNow.getConnexion();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            services.clear();
            while (rs.next()) {
                services.add(new Service(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("duree"),
                        rs.getDouble("prix")
                ));
            }
            ServicesTable.setItems(services);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les services.");
        }
    }

    private void gererSelectionService() {
        ServicesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                nomField.setText(selected.getNom());
                descriptionField.setText(selected.getDescription());
                dureeField.setText(String.valueOf(selected.getDuree()));
                prixField.setText(String.valueOf(selected.getPrix()));
            }
        });
    }
    
    
    @FXML
private void revenirDashboard() {
    try {
        // Chargez le fichier FXML du dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/spa2/dashboard.fxml"));
        Parent root = loader.load();
        
        // Obtenez la scène actuelle
        Scene currentScene = btnRevenir.getScene();
        
        // Créez une nouvelle scène avec le dashboard
        Scene dashboardScene = new Scene(root);
        
        // Obtenez la fenêtre (stage) actuelle
        Stage stage = (Stage) currentScene.getWindow();
        
        // Changez la scène
        stage.setScene(dashboardScene);
        stage.setTitle("Dashboard");
        stage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le dashboard");
    }
}

    @FXML
    private void ajouterService() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String dureeStr = dureeField.getText().trim();
        String prixStr = prixField.getText().trim();

        if (nom.isEmpty() || description.isEmpty() || dureeStr.isEmpty() || prixStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            int duree = Integer.parseInt(dureeStr);
            double prix = Double.parseDouble(prixStr);

            Data connectNow = new Data();
            Connection conn = connectNow.getConnexion();

            String query = "INSERT INTO services (nom, description, duree, prix) VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nom);
                ps.setString(2, description);
                ps.setInt(3, duree);
                ps.setDouble(4, prix);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        Service nouveau = new Service(id, nom, description, duree, prix);
                        services.add(nouveau);
                        ServicesTable.setItems(services);
                        viderChamps();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès !");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du service !");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Durée et prix doivent être des nombres valides !");
        }
    }

    @FXML
    private void modifierService() {
        Service selectionne = ServicesTable.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun service sélectionné", "Veuillez sélectionner un service !");
            return;
        }

        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String dureeStr = dureeField.getText().trim();
        String prixStr = prixField.getText().trim();

        if (nom.isEmpty() || description.isEmpty() || dureeStr.isEmpty() || prixStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            int duree = Integer.parseInt(dureeStr);
            double prix = Double.parseDouble(prixStr);

            Data connectNow = new Data();
            Connection conn = connectNow.getConnexion();

            String query = "UPDATE services SET nom = ?, description = ?, duree = ?, prix = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nom);
                ps.setString(2, description);
                ps.setInt(3, duree);
                ps.setDouble(4, prix);
                ps.setInt(5, selectionne.getId());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    selectionne.setNom(nom);
                    selectionne.setDescription(description);
                    selectionne.setDuree(duree);
                    selectionne.setPrix(prix);
                    ServicesTable.refresh();
                    viderChamps();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Service modifié avec succès !");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification !");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Durée et prix doivent être des nombres valides !");
        }
    }

    @FXML
    private void supprimerService() {
        Service selectionne = ServicesTable.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun service sélectionné", "Veuillez sélectionner un service !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Voulez-vous vraiment supprimer ce service ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Data connectNow = new Data();
                Connection conn = connectNow.getConnexion();

                try {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM services WHERE id = ?");
                    ps.setInt(1, selectionne.getId());
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        services.remove(selectionne);
                        ServicesTable.refresh();
                        viderChamps();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé avec succès !");
                    }
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression !");
                }
            }
        });
    }

    @FXML
    private void viderChamps() {
        nomField.clear();
        descriptionField.clear();
        dureeField.clear();
        prixField.clear();
    }


}