package com.mycompany.spa2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.*;
import java.io.IOException; // Pour IOException
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.sql.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Pour Parent
import javafx.scene.control.Alert; // Pour Alert
import javafx.scene.control.Alert.AlertType; // Pour AlertType
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class ClientsController {
      private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TableView<Client> Clientstable;

    @FXML
    private TableColumn<Client, Integer> Id;

    @FXML
    private TableColumn<Client, String> Nom;

    @FXML
    private TableColumn<Client, String> Prenom;

    @FXML
    private TableColumn<Client, String> Tel;

    @FXML
    private TableColumn<Client, String> Email;
    
    @FXML 
   private TextField nomField;
    
    @FXML 
   private TextField prenomField;
    
    @FXML 
   private TextField telephoneField;
    
    @FXML 
   private TextField emailField;
    
    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnRevenir;
    
    @FXML
    private TextField rechercheClients;
 
    @FXML
    private Button btnRechercher;
    
    @FXML
    private Button btnReinitialiser;


    private final ObservableList<Client> clients = FXCollections.observableArrayList();
      @FXML
    private void handleAbonnements(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com/mycompany/spa2/Abonnement.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
private void reinitialiserRecherche() {
    // Réinitialiser le champ de recherche
    rechercheClients.clear();
    
    // Charger à nouveau tous les clients
    loadClientsData();
}
    
    @FXML
    public void initialize() {
        Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        Nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        Prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        Tel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        Email.setCellValueFactory(new PropertyValueFactory<>("email"));

        chargerDonnees();
        gererSelectionClient();
        loadClientsData();

    }
    
    @FXML
private void rechercherClients() {
    String searchQuery = rechercheClients.getText().trim();
    
    // Si le champ de recherche est vide, afficher tous les clients
    if (searchQuery.isEmpty()) {
        loadClientsData();  // Recharge tous les clients si le champ est vide
        return;
    }

    // Requête SQL pour rechercher les clients par nom, prénom, téléphone ou email
    String sql = "SELECT * FROM clients WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? OR email LIKE ?";
    
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
            ObservableList<Client> searchResults = FXCollections.observableArrayList();

            // Remplir la liste avec les résultats de la recherche
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                searchResults.add(client);
            }

            // Remplir la TableView avec les résultats
            Clientstable.setItems(searchResults);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();  // Afficher l'erreur dans la console en cas de problème avec la base de données
    }
}


private void loadClientsData() {
    String sql = "SELECT * FROM clients";
    
    // Utilisation de try-with-resources pour gérer la connexion et le Statement
    try (Connection connectDB = new Data().getConnexion(); // Connexion à la base de données
         Statement stmt = connectDB.createStatement();  // Création du Statement
         ResultSet rs = stmt.executeQuery(sql)) {  // Exécution de la requête SQL

        // Créer une liste observable pour stocker les clients récupérés
        ObservableList<Client> clientsList = FXCollections.observableArrayList();

        while (rs.next()) {
            Client client = new Client(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email")
            );
            clientsList.add(client);
        }

        // Remplir la TableView avec les résultats
        Clientstable.setItems(clientsList);
        
    } catch (SQLException e) {
        e.printStackTrace();  // Afficher l'erreur dans la console en cas de problème avec la base de données
    }
}


    
        private void gererSelectionClient() {
    Clientstable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedClient) -> {
        if (selectedClient != null) {
            nomField.setText(selectedClient.getNom());
            prenomField.setText(selectedClient.getPrenom());
            telephoneField.setText(selectedClient.getTelephone());
            emailField.setText(selectedClient.getEmail());
        }
    });
}

        private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
    private void chargerDonnees() {
    String query = "SELECT * FROM clients";

    Data connectNow = new Data();
    Connection connectDB = connectNow.getConnexion();
    
    

    try (Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        clients.clear();
        while (rs.next()) {
            clients.add(new Client(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email")
            ));
        }

        Clientstable.setItems(clients);
    }
   catch (SQLException e) {
    System.out.println("Erreur lors de la récupération des données : " + e.getMessage());
}

}
    
@FXML
private void revenirDashboard() {
    try {
        // Chargez le fichier FXML du dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/spa2/Dashboard.fxml"));
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
private void ajouterClient() {
    String nom = nomField.getText().trim();
    String prenom = prenomField.getText().trim();
    String telephone = telephoneField.getText().trim();
    String email = emailField.getText().trim();

    // Vérification des champs
    if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champs manquants");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs !");
        alert.showAndWait();
        return;
    }

    // Connexion à la base de données
    Data connectNow = new Data();
    Connection conn = connectNow.getConnexion();

    String query = "INSERT INTO clients (nom, prenom, telephone, email) VALUES (?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, telephone);
        ps.setString(4, email);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                Client nouveauClient = new Client(id, nom, prenom, telephone, email);
                clients.add(nouveauClient); // ajoute à la liste observable
                Clientstable.setItems(clients); // rafraîchit la table

                // Réinitialisation des champs
                nomField.clear();
                prenomField.clear();
                telephoneField.clear();
                emailField.clear();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText(null);
                success.setContentText("Client ajouté avec succès !");
                success.showAndWait();
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Erreur");
        error.setHeaderText(null);
        error.setContentText("Erreur lors de l'ajout du client !");
        error.showAndWait();
    }
}


@FXML
private void modifierClient() {
    Client clientSelectionne = Clientstable.getSelectionModel().getSelectedItem();

    if (clientSelectionne == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucun client sélectionné");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez sélectionner un client à modifier !");
        alert.showAndWait();
        return;
    }

    String nom = nomField.getText().trim();
    String prenom = prenomField.getText().trim();
    String telephone = telephoneField.getText().trim();
    String email = emailField.getText().trim();

    if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champs manquants");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez remplir tous les champs !");
        alert.showAndWait();
        return;
    }

    Data connectNow = new Data();
    Connection conn = connectNow.getConnexion();

    String query = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ? WHERE id = ?";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, telephone);
        ps.setString(4, email);
        ps.setInt(5, clientSelectionne.getId());

        int rows = ps.executeUpdate();

        if (rows > 0) {
            clientSelectionne.setNom(nom);
            clientSelectionne.setPrenom(prenom);
            clientSelectionne.setTelephone(telephone);
            clientSelectionne.setEmail(email);

            Clientstable.refresh(); // pour que le tableau se mette à jour visuellement

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("Client modifié avec succès !");
            success.showAndWait();

            // Réinitialiser les champs
            nomField.clear();
            prenomField.clear();
            telephoneField.clear();
            emailField.clear();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Erreur");
        error.setHeaderText(null);
        error.setContentText("Erreur lors de la modification du client !");
        error.showAndWait();
    }
}

@FXML
private void supprimerClient() {
    Client clientSelectionne = Clientstable.getSelectionModel().getSelectedItem();

    if (clientSelectionne == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aucun client sélectionné");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez sélectionner un client à supprimer.");
        alert.showAndWait();
        return;
    }

    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirmation de suppression");
    confirmation.setHeaderText(null);
    confirmation.setContentText("Voulez-vous vraiment supprimer ce client ?");

    // Attendre la réponse de l'utilisateur
    confirmation.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            // Connexion à la BDD
            Data connectNow = new Data();
            Connection conn = connectNow.getConnexion();

            try {
                String sql = "DELETE FROM clients WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, clientSelectionne.getId());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    clients.remove(clientSelectionne); // Retirer de la liste observable
                    Clientstable.refresh(); // Rafraîchir l'affichage

                    // Vider les champs
                    nomField.clear();
                    prenomField.clear();
                    telephoneField.clear();
                    emailField.clear();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Suppression réussie");
                    success.setHeaderText(null);
                    success.setContentText("Client supprimé avec succès.");
                    success.showAndWait();
                }

                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la suppression du client.");
                error.showAndWait();
            }
        }
    });
}


}

