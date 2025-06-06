package com.mycompany.spa2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.io.IOException; // Pour IOException
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Pour Parent
import javafx.scene.control.Alert; // Pour Alert
import javafx.scene.control.Alert.AlertType; // Pour AlertType


public class EmployesController {

    @FXML
    private TableView<Employe> employesTable;
    
    

    @FXML
    private TableColumn<Employe, String> Nom;

    @FXML
    private TableColumn<Employe, String> Prenom;

    @FXML
    private TableColumn<Employe, String> Tel;

    @FXML
    private TableColumn<Employe, String> Specialite;
    
     @FXML
    private TableColumn<Employe, Integer> Cni;

    @FXML
    private TextField cniField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField specialiteField;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRevenir;
    @FXML
    private Button btnRechercher;
    
    @FXML
    private Button btnReinitialiser;
     @FXML
    private TextField rechercheEmployes;
     
    private final ObservableList<Employe> employes = FXCollections.observableArrayList();
    
    private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
    
    @FXML
    public void initialize() {
        Cni.setCellValueFactory(new PropertyValueFactory<>("cni"));
        Nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        Prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        Tel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        Specialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));

        chargerDonnees();
        gererSelectionEmploye();
    }
    
        @FXML
private void reinitialiserRecherche() {
    // Réinitialiser le champ de recherche
    rechercheEmployes.clear();
    
    // Charger à nouveau tous les clients
    loadEmployesData();
}

private void loadEmployesData() {
    String sql = "SELECT * FROM employes";
    
    // Utilisation de try-with-resources pour gérer la connexion et le Statement
    try (Connection connectDB = new Data().getConnexion(); // Connexion à la base de données
         Statement stmt = connectDB.createStatement();  // Création du Statement
         ResultSet rs = stmt.executeQuery(sql)) {  // Exécution de la requête SQL

        // Créer une liste observable pour stocker les clients récupérés
        ObservableList<Employe> employesList = FXCollections.observableArrayList();

        while (rs.next()) {
            Employe employe = new Employe(
                rs.getInt("id"),   
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("specialite"),
                rs.getString("cni")
            );
            employesList.add(employe);
        }

        // Remplir la TableView avec les résultats
        employesTable.setItems(employesList);
        
    } catch (SQLException e) {
        e.printStackTrace();  // Afficher l'erreur dans la console en cas de problème avec la base de données
    }
}

   @FXML
private void rechercherEmployes() {
    String searchQuery = rechercheEmployes.getText().trim();
    
    // Si le champ de recherche est vide, afficher tous les clients
    if (searchQuery.isEmpty()) {
        loadEmployesData();  // Recharge tous les clients si le champ est vide
        return;
    }

    // Requête SQL pour rechercher les clients par nom, prénom, téléphone ou email
    String sql = "SELECT * FROM employes WHERE cni LIKE ? OR nom LIKE ? OR prenom LIKE ? OR specialite LIKE ?";
    
    try (Connection conn = new Data().getConnexion();  // Utiliser la méthode getConnexion de la classe Data
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Utilisation du paramètre de recherche dans la requête (les % permettent la recherche partielle)
        String searchPattern = "%" + searchQuery + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern);
        stmt.setString(4, searchPattern);

        // Exécution de la requête
        try (ResultSet rs = stmt.executeQuery()) {

            // Vider la TableView avant d'ajouter les résultats de la recherche
            ObservableList<Employe> searchResults = FXCollections.observableArrayList();

            // Remplir la liste avec les résultats de la recherche
            while (rs.next()) {
                  Employe employe = new Employe(
                rs.getInt("id"),  
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("specialite"),
                rs.getString("cni")
            );
                searchResults.add(employe);
            }

            // Remplir la TableView avec les résultats
            employesTable.setItems(searchResults);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();  // Afficher l'erreur dans la console en cas de problème avec la base de données
    }
}

    private void gererSelectionEmploye() {
        employesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedEmploye) -> {
            if (selectedEmploye != null) {
                cniField.setText(String.valueOf(selectedEmploye.getCni()));
                nomField.setText(selectedEmploye.getNom());
                prenomField.setText(selectedEmploye.getPrenom());
                telephoneField.setText(selectedEmploye.getTelephone());
                specialiteField.setText(selectedEmploye.getSpecialite());
            }
        });
    }

    private void chargerDonnees() {
        String query = "SELECT * FROM employes";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            employes.clear();
            while (rs.next()) {
                employes.add(new Employe(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("specialite"),
                        rs.getString("cni")
                ));
            }

            employesTable.setItems(employes);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des données : " + e.getMessage());
        }
    }
    
    

    @FXML
    private void ajouterEmploye() {
        String cni = cniField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String specialite = specialiteField.getText().trim();

        // Vérification des champs
        if (cni.isEmpty() || nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || specialite.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.showAndWait();
            return;
        }

        Data connectNow = new Data();
        Connection conn = connectNow.getConnexion();
        String query = "INSERT INTO employes ( nom, prenom, telephone, specialite ,cni) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, telephone);
            ps.setString(4, specialite);
            ps.setString(5, cni);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Employe nouvelEmploye = new Employe(id, nom, prenom, telephone, specialite,cni);
                    employes.add(nouvelEmploye);
                    employesTable.setItems(employes);

                    cniField.clear();
                    nomField.clear();
                    prenomField.clear();
                    telephoneField.clear();
                    specialiteField.clear();

                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Succès");
                    success.setHeaderText(null);
                    success.setContentText("Employé ajouté avec succès !");
                    success.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText(null);
            error.setContentText("Erreur lors de l'ajout de l'employé !");
            error.showAndWait();
        }
    }

  
@FXML
private void modifierEmploye() {
    Employe employeSelectionne = employesTable.getSelectionModel().getSelectedItem();

    if (employeSelectionne == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucun employé sélectionné");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez sélectionner un employé à modifier !");
        alert.showAndWait();
        return;
    }

    
    String nom = nomField.getText().trim();
    String prenom = prenomField.getText().trim();
    String telephone = telephoneField.getText().trim();
    String specialite = specialiteField.getText().trim();
    String cni = cniField.getText().trim();

    if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || specialite.isEmpty() || cni.isEmpty() ) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champs manquants");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs !");
        alert.showAndWait();
        return;
    }

    Data connectNow = new Data();
    Connection conn = connectNow.getConnexion();

    String query = "UPDATE employes SET  nom = ?, prenom = ?, telephone = ?, specialite = ?, cni = ?  WHERE id = ?";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
   
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, telephone);
        ps.setString(4, specialite);
        ps.setString(5, cni);    
        ps.setInt(6, employeSelectionne.getId()); // identifiant fiable

        int rows = ps.executeUpdate();

        if (rows > 0) {
            // Mettre à jour l'objet sélectionné
         
            employeSelectionne.setNom(nom);
            employeSelectionne.setPrenom(prenom);
            employeSelectionne.setTelephone(telephone);
            employeSelectionne.setSpecialite(specialite);
               employeSelectionne.setCni(cni);

            employesTable.refresh();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("Employé modifié avec succès !");
            success.showAndWait();

            // Réinitialiser les champs
            cniField.clear();
            nomField.clear();
            prenomField.clear();
            telephoneField.clear();
            specialiteField.clear();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Erreur");
        error.setHeaderText(null);
        error.setContentText("Erreur lors de la modification de l'employé !");
        error.showAndWait();
    }
}

    @FXML
    private void supprimerEmploye() {
        Employe employeSelectionne = employesTable.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucun employé sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé à supprimer.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cet employé ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Data connectNow = new Data();
                Connection conn = connectNow.getConnexion();

                try {
                    String sql = "DELETE FROM employes WHERE cni = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, employeSelectionne.getCni());

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        employes.remove(employeSelectionne);
                        employesTable.refresh();

                        cniField.clear();
                        nomField.clear();
                        prenomField.clear();
                        telephoneField.clear();
                        specialiteField.clear();

                        Alert success = new Alert(AlertType.INFORMATION);
                        success.setTitle("Suppression réussie");
                        success.setHeaderText(null);
                        success.setContentText("Employé supprimé avec succès.");
                        success.showAndWait();
                    }
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText(null);
                    error.setContentText("Erreur lors de la suppression de l'employé.");
                    error.showAndWait();
                }
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
}
