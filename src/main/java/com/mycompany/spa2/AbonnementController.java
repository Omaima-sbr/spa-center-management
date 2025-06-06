package com.mycompany.spa2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;

public class AbonnementController {
     private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private TextField clientSearchField; // Pour le champ de recherche client
    @FXML
    private TextField serviceSearchField; 

    @FXML
    private TableView<Abonnement> AbonnementsTable;

    @FXML
    private TableColumn<Abonnement, Integer> idCol;

    @FXML
    private TableColumn<Abonnement, String> nomClientCol;

    @FXML
    private TableColumn<Abonnement, String> prenomClientCol;
    
    @FXML
    private TableColumn<Abonnement, String> telClientCol;
    
    @FXML
    private TableColumn<Abonnement, String> emailClientCol;

    @FXML
    private TableColumn<Abonnement, String> serviceCol;

    @FXML
    private TableColumn<Abonnement, LocalDate> dateDebutCol;

    @FXML
    private TableColumn<Abonnement, LocalDate> dateFinCol;
    
    @FXML
    private TableColumn<Abonnement, String> typeCol;
    
    // Champs pour ajouter/modifier un abonnement
    @FXML 
    private ComboBox<Client> clientComboBox;
    
    @FXML 
    private ComboBox<Service> serviceComboBox;
    
    @FXML 
    private DatePicker dateDebutPicker;
    
    @FXML 
    private DatePicker dateFinPicker;
    
    @FXML 
    private ComboBox<String> typeAbonnementComboBox;
    
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
    private TextField rechercheAbonnements;

    private final ObservableList<Abonnement> abonnements = FXCollections.observableArrayList();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ObservableList<Service> services = FXCollections.observableArrayList();
    private final ObservableList<String> typesAbonnement = FXCollections.observableArrayList("Mensuel", "Annuel");
    
    @FXML
private void rechercherAbonnements() {
    String searchQuery = rechercheAbonnements.getText().trim();
    
    // Si le champ de recherche est vide, afficher tous les abonnements
    if (searchQuery.isEmpty()) {
        loadAbonnementsData();
        return;
    }

    // Requête SQL pour rechercher les abonnements
    String sql = "SELECT a.*, c.nom, c.prenom, s.nom as service_nom FROM abonnements a " +
                 "JOIN clients c ON a.client_id = c.id " +
                 "JOIN services s ON a.service_id = s.id " +
                 "WHERE c.nom LIKE ? OR c.prenom LIKE ? OR s.nom LIKE ? OR a.type_abonnement LIKE ?";
    
    try (Connection conn = new Data().getConnexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + searchQuery + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern);
        stmt.setString(4, searchPattern);

        try (ResultSet rs = stmt.executeQuery()) {
            ObservableList<Abonnement> searchResults = FXCollections.observableArrayList();

            while (rs.next()) {
                Client client = new Client();
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));

                Service service = new Service();
                service.setNom(rs.getString("service_nom"));

                Abonnement abonnement = new Abonnement(
                    rs.getInt("id"),
                    client,
                    service,
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("type_abonnement")
                );
                searchResults.add(abonnement);
            }

            AbonnementsTable.setItems(searchResults);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche des abonnements");
    }
}

private void loadAbonnementsData() {
    String sql = "SELECT a.*, c.nom, c.prenom, s.nom as service_nom FROM abonnements a " +
                 "JOIN clients c ON a.client_id = c.id " +
                 "JOIN services s ON a.service_id = s.id";
    
    try (Connection connectDB = new Data().getConnexion();
         Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        ObservableList<Abonnement> abonnementsList = FXCollections.observableArrayList();

        while (rs.next()) {
            Client client = new Client();
            client.setNom(rs.getString("nom"));
            client.setPrenom(rs.getString("prenom"));

            Service service = new Service();
            service.setNom(rs.getString("service_nom"));

            Abonnement abonnement = new Abonnement(
                rs.getInt("id"),
                client,
                service,
                rs.getDate("date_debut").toLocalDate(),
                rs.getDate("date_fin").toLocalDate(),
                rs.getString("type_abonnement")
            );
            abonnementsList.add(abonnement);
        }

        AbonnementsTable.setItems(abonnementsList);
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des abonnements");
    }
}

@FXML
private void reinitialiserRecherche() {
    rechercheAbonnements.clear();
    loadAbonnementsData();
}



  @FXML
private void handleClients(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/spa2/Clients.fxml"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);  // Juste remplacer le contenu sans recréer la scène
        stage.setTitle("Gestion Clients");
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}

  @FXML
public void initialize() {
    // Configuration des colonnes de la table
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    
    // Pour afficher les propriétés du client et du service dans les colonnes
    nomClientCol.setCellValueFactory(cellData -> {
        Client client = cellData.getValue().getClient();
        return client != null ? javafx.beans.binding.Bindings.createStringBinding(() -> client.getNom()) : null;
    });
    
    prenomClientCol.setCellValueFactory(cellData -> {
        Client client = cellData.getValue().getClient();
        return client != null ? javafx.beans.binding.Bindings.createStringBinding(() -> client.getPrenom()) : null;
    });
    
    telClientCol.setCellValueFactory(cellData -> {
        Client client = cellData.getValue().getClient();
        return client != null ? javafx.beans.binding.Bindings.createStringBinding(() -> client.getTelephone()) : null;
    });
    
    emailClientCol.setCellValueFactory(cellData -> {
        Client client = cellData.getValue().getClient();
        return client != null ? javafx.beans.binding.Bindings.createStringBinding(() -> client.getEmail()) : null;
    });
    
    serviceCol.setCellValueFactory(cellData -> {
        Service service = cellData.getValue().getService();
        return service != null ? javafx.beans.binding.Bindings.createStringBinding(() -> service.getNom()) : null;
    });
    
    dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
    dateFinCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
    typeCol.setCellValueFactory(new PropertyValueFactory<>("typeAbonnement"));
    
    // Initialisation des ComboBox
    typeAbonnementComboBox.setItems(typesAbonnement);
    
    // Chargement des données
    chargerClients();
    chargerServices();
    chargerAbonnements();
    
    // Configuration de la recherche pour le ComboBox client
    // Supposons que vous avez un TextField clientSearchField dans votre FXML
    if (clientSearchField != null && clientComboBox != null) {
        // Création d'une liste filtrée basée sur la liste de clients
        FilteredList<Client> filteredClients = new FilteredList<>(clients, p -> true);
        
        clientSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredClients.setPredicate(client -> {
                // Si le champ de recherche est vide, afficher tous les clients
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                // Recherche sur le nom et prénom
                if (client.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    client.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            
            clientComboBox.setItems(filteredClients);
            if (!clientComboBox.isShowing() && !newValue.isEmpty()) {
                clientComboBox.show();
            }
        });
        
        // Initialisation avec tous les clients
        clientComboBox.setItems(clients);
    }
    
    // Configuration de la recherche pour le ComboBox service
    // Supposons que vous avez un TextField serviceSearchField dans votre FXML
    if (serviceSearchField != null && serviceComboBox != null) {
        // Création d'une liste filtrée basée sur la liste de services
        FilteredList<Service> filteredServices = new FilteredList<>(services, p -> true);
        
        serviceSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredServices.setPredicate(service -> {
                // Si le champ de recherche est vide, afficher tous les services
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                // Recherche sur le nom du service
                if (service.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            
            serviceComboBox.setItems(filteredServices);
            if (!serviceComboBox.isShowing() && !newValue.isEmpty()) {
                serviceComboBox.show();
            }
        });
        
        // Initialisation avec tous les services
        serviceComboBox.setItems(services);
    }
    
    // Gestion de la sélection d'un abonnement
    gererSelectionAbonnement();
}
    
    private void gererSelectionAbonnement() {
        AbonnementsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedAbonnement) -> {
            if (selectedAbonnement != null) {
                clientComboBox.setValue(selectedAbonnement.getClient());
                serviceComboBox.setValue(selectedAbonnement.getService());
                typeAbonnementComboBox.setValue(selectedAbonnement.getTypeAbonnement());
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
    
    private void chargerClients() {
        String query = "SELECT * FROM clients";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            clients.clear();
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                clients.add(client);
            }

            clientComboBox.setItems(clients);
            
            // Configuration de l'affichage des clients dans la ComboBox
            clientComboBox.setCellFactory(param -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? "" : client.getNom() + " " + client.getPrenom());
                }
            });
            
            clientComboBox.setButtonCell(new ListCell<Client>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? "" : client.getNom() + " " + client.getPrenom());
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des clients : " + e.getMessage());
        }
    }
    
    private void chargerServices() {
        String query = "SELECT * FROM services";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            services.clear();
            while (rs.next()) {
                Service service = new Service(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                      rs.getInt("duree"),    
                    rs.getDouble("prix")
                    
                );
                services.add(service);
            }

            serviceComboBox.setItems(services);
            
            // Configuration de l'affichage des services dans la ComboBox
            serviceComboBox.setCellFactory(param -> new ListCell<Service>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    setText(empty || service == null ? "" : service.getNom());
                }
            });
            
            serviceComboBox.setButtonCell(new ListCell<Service>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    setText(empty || service == null ? "" : service.getNom());
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des services : " + e.getMessage());
        }
    }
    
    private void chargerAbonnements() {
        String query = "SELECT a.*, c.nom, c.prenom, c.telephone, c.email, " +
               "s.nom as service_nom, s.prix, s.description, s.duree " +
               "FROM abonnements a " +
               "JOIN clients c ON a.client_id = c.id " +
               "JOIN services s ON a.service_id = s.id";

        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            abonnements.clear();
            while (rs.next()) {
                // Création d'objets Client et Service
                Client client = new Client(
                    rs.getInt("client_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                
                Service service = new Service(
                    rs.getInt("service_id"),
                    rs.getString("service_nom"),
                    rs.getString("description"),
                    rs.getInt("duree"),
                    rs.getDouble("prix")
                );
                
                // Création de l'objet Abonnement
                Abonnement abonnement = new Abonnement(
                    rs.getInt("id"),
                    client,
                    service,
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("type_abonnement")
                );
                
                abonnements.add(abonnement);
            }

            AbonnementsTable.setItems(abonnements);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des abonnements : " + e.getMessage());
        }
    }
    
    @FXML
    private void revenirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/spa2/Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene currentScene = btnRevenir.getScene();
            Scene dashboardScene = new Scene(root);
            
            Stage stage = (Stage) currentScene.getWindow();
            
            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le dashboard");
        }
    }
    
    @FXML
    private void ajouterAbonnement() {
        Client client = clientComboBox.getValue();
        Service service = serviceComboBox.getValue();
        String typeAbonnement = typeAbonnementComboBox.getValue();

        // Vérification des champs
        if (client == null || service == null || typeAbonnement == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

          // Définir automatiquement la date de début (aujourd'hui)
    LocalDate dateDebut = LocalDate.now();
    LocalDate dateFin;

    // Définir automatiquement la date de fin selon le type
    if (typeAbonnement.equals("Mensuel")) {
        dateFin = dateDebut.plusMonths(1);
    } else if (typeAbonnement.equals("Annuel")) {
        dateFin = dateDebut.plusYears(1);
    } else {
        showAlert(Alert.AlertType.WARNING, "Type d'abonnement invalide", "Le type d'abonnement doit être 'Mensuel' ou 'Annuel'.");
        return;
    }

        // Connexion à la base de données
        Data connectNow = new Data();
        Connection conn = connectNow.getConnexion();

        String query = "INSERT INTO abonnements (client_id, service_id, date_debut, date_fin, type_abonnement) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, client.getId());
            ps.setInt(2, service.getId());
            ps.setDate(3, java.sql.Date.valueOf(dateDebut));
            ps.setDate(4, java.sql.Date.valueOf(dateFin));
            ps.setString(5, typeAbonnement);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Abonnement nouveauAbonnement = new Abonnement(id, client, service, dateDebut, dateFin, typeAbonnement);
                    abonnements.add(nouveauAbonnement);
                    AbonnementsTable.setItems(abonnements);

                    // Réinitialisation des champs
                    clientComboBox.setValue(null);
                    serviceComboBox.setValue(null);
                    typeAbonnementComboBox.setValue(null);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement ajouté avec succès !");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'abonnement !");
        }
    }

    @FXML
    private void modifierAbonnement() {
        Abonnement abonnementSelectionne = AbonnementsTable.getSelectionModel().getSelectedItem();

        if (abonnementSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun abonnement sélectionné", "Veuillez sélectionner un abonnement à modifier !");
            return;
        }

        Client client = clientComboBox.getValue();
        Service service = serviceComboBox.getValue();
        String typeAbonnement = typeAbonnementComboBox.getValue();

        // Vérification des champs
        if (client == null || service == null  || typeAbonnement == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

          // Définir automatiquement la date de début (aujourd'hui)
    LocalDate dateDebut = LocalDate.now();
    LocalDate dateFin;

    // Définir automatiquement la date de fin selon le type
    if (typeAbonnement.equals("Mensuel")) {
        dateFin = dateDebut.plusMonths(1);
    } else if (typeAbonnement.equals("Annuel")) {
        dateFin = dateDebut.plusYears(1);
    } else {
        showAlert(Alert.AlertType.WARNING, "Type d'abonnement invalide", "Le type d'abonnement doit être 'Mensuel' ou 'Annuel'.");
        return;
    }

        // Connexion à la base de données
        Data connectNow = new Data();
        Connection conn = connectNow.getConnexion();

        String query = "UPDATE abonnements SET client_id = ?, service_id = ?, date_debut = ?, date_fin = ?, type_abonnement = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, client.getId());
            ps.setInt(2, service.getId());
            ps.setDate(3, java.sql.Date.valueOf(dateDebut));
            ps.setDate(4, java.sql.Date.valueOf(dateFin));
            ps.setString(5, typeAbonnement);
            ps.setInt(6, abonnementSelectionne.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Mise à jour de l'objet sélectionné
                abonnementSelectionne.setClient(client);
                abonnementSelectionne.setService(service);
                abonnementSelectionne.setDateDebut(dateDebut);
                abonnementSelectionne.setDateFin(dateFin);
                abonnementSelectionne.setTypeAbonnement(typeAbonnement);

                AbonnementsTable.refresh(); // Rafraîchissement de la table

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement modifié avec succès !");

                // Réinitialiser les champs
                clientComboBox.setValue(null);
                serviceComboBox.setValue(null);
                typeAbonnementComboBox.setValue(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de l'abonnement !");
        }
    }

    @FXML
    private void supprimerAbonnement() {
        Abonnement abonnementSelectionne = AbonnementsTable.getSelectionModel().getSelectedItem();

        if (abonnementSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun abonnement sélectionné", "Veuillez sélectionner un abonnement à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cet abonnement ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Connexion à la BDD
                Data connectNow = new Data();
                Connection conn = connectNow.getConnexion();

                try {
                    String sql = "DELETE FROM abonnements WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, abonnementSelectionne.getId());

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        abonnements.remove(abonnementSelectionne); // Retirer de la liste observable
                        AbonnementsTable.refresh(); // Rafraîchir l'affichage

                        // Vider les champs
                        clientComboBox.setValue(null);
                        serviceComboBox.setValue(null);
                        typeAbonnementComboBox.setValue(null);

                        showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Abonnement supprimé avec succès.");
                    }

                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'abonnement.");
                }
            }
        });
    }
}