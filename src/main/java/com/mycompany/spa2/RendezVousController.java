package com.mycompany.spa2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.scene.control.Alert; // Pour Alert
import javafx.scene.control.Alert.AlertType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RendezVousController implements Initializable {

    @FXML
    private ComboBox<Client> clientComboBox;
    
    @FXML
    private Button btnNouveauClient;
    
    @FXML
    private ComboBox<Service> serviceComboBox;
    
    @FXML
    private ComboBox<Employe> employeComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<String> heureComboBox;
    
    @FXML
    private ComboBox<String> minuteComboBox;
    
    @FXML
    private ComboBox<String> statutComboBox;
    
    @FXML
    private Button btnEnregistrer;
    
    @FXML
    private Button btnAnnuler;
    
    @FXML
    private TextField txtRecherche;
    
    @FXML
    private Button btnRechercher;
    
      
    @FXML
    private Button btnRevenir;
    
    @FXML
    private TableView<RendezVousTableModel> tableRendezVous;
    
    @FXML
    private TableColumn<RendezVousTableModel, Integer> colId;
    
    @FXML
    private TableColumn<RendezVousTableModel, String> colClient;
    
    @FXML
    private TableColumn<RendezVousTableModel, String> colService;
    
    @FXML
    private TableColumn<RendezVousTableModel, String> colEmploye;
    
    @FXML
    private TableColumn<RendezVousTableModel, LocalDate> colDate;
    
    @FXML
    private TableColumn<RendezVousTableModel, String> colHeure;
    
    @FXML
    private TableColumn<RendezVousTableModel, String> colStatut;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnRafraichir;
    
    private ObservableList<RendezVousTableModel> listeRendezVousTable = FXCollections.observableArrayList();
    private ObservableList<Client> listeClients = FXCollections.observableArrayList();
    private ObservableList<Service> listeServices = FXCollections.observableArrayList();
    private ObservableList<Employe> listeEmployes = FXCollections.observableArrayList();
    
    private Connection connection;
    private RendezVousTableModel rendezVousSelectionne;
    private boolean modification = false;
    private int idRendezVousModifie = 0;
    
    // Classe pour l'affichage des rendez-vous dans le TableView
    public static class RendezVousTableModel {
        private int id;
        private String nomClient;
        private String nomService;
        private String nomEmploye;
        private LocalDate date;
        private String heure;
        private String statut;
        
        public RendezVousTableModel(int id, String nomClient, String nomService, String nomEmploye, 
                LocalDate date, String heure, String statut) {
            this.id = id;
            this.nomClient = nomClient;
            this.nomService = nomService;
            this.nomEmploye = nomEmploye;
            this.date = date;
            this.heure = heure;
            this.statut = statut;
        }
        
        public int getId() { return id; }
        public String getNomClient() { return nomClient; }
        public String getNomService() { return nomService; }
        public String getNomEmploye() { return nomEmploye; }
        public LocalDate getDate() { return date; }
        public String getHeure() { return heure; }
        public String getStatut() { return statut; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        // Configurer les colonnes du TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        colService.setCellValueFactory(new PropertyValueFactory<>("nomService"));
        colEmploye.setCellValueFactory(new PropertyValueFactory<>("nomEmploye"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Initialiser les ComboBox
        initialiserComboBoxes();
        
        // Charger les données initiales
        chargerDonnees();
        
        // mise a jour des rendezvous deja effectuer 
        mettreAJourRendezVousDepasses();
        creerPaiementsPourRendezVousEffectues();
        // Ajouter un écouteur de sélection pour le TableView
        tableRendezVous.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rendezVousSelectionne = newSelection;
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
            } else {
                rendezVousSelectionne = null;
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            }
        });
        
        // Désactiver initialement les boutons de modification et suppression
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    private void initialiserComboBoxes() {
        // Initialiser les ComboBox d'heure et minute
        ObservableList<String> heures = FXCollections.observableArrayList();
        for (int i = 8; i <= 18; i++) {
            heures.add(String.format("%02d", i));
        }
        heureComboBox.setItems(heures);
        
        ObservableList<String> minutes = FXCollections.observableArrayList("00", "15", "30", "45");
        minuteComboBox.setItems(minutes);
        
        // Initialiser le ComboBox de statut
        ObservableList<String> statuts = FXCollections.observableArrayList(
                "planifié", "confirmé", "en attente", "annulé", "effectué");
        statutComboBox.setItems(statuts);
        
        // Charger les clients, services et employés depuis la base de données
        chargerClients();
        chargerServices();
        chargerEmployes();
    }
    
    public void mettreAJourRendezVousDepasses() {
        String query = "UPDATE rendez_vous SET statut = 'effectué' " +
                      "WHERE date_heure < NOW() AND (statut = 'planifié' OR statut='confirmé') ";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();
        
        try (PreparedStatement pst = connectDB.prepareStatement(query)) {
            int rowsUpdated = pst.executeUpdate();
            System.out.println(rowsUpdated + " rendez-vous marqués comme effectués.");
            
           
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des rendez-vous dépassés: " + e.getMessage());
        }
    }
    
    public void creerPaiementsPourRendezVousEffectues() {
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();
    String selectQuery = 
        "SELECT rv.id AS rendez_vous_id, rv.client_id, s.prix"+
        "FROM rendez_vous rv"+
        "JOIN services s ON rv.service_id = s.id"+
        "LEFT JOIN paiements p ON rv.id = p.rendez_vous_id"+
        "WHERE rv.statut = 'effectué' AND p.id IS NULL";
    

    String insertQuery = 
        "INSERT INTO paiements (client_id, rendez_vous_id, montant, mode_paiement, date_paiement)"+
        "VALUES (?, ?, ?, ?, ?)";
   
    try (
        PreparedStatement selectStmt = connectDB.prepareStatement(selectQuery);
        ResultSet rs = selectStmt.executeQuery();
        PreparedStatement insertStmt = connectDB.prepareStatement(insertQuery)
    ) {
        while (rs.next()) {
            int clientId = rs.getInt("client_id");
            int rdvId = rs.getInt("rendez_vous_id");
            double montant = rs.getDouble("prix");

            insertStmt.setInt(1, clientId);
            insertStmt.setInt(2, rdvId);
            insertStmt.setDouble(3, montant);
            insertStmt.setString(4, "espèce"); // mode de paiement par défaut
            insertStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            insertStmt.executeUpdate();
        }

        System.out.println("Paiements créés pour les rendez-vous effectués.");
    } catch (SQLException e) {
        
    }
}

    
     private void chargerClients() {
        String query = "SELECT * FROM clients";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (Statement stmt = connectDB.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            listeClients.clear();
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                listeClients.add(client);
            }

            clientComboBox.setItems(listeClients);
            
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

        listeServices.clear();
        while (rs.next()) {
            Service service = new Service(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getInt("duree"),
                rs.getDouble("prix")
            );
            listeServices.add(service);
        }

        serviceComboBox.setItems(listeServices);
        
        // Configuration de l'affichage des services dans la ComboBox
        serviceComboBox.setCellFactory(param -> new ListCell<Service>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                setText(empty || service == null ? "" : service.getNom() + " (" + service.getPrix() + " €)");
            }
            });
        
        serviceComboBox.setButtonCell(new ListCell<Service>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                setText(empty || service == null ? "" : service.getNom() + " (" + service.getPrix() + " €)");
            }
        });

    } catch (SQLException e) {
        System.out.println("Erreur lors de la récupération des services : " + e.getMessage());
        // Vous pouvez aussi utiliser showAlert() comme dans votre autre fonction si vous préférez
        showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des services", e.getMessage());
    }
}

 private void chargerEmployes() {
    String query = "SELECT id, nom, prenom, telephone, specialite, cni FROM employes ORDER BY nom, prenom";
    Data connectNow = new Data();
    Connection connectDB = connectNow.getConnexion();

    try (Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        listeEmployes.clear();
        while (rs.next()) {
            Employe employe = new Employe(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("specialite"),
                rs.getString("cni")
            );
            listeEmployes.add(employe);
        }

        employeComboBox.setItems(listeEmployes);
        
        // Configuration de l'affichage des employés dans la ComboBox
        employeComboBox.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe employe, boolean empty) {
                super.updateItem(employe, empty);
                setText(empty || employe == null ? "" : employe.getNom() + " " + employe.getPrenom() + " (" + employe.getSpecialite() + ")");
            }
        });
        
        employeComboBox.setButtonCell(new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe employe, boolean empty) {
                super.updateItem(employe, empty);
                setText(empty || employe == null ? "" : employe.getNom() + " " + employe.getPrenom() + " (" + employe.getSpecialite() + ")");
            }
        });

    } catch (SQLException e) {
        System.out.println("Erreur lors de la récupération des employés : " + e.getMessage());
        showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des employés", e.getMessage());
    }
}
 
 
  private void chargerDonnees() {
    listeRendezVousTable.clear();
    try {
        String query = "SELECT rv.id, c.nom AS client_nom, c.prenom AS client_prenom, " +
                       "s.nom AS service_nom, e.nom AS employe_nom, e.prenom AS employe_prenom, " +
                       "rv.date_heure, rv.statut " +
                       "FROM rendez_vous rv " +
                       "JOIN clients c ON rv.client_id = c.id " +
                       "JOIN services s ON rv.service_id = s.id " +
                       "JOIN employes e ON rv.employe_id = e.id " +
                       "ORDER BY rv.date_heure DESC";

        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();
        PreparedStatement pst = connectDB.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        while (rs.next()) {
            LocalDateTime dateHeure = rs.getTimestamp("date_heure").toLocalDateTime();
            LocalDate date = dateHeure.toLocalDate();
            String heure = dateHeure.format(timeFormatter);

            RendezVousTableModel rv = new RendezVousTableModel(
                    rs.getInt("id"),
                    rs.getString("client_nom") + " " + rs.getString("client_prenom"),
                    rs.getString("service_nom"),
                    rs.getString("employe_nom") + " " + rs.getString("employe_prenom"),
                    date,
                    heure,
                    rs.getString("statut")
            );
            listeRendezVousTable.add(rv);
        }

        tableRendezVous.setItems(listeRendezVousTable);

    } catch (SQLException e) {
        showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des rendez-vous", e.getMessage());
    }
}


@FXML
void handleEnregistrer(ActionEvent event) {
    // Validation des champs
    if (clientComboBox.getValue() == null ||
        serviceComboBox.getValue() == null ||
        employeComboBox.getValue() == null ||
        datePicker.getValue() == null ||
        heureComboBox.getValue() == null ||
        minuteComboBox.getValue() == null ||
        statutComboBox.getValue() == null) {
        
        showAlert(AlertType.WARNING, "Validation", "Champs requis", 
                "Veuillez remplir tous les champs obligatoires.");
        return;
    }

    try {
        // Vérifier et obtenir une connexion valide
        if (connection == null || connection.isClosed()) {
            Data connectNow = new Data();
            connection = connectNow.getConnexion();
        }

        // Créer un objet LocalDateTime
        LocalDate date = datePicker.getValue();
        int heure = Integer.parseInt(heureComboBox.getValue());
        int minute = Integer.parseInt(minuteComboBox.getValue());
        LocalTime time = LocalTime.of(heure, minute);
        LocalDateTime dateHeure = LocalDateTime.of(date, time);

        // Récupérer les valeurs
        Client client = clientComboBox.getValue();
        Service service = serviceComboBox.getValue();
        Employe employe = employeComboBox.getValue();
        String statut = statutComboBox.getValue();

        String query = modification 
            ? "UPDATE rendez_vous SET client_id=?, service_id=?, employe_id=?, date_heure=?, statut=? WHERE id=?" 
            : "INSERT INTO rendez_vous (client_id, service_id, employe_id, date_heure, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Paramètres obligatoires pour les deux requêtes
            pst.setInt(1, client.getId());
            pst.setInt(2, service.getId());
            pst.setInt(3, employe.getId());
            pst.setTimestamp(4, Timestamp.valueOf(dateHeure));
            pst.setString(5, statut);
            
            // Paramètre supplémentaire pour la modification
            if (modification) {
                pst.setInt(6, idRendezVousModifie);
            }

            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                String message = modification ? "modifié" : "ajouté";
                showAlert(AlertType.INFORMATION, "Succès", 
                        "Rendez-vous " + message, 
                        "Le rendez-vous a été " + message + " avec succès.");
                resetForm();
                chargerDonnees();
            } else {
                showAlert(AlertType.WARNING, "Avertissement", 
                        "Aucune modification", 
                        "Aucune donnée n'a été modifiée.");
            }
        }
    } catch (SQLException e) {
        showAlert(AlertType.ERROR, "Erreur SQL", 
                "Erreur de base de données", 
                "Code: " + e.getErrorCode() + "\nMessage: " + e.getMessage());
    } catch (NumberFormatException e) {
        showAlert(AlertType.ERROR, "Format invalide", 
                "Erreur de format", 
                "Veuillez vérifier que l'heure et les minutes sont des nombres valides.");
    } catch (Exception e) {
        showAlert(AlertType.ERROR, "Erreur", 
                "Erreur inattendue", 
                e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}
   
    @FXML
    void handleAnnuler(ActionEvent event) {
        resetForm();
    }
    
    
    
     @FXML
    private void handleNouveauClient(ActionEvent event)  {
          try {
        // Chargez le fichier FXML du dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/spa2/Clients.fxml"));
        Parent root = loader.load();
        
        // Obtenez la scène actuelle
        Scene currentScene = btnNouveauClient.getScene();
        
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
        showAlert(Alert.AlertType.WARNING, "Erreur", "Impossible de charger le dashboard","Veuillez réssayer.");
    }
    }
    
    @FXML
    void handleRechercher(ActionEvent event) {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        
        if (recherche.isEmpty()) {
            tableRendezVous.setItems(listeRendezVousTable);
            return;
        }
        
        // Filtrer les rendez-vous selon le terme de recherche
        ObservableList<RendezVousTableModel> listeFiltre = FXCollections.observableArrayList();
        
        for (RendezVousTableModel rv : listeRendezVousTable) {
              String dateStr = rv.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toLowerCase();
            if (rv.getNomClient().toLowerCase().contains(recherche) ||
                rv.getNomService().toLowerCase().contains(recherche) ||
                rv.getNomEmploye().toLowerCase().contains(recherche) ||
                 dateStr.contains(recherche) ||  
                rv.getStatut().toLowerCase().contains(recherche)) {
                
                listeFiltre.add(rv);
            }
        }
        
        tableRendezVous.setItems(listeFiltre);
    }
    
   @FXML
void handleModifier(ActionEvent event) {
    if (rendezVousSelectionne == null) {
        showAlert(AlertType.WARNING, "Sélection", "Aucun rendez-vous sélectionné", 
                  "Veuillez sélectionner un rendez-vous à modifier.");
        return;
    }

    try {
     String query = "SELECT rv.client_id, rv.service_id, rv.employe_id, rv.date_heure, rv.statut "
             + "FROM rendez_vous rv WHERE rv.id = ?";

        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, rendezVousSelectionne.getId());
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // Activer le mode modification
            modification = true;
            idRendezVousModifie = rendezVousSelectionne.getId();

            // Récupérer les valeurs
            int clientId = rs.getInt("client_id");
            int serviceId = rs.getInt("service_id");
            int employeId = rs.getInt("employe_id");
            LocalDateTime dateHeure = rs.getTimestamp("date_heure").toLocalDateTime();

            String statut = rs.getString("statut");

            // Remplir les ComboBox avec les objets correspondants
            listeClients.stream()
                .filter(c -> c.getId() == clientId)
                .findFirst()
                .ifPresent(clientComboBox::setValue);

            listeServices.stream()
                .filter(s -> s.getId() == serviceId)
                .findFirst()
                .ifPresent(serviceComboBox::setValue);

            listeEmployes.stream()
                .filter(e -> e.getId() == employeId)
                .findFirst()
                .ifPresent(employeComboBox::setValue);

            // Remplir les champs date et heure
            datePicker.setValue(dateHeure.toLocalDate());
            heureComboBox.setValue(String.format("%02d", dateHeure.getHour()));
            minuteComboBox.setValue(String.format("%02d", dateHeure.getMinute()));
            statutComboBox.setValue(statut);

            btnEnregistrer.setText("METTRE À JOUR");
        }

    } catch (SQLException e) {
        showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la récupération des données", e.getMessage());
    }
}


@FXML
void handleSupprimer(ActionEvent event) {
    if (rendezVousSelectionne == null) {
        showAlert(AlertType.WARNING, "Sélection", "Aucun rendez-vous sélectionné", 
                  "Veuillez sélectionner un rendez-vous à supprimer.");
        return;
    }

    Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
    confirmDialog.setTitle("Confirmation de suppression");
    confirmDialog.setHeaderText("Suppression du rendez-vous");
    confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?");

    Optional<ButtonType> result = confirmDialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            String query = "DELETE FROM rendez_vous WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, rendezVousSelectionne.getId());
            pst.executeUpdate();

            showAlert(AlertType.INFORMATION, "Succès", "Rendez-vous supprimé", 
                      "Le rendez-vous a été supprimé avec succès.");

            // Réinitialiser la sélection et recharger la liste
            rendezVousSelectionne = null;
            chargerDonnees();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
        }
    }
}


    @FXML
    void handleRafraichir(ActionEvent event) {
        chargerDonnees();
        resetForm();
    }
    
    private void resetForm() {
        // Réinitialiser les champs
        clientComboBox.setValue(null);
        serviceComboBox.setValue(null);
        employeComboBox.setValue(null);
        datePicker.setValue(null);
        heureComboBox.setValue(null);
        minuteComboBox.setValue(null);
        statutComboBox.setValue(null);
        
        // Réinitialiser le mode modification
        modification = false;
        idRendezVousModifie = 0;
        btnEnregistrer.setText("ENREGISTRER");
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
        showAlert(Alert.AlertType.WARNING, "Erreur", "Impossible de charger le dashboard","Veuillez réssayer.");
    }
}


}