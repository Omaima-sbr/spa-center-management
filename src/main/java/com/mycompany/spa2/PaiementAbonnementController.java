package com.mycompany.spa2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.beans.binding.Bindings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;

public class PaiementAbonnementController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private TableView<PaiementAbonnement> paiementsTable;

    @FXML
    private TableColumn<PaiementAbonnement, Integer> idCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, String> nomClientCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, String> servicesCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, Double> montantCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, Double> montantRestantCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, String> modePaiementCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, LocalDateTime> datePaiementCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, LocalDate> periodeCol;
    
    @FXML
    private TableColumn<PaiementAbonnement, String> statutCol;

    // Champs pour ajouter/modifier un paiement
    @FXML 
    private ComboBox<Client> clientComboBox;
    
    @FXML
    private ComboBox<String> modePaiementComboBox;
    
    @FXML
    private DatePicker periodePicker;
    
    
    @FXML
    private TextField montantField;
    
    @FXML
    private TextField clientSearchField;
    
     @FXML
    private Button btnCalculer;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnRevenir;
    
    // Éléments supplémentaires pour afficher les détails des services
    @FXML
    private TableView<ServiceAbonnement> servicesAbonnementTable;
    
    @FXML
    private TableColumn<ServiceAbonnement, String> serviceNomCol;
    
    @FXML
    private TableColumn<ServiceAbonnement, Double> servicePrixCol;
    
    @FXML
    private TableColumn<ServiceAbonnement, String> serviceTypeCol;
    
    @FXML
    private Label totalLabel;
      @FXML
    private TextField recherchePaiements;
 
    @FXML
    private Button btnRechercher;
    
    @FXML
    private Button btnReinitialiser;

    private final ObservableList<PaiementAbonnement> paiements = FXCollections.observableArrayList();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ObservableList<String> modesPaiement = FXCollections.observableArrayList("Espèce", "Carte", "Virement", "Chèque");
    private final ObservableList<String> statuts = FXCollections.observableArrayList("Payé", "En attente", "En retard");
    private final ObservableList<ServiceAbonnement> servicesAbonnement = FXCollections.observableArrayList();
    
    // Classe auxiliaire pour représenter un service dans un abonnement
    public static class ServiceAbonnement {
        private int id;
        private String nom;
        private double prix;
        private String typeAbonnement;
        
        public ServiceAbonnement(int id, String nom, double prix, String typeAbonnement) {
            this.id = id;
            this.nom = nom;
            this.prix = prix;
            this.typeAbonnement = typeAbonnement;
        }
        
        public int getId() { return id; }
        public String getNom() { return nom; }
        public double getPrix() { return prix; }
        public String getTypeAbonnement() { return typeAbonnement; }
    }
    
    
    
    @FXML
public void initialize() {
    servicesAbonnementTable.setPrefHeight(150);
    servicesAbonnementTable.setMinHeight(150);
    
    paiementsTable.setPrefHeight(300);
    paiementsTable.setMinHeight(300);
    // Configuration des colonnes de la table des paiements
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
     servicesAbonnementTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
     paiementsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    nomClientCol.setCellValueFactory(cellData -> {
        Abonnement abonnement = cellData.getValue().getAbonnement();
        return abonnement != null ? Bindings.createStringBinding(
            () -> abonnement.getClient().getNom() + " " + abonnement.getClient().getPrenom()
        ) : null;
    });

    servicesCol.setCellValueFactory(cellData -> {
        Abonnement abonnement = cellData.getValue().getAbonnement();
        return abonnement != null ? Bindings.createStringBinding(
            () -> abonnement.getService().getNom()
        ) : null;
    });

    montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
    montantRestantCol.setCellValueFactory(new PropertyValueFactory<>("montantRestant"));
    modePaiementCol.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
    datePaiementCol.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
    periodeCol.setCellValueFactory(new PropertyValueFactory<>("periode"));
    statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

    // Configuration des colonnes de la table des services d'abonnement
    serviceNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
    servicePrixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
    serviceTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeAbonnement"));

    // Initialisation des ComboBox
    modePaiementComboBox.setItems(modesPaiement);
  

    // Configuration de la recherche dans le ComboBox client
    if (clientSearchField != null && clientComboBox != null) {
        FilteredList<Client> filteredClients = new FilteredList<>(clients, p -> true);

        clientSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredClients.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return client.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       client.getPrenom().toLowerCase().contains(lowerCaseFilter);
            });

            clientComboBox.setItems(filteredClients);
            if (!clientComboBox.isShowing() && !newValue.isEmpty()) {
                clientComboBox.show();
            }
        });

        clientComboBox.setItems(clients);
    }

    // Gestion de la sélection d'un paiement
    gererSelectionPaiement();

    // Action lors de la sélection d'un client
    clientComboBox.setOnAction(event -> {
        Client client = clientComboBox.getValue();
        if (client != null) {
            chargerServicesAbonnement(client.getId());
            calculerMontantTotal();    
        } else {
            servicesAbonnement.clear();
            calculerMontantTotal();    
        }
    });
   
    // Chargement des données
    chargerClients();
    chargerPaiements();
    paiementsTable.setItems(paiements);
}
    

    @FXML
private void reinitialiserRecherche() {
    // Réinitialiser le champ de recherche
    recherchePaiements.clear();
    
    // Charger à nouveau tous les clients
    loadPaiementsData();
}
  

@FXML
private void rechercherPaiements() {
    String searchQuery = recherchePaiements.getText().trim().toLowerCase();

    if (searchQuery.isEmpty()) {
        paiementsTable.setItems(paiements);  // Rétablir la liste complète
        return;
    }

    FilteredList<PaiementAbonnement> filteredList = new FilteredList<>(paiements, paiement -> {
        Abonnement abonnement = paiement.getAbonnement();
        if (abonnement == null) return false;

        Client client = abonnement.getClient();
        Service service = abonnement.getService();

        return (client.getNom().toLowerCase().contains(searchQuery) ||
                client.getPrenom().toLowerCase().contains(searchQuery) ||
                service.getNom().toLowerCase().contains(searchQuery));
    });

    paiementsTable.setItems(filteredList);
}


private void loadPaiementsData() {
    String sql = "SELECT p.*, a.*, c.nom, c.prenom, c.telephone, c.email, " +
                   "s.nom AS service_nom, s.prix, s.description, s.duree, s.id AS service_id " +
                   "FROM paiements_abonnement p " +
                   "JOIN abonnements a ON p.abonnement_id = a.id " +
                   "JOIN clients c ON a.client_id = c.id " +
                   "JOIN services s ON a.service_id = s.id " +
                   "ORDER BY p.date_paiement DESC";

    try (Connection connectDB = new Data().getConnexion();
         Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        ObservableList<PaiementAbonnement> paiementsList = FXCollections.observableArrayList();

        while (rs.next()) {
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

            Abonnement abonnement = new Abonnement(
                rs.getInt("abonnement_id"),
                client,
                service,
                rs.getDate("date_debut").toLocalDate(),
                rs.getDate("date_fin").toLocalDate(),
                rs.getString("type_abonnement")
            );

            PaiementAbonnement paiement = new PaiementAbonnement(
                rs.getInt("id"),
                abonnement,
                rs.getDouble("montant"),
                rs.getDouble("montant_restant"),
                rs.getString("mode_paiement"),
                rs.getTimestamp("date_paiement").toLocalDateTime(),
                rs.getDate("periode").toLocalDate(),
                rs.getString("statut")
            );

            paiementsList.add(paiement);
        }

        paiementsTable.setItems(paiementsList);

    } catch (SQLException e) {
        System.err.println("Erreur lors du chargement des paiements : " + e.getMessage());
        e.printStackTrace();
    }
}


@FXML
private void exporterPDF(ActionEvent event) {
    try {
        // Vérifier si un paiement est sélectionné dans le TableView
        if (paiementsTable.getSelectionModel().getSelectedItem() == null) {
            afficherAlerte("Aucun paiement sélectionné", 
                          "Veuillez sélectionner un paiement à exporter.", 
                          Alert.AlertType.WARNING);
            return;
        }
        
        // Récupérer le paiement sélectionné
        PaiementAbonnement paiement = paiementsTable.getSelectionModel().getSelectedItem();
        
        // Configurer le FileChooser pour sélectionner l'emplacement de sauvegarde
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le reçu de paiement");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Fichiers HTML", "*.html"),
            new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
        );
        
        // Proposer un nom par défaut
        String nomFichierDefaut = "paiement_" + paiement.getId() + "_" + 
                                 paiement.getDatePaiement().format(
                                     DateTimeFormatter.ofPattern("yyyyMMdd")
                                 ) + ".html";
        fileChooser.setInitialFileName(nomFichierDefaut);
        
        // Afficher la boîte de dialogue et récupérer le fichier choisi
        File fichierExport = fileChooser.showSaveDialog(
            ((Node) event.getSource()).getScene().getWindow()
        );
        
        // Si l'utilisateur annule, sortir de la méthode
        if (fichierExport == null) {
            return;
        }
        
        // Formatage des dates
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Créer le contenu HTML pour un document imprimable
        StringBuilder contenu = new StringBuilder();
        
        if (fichierExport.getName().endsWith(".html")) {
            // Créer un document HTML élégant et imprimable
            contenu.append("<!DOCTYPE html>\n");
            contenu.append("<html lang=\"fr\">\n");
            contenu.append("<head>\n");
            contenu.append("    <meta charset=\"UTF-8\">\n");
            contenu.append("    <title>Reçu de paiement #").append(paiement.getId()).append("</title>\n");
            contenu.append("    <style>\n");
            contenu.append("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
            contenu.append("        h1 { text-align: center; color: #333; }\n");
            contenu.append("        h2 { color: #555; margin-top: 30px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }\n");
            contenu.append("        .info { margin-left: 20px; }\n");
            contenu.append("        .footer { margin-top: 50px; font-size: 0.9em; color: #777; text-align: center; }\n");
            contenu.append("        @media print {\n");
            contenu.append("            body { margin: 20px; }\n");
            contenu.append("            .no-print { display: none; }\n");
            contenu.append("            button { display: none; }\n");
            contenu.append("        }\n");
            contenu.append("    </style>\n");
            contenu.append("</head>\n");
            contenu.append("<body>\n");
            
            // Bouton imprimer (apparaît seulement à l'écran)
            contenu.append("    <div class=\"no-print\" style=\"text-align: right;\">\n");
            contenu.append("        <button onclick=\"window.print()\">Imprimer</button>\n");
            contenu.append("    </div>\n");
            
            // Titre
            contenu.append("    <h1>REÇU DE PAIEMENT</h1>\n");
            
            // Informations du paiement
            contenu.append("    <h2>Informations du paiement</h2>\n");
            contenu.append("    <div class=\"info\">\n");
            contenu.append("        <p><strong>ID de paiement:</strong> ").append(paiement.getId()).append("</p>\n");
            contenu.append("        <p><strong>Montant payé:</strong> ").append(paiement.getMontant()).append(" €</p>\n");
            contenu.append("        <p><strong>Montant restant:</strong> ").append(paiement.getMontantRestant()).append(" €</p>\n");
            contenu.append("        <p><strong>Mode de paiement:</strong> ").append(paiement.getModePaiement()).append("</p>\n");
            contenu.append("        <p><strong>Date de paiement:</strong> ").append(paiement.getDatePaiement().format(dateTimeFormatter)).append("</p>\n");
            contenu.append("        <p><strong>Période concernée:</strong> ").append(paiement.getPeriode().format(dateFormatter)).append("</p>\n");
            contenu.append("        <p><strong>Statut:</strong> ").append(paiement.getStatut()).append("</p>\n");
            contenu.append("    </div>\n");
            
            // Informations de l'abonnement
            if (paiement.getAbonnement() != null) {
                Abonnement abonnement = paiement.getAbonnement();
                contenu.append("    <h2>Informations de l'abonnement</h2>\n");
                contenu.append("    <div class=\"info\">\n");
                contenu.append("        <p><strong>ID de l'abonnement:</strong> ").append(abonnement.getId()).append("</p>\n");
                contenu.append("        <p><strong>Type:</strong> ").append(abonnement.getTypeAbonnement()).append("</p>\n");
                contenu.append("    </div>\n");
                
                // Informations du client
                if (abonnement.getClient() != null) {
                    contenu.append("    <h2>Informations du client</h2>\n");
                    contenu.append("    <div class=\"info\">\n");
                    contenu.append("        <p><strong>ID client:</strong> ").append(abonnement.getClient().getId()).append("</p>\n");
                    contenu.append("        <p><strong>Nom:</strong> ").append(abonnement.getClient().getNom()).append("</p>\n");
                    contenu.append("        <p><strong>Prénom:</strong> ").append(abonnement.getClient().getPrenom()).append("</p>\n");
                    contenu.append("    </div>\n");
                }
            }
            
            // Pied de page
            contenu.append("    <div class=\"footer\">\n");
            contenu.append("        <p>Document généré le: ").append(LocalDateTime.now().format(dateTimeFormatter)).append("</p>\n");
            contenu.append("    </div>\n");
            
            contenu.append("</body>\n");
            contenu.append("</html>");
        } else {
            // Format texte simple
            contenu.append("REÇU DE PAIEMENT\n");
            contenu.append("\n");
            contenu.append("INFORMATIONS DU PAIEMENT\n");
            contenu.append("------------------------\n");
            contenu.append("ID de paiement: ").append(paiement.getId()).append("\n");
            contenu.append("Montant payé: ").append(paiement.getMontant()).append(" €\n");
            contenu.append("Montant restant: ").append(paiement.getMontantRestant()).append(" €\n");
            contenu.append("Mode de paiement: ").append(paiement.getModePaiement()).append("\n");
            contenu.append("Date de paiement: ").append(paiement.getDatePaiement().format(dateTimeFormatter)).append("\n");
            contenu.append("Période concernée: ").append(paiement.getPeriode().format(dateFormatter)).append("\n");
            contenu.append("Statut: ").append(paiement.getStatut()).append("\n");
            contenu.append("\n");
            
            // Informations de l'abonnement
            if (paiement.getAbonnement() != null) {
                Abonnement abonnement = paiement.getAbonnement();
                contenu.append("INFORMATIONS DE L'ABONNEMENT\n");
                contenu.append("----------------------------\n");
                contenu.append("ID de l'abonnement: ").append(abonnement.getId()).append("\n");
                contenu.append("Type: ").append(abonnement.getTypeAbonnement()).append("\n");
                contenu.append("\n");
                
                // Informations du client
                if (abonnement.getClient() != null) {
                    contenu.append("INFORMATIONS DU CLIENT\n");
                    contenu.append("----------------------\n");
                    contenu.append("ID client: ").append(abonnement.getClient().getId()).append("\n");
                    contenu.append("Nom: ").append(abonnement.getClient().getNom()).append("\n");
                    contenu.append("Prénom: ").append(abonnement.getClient().getPrenom()).append("\n");
                    contenu.append("\n");
                }
            }
            
            // Pied de page
            contenu.append("Document généré le: ").append(LocalDateTime.now().format(dateTimeFormatter)).append("\n");
        }
        
        // Écrire le contenu dans le fichier
        try (FileWriter writer = new FileWriter(fichierExport)) {
            writer.write(contenu.toString());
        }
        
        // Afficher un message de confirmation
        afficherAlerte("Export réussi", 
                     "Le reçu de paiement a été exporté avec succès." + 
                     (fichierExport.getName().endsWith(".html") ? 
                      " Vous pouvez l'ouvrir dans un navigateur pour l'imprimer." : ""), 
                     Alert.AlertType.INFORMATION);
                      
    } catch (IOException e) {
        e.printStackTrace();
        afficherAlerte("Erreur d'export", 
                     "Une erreur est survenue lors de l'export: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
    }
}

/**
 * Méthode utilitaire pour afficher des alertes
 */
private void afficherAlerte(String titre, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(titre);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}



 private double getMontantTotalAbonnement(int clientId) {
    double total = 0.0;
    
    // Requête SQL pour récupérer tous les abonnements actifs du client et leurs services associés
    String sqlAbonnements = "SELECT a.id, a.type_abonnement, s.prix, s.nom " +
                           "FROM abonnements a " +
                           "JOIN services s ON a.service_id = s.id " +
                           "WHERE a.client_id = ? AND a.date_fin >= CURRENT_DATE";
    
    Data connectNow = new Data();
    
    try (Connection conn = connectNow.getConnexion();
         PreparedStatement stmt = conn.prepareStatement(sqlAbonnements)) {
        
        stmt.setInt(1, clientId);
        ResultSet rs = stmt.executeQuery();
        
        System.out.println("Calcul des montants pour le client #" + clientId);
        
        // Parcourir tous les abonnements actifs du client
        while (rs.next()) {
            int abonnementId = rs.getInt("id");
            double prixBase = rs.getDouble("prix");
            String typeAbonnement = rs.getString("type_abonnement");
            String nomService = rs.getString("nom");
            double montantService = 0.0;
            
            System.out.println("- Abonnement #" + abonnementId + 
                              " - Service: " + nomService + 
                              " - Type: " + typeAbonnement + 
                              " - Prix base: " + prixBase);
            
            // Application des règles de calcul selon le type d'abonnement
            if ("Mensuel".equals(typeAbonnement)) {
                montantService = prixBase;
                System.out.println("  Calcul mensuel: " + montantService);
            } else if ("Annuel".equals(typeAbonnement)) {
                montantService = prixBase * 10; // 10 mois au lieu de 12
                System.out.println("  Calcul annuel (10 mois): " + montantService);
            } else {
                montantService = prixBase;
                System.out.println("  Type non reconnu, utilisation du prix de base: " + montantService);
            }
            
            // Ajouter le montant de ce service au total
            total += montantService;
            System.out.println("  Sous-total cumulé: " + total);
        }
        
        if (total == 0) {
            System.out.println("Aucun abonnement actif trouvé pour le client #" + clientId);
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors du calcul du montant total des abonnements: " + e.getMessage());
        e.printStackTrace();
    }
    
    // Arrondir à 2 décimales
    total = Math.round(total * 100.0) / 100.0;
    System.out.println("Montant total final pour tous les abonnements du client #" + clientId + ": " + total);
    
    return total;
}
    
 
 @FXML
    private void calculerMontantTotal() {
        Client client = clientComboBox.getValue();
        if (client != null) {
            // Récupérer l'ID de l'abonnement du client (selon ton modèle de données)
            double montantTotal = getMontantTotalAbonnement(client.getId());

            // Mise à jour de l'affichage avec le montant total formaté
            totalLabel.setText("Total: " + String.format("%.2f €", montantTotal));
        }
    }
    
    private void gererSelectionPaiement() {
        paiementsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedPaiement) -> {
            if (selectedPaiement != null) {
                // Trouver le client correspondant à l'abonnement
                Client clientAbonnement = selectedPaiement.getAbonnement().getClient();
                clientComboBox.setValue(clientAbonnement);
                
                // Charger les services associés à ce client
                chargerServicesAbonnement(clientAbonnement.getId());
                
                modePaiementComboBox.setValue(selectedPaiement.getModePaiement());
                periodePicker.setValue(selectedPaiement.getPeriode());
               
                montantField.setText(String.valueOf(selectedPaiement.getMontant()));
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
    
  private void chargerPaiements() {
    String query = "SELECT p.*, a.*, c.nom, c.prenom, c.telephone, c.email, " +
                   "s.nom AS service_nom, s.prix, s.description, s.duree, s.id AS service_id " +
                   "FROM paiements_abonnement p " +
                   "JOIN abonnements a ON p.abonnement_id = a.id " +
                   "JOIN clients c ON a.client_id = c.id " +
                   "JOIN services s ON a.service_id = s.id " +
                   "ORDER BY p.date_paiement DESC";

    Data connectNow = new Data();
    Connection connectDB = connectNow.getConnexion();

    try (Statement stmt = connectDB.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        paiements.clear();
        while (rs.next()) {
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

            Abonnement abonnement = new Abonnement(
                rs.getInt("abonnement_id"),
                client,
                service,
                rs.getDate("date_debut").toLocalDate(),
                rs.getDate("date_fin").toLocalDate(),
                rs.getString("type_abonnement")
            );

            PaiementAbonnement paiement = new PaiementAbonnement(
                rs.getInt("id"),
                abonnement,
                rs.getDouble("montant"),
                rs.getDouble("montant_restant"),
                rs.getString("mode_paiement"),
                rs.getTimestamp("date_paiement").toLocalDateTime(),
                rs.getDate("periode").toLocalDate(),
                rs.getString("statut")
            );

            paiements.add(paiement);
        }

        paiementsTable.setItems(paiements);

    } catch (SQLException e) {
        System.out.println("Erreur lors de la récupération des paiements : " + e.getMessage());
        e.printStackTrace(); // Pour debug si jamais
    }
}

    
    private void chargerServicesAbonnement(int clientId) {
        String query = "SELECT a.id, a.type_abonnement, s.id as service_id, s.nom, s.prix " +
               "FROM abonnements a " +
               "JOIN services s ON a.service_id = s.id " +
               "WHERE a.client_id = ? AND a.date_fin >= CURRENT_DATE " +
               "ORDER BY a.id";

        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();

        try (PreparedStatement ps = connectDB.prepareStatement(query)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            servicesAbonnement.clear();
            while (rs.next()) {
                ServiceAbonnement serviceAbo = new ServiceAbonnement(
                    rs.getInt("service_id"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getString("type_abonnement")
                );
                servicesAbonnement.add(serviceAbo);
            }

            servicesAbonnementTable.setItems(servicesAbonnement);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des services d'abonnement : " + e.getMessage());
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
private void ajouterPaiement() {
    Client client = clientComboBox.getValue();
    String modePaiement = modePaiementComboBox.getValue();
    LocalDate periode = periodePicker.getValue();
    
    
    // Vérification des champs
    if (client == null || modePaiement == null || periode == null ) {
        showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
        return;
    }
    
    // Vérifier que le montant est un nombre valide
    double montant;
    try {
        montant = Double.parseDouble(montantField.getText());
        if (montant <= 0) {
            showAlert(Alert.AlertType.WARNING, "Montant invalide", "Le montant doit être supérieur à zéro !");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.WARNING, "Montant invalide", "Veuillez entrer un montant valide !");
        return;
    }
    
    // Vérifier que le client a au moins un abonnement actif
    if (servicesAbonnement.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Aucun abonnement", "Ce client n'a pas d'abonnements actifs !");
        return;
    }
    
    // Trouver l'abonnement principal
    int abonnementId = getAbonnementIdPrincipal(client.getId());
    
    if (abonnementId == -1) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de trouver un abonnement valide pour ce client !");
        return;
    }

    // Calculer le montant total de l'abonnement
    double montantTotal = getMontantTotalAbonnement(client.getId());
    if (montantTotal <= 0) {
        showAlert(Alert.AlertType.WARNING, "Montant total incorrect", "Le montant total de l'abonnement est incorrect !");
        return;
    }

    // Connexion à la base de données
    Data connectNow = new Data();
    Connection conn = connectNow.getConnexion();

    try {
        conn.setAutoCommit(false); // Démarrer une transaction
        
        // Vérification de l'existence d'un paiement pour cet abonnement et cette période
        String checkQuery = "SELECT id, montant, montant_restant FROM paiements_abonnement WHERE abonnement_id = ? AND periode = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, abonnementId);
            checkStmt.setDate(2, java.sql.Date.valueOf(periode));
            
             ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Paiement déjà existant, on met à jour
                int existingId = rs.getInt("id");
                double ancienMontantRestant = rs.getDouble("montant_restant");
                double ancienMontant = rs.getDouble("montant");
                double nouveauMontant = montant+ancienMontant; // Remplacer l'ancien montant par le nouveau (pas additionner)
                double montantRestant = ancienMontantRestant - montant;
                
                // Déterminer le statut en fonction du montant restant
                String nouveauStatut = null; // Utiliser le statut sélectionné par l'utilisateur
                if (montantRestant <= 0) {
                    nouveauStatut = "Payé";
                    montantRestant = 0; // Éviter les valeurs négatives
               } else if (periode.isBefore(LocalDate.now())) {
                 nouveauStatut ="en retard";
               } else {
               nouveauStatut = "en attente";
               }
                
                // Mise à jour du paiement existant
                String updateQuery = "UPDATE paiements_abonnement SET montant = ?, montant_restant = ?, mode_paiement = ?, statut = ?, date_paiement = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setDouble(1, nouveauMontant);
                    updateStmt.setDouble(2, montantRestant);
                    updateStmt.setString(3, modePaiement);
                    updateStmt.setString(4, nouveauStatut);
                    updateStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // Actualiser la date
                    updateStmt.setInt(6, existingId);
                    updateStmt.executeUpdate();
                }
                
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement mis à jour avec succès !");
            } else {
                // Aucun paiement existant, on crée un nouveau paiement
                double montantRestant = montantTotal - montant;
             // Déterminer le statut en fonction du montant restant
                String nouveauStatut = null; // Utiliser le statut sélectionné par l'utilisateur
                   if (montantRestant <= 0) {
                    nouveauStatut = "Payé";
                    montantRestant = 0; // Éviter les valeurs négatives
               } else if (periode.isBefore(LocalDate.now())) {
                 nouveauStatut ="en retard";
               } else {
               nouveauStatut = "en attente";
               }

                String insertQuery = "INSERT INTO paiements_abonnement (abonnement_id, montant, montant_restant, mode_paiement, date_paiement, periode, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setInt(1, abonnementId);
                    insertStmt.setDouble(2, montant);
                    insertStmt.setDouble(3, montantRestant);
                    insertStmt.setString(4, modePaiement);
                    insertStmt.setDate(5, Date.valueOf(LocalDate.now()));
                    insertStmt.setDate(6, java.sql.Date.valueOf(periode));
                    insertStmt.setString(7, nouveauStatut);

                    int rows = insertStmt.executeUpdate();
                    if (rows > 0) {
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            conn.commit();
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement enregistré avec succès !");
                        }
                    }
                }
            }
        }
    } catch (SQLException e) {
        try {
            conn.rollback(); // Annuler la transaction en cas d'erreur
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement du paiement : " + e.getMessage());
    } finally {
        try {
            conn.setAutoCommit(true); // Rétablir l'auto-commit
            conn.close();
        } catch (SQLException closeEx) {
            closeEx.printStackTrace();
        }
    }

    // Actualisation des paiements dans la table
    chargerPaiements();
    
    // Réinitialisation des champs
    clientComboBox.setValue(null);
    modePaiementComboBox.setValue(null);
    periodePicker.setValue(null);
    montantField.setText("0.0");
    servicesAbonnement.clear();
    totalLabel.setText("Total: 0.00 €");
}

   
    private int getAbonnementIdPrincipal(int clientId) {
        String query = "SELECT id FROM abonnements WHERE client_id = ? AND date_fin >= CURRENT_DATE LIMIT 1";
        Data connectNow = new Data();
        Connection connectDB = connectNow.getConnexion();
        
        try (PreparedStatement ps = connectDB.prepareStatement(query)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    @FXML
private void modifierPaiement() {
    PaiementAbonnement paiementSelectionne = paiementsTable.getSelectionModel().getSelectedItem();
    if (paiementSelectionne == null) {
        showAlert(Alert.AlertType.WARNING, "Aucun paiement sélectionné", "Veuillez sélectionner un paiement à modifier !");
        return;
    }
    
    String modePaiement = modePaiementComboBox.getValue();
    LocalDate periode = periodePicker.getValue();
    
    
    // Vérification des champs
    if (modePaiement == null || periode==null  ) {
        showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir le mode de paiement !");
        return;
    }
    
    // Vérifier que le montant est un nombre valide
    double montant;
    try {
        montant = Double.parseDouble(montantField.getText());
        if (montant <= 0) {
            showAlert(Alert.AlertType.WARNING, "Montant invalide", "Le montant doit être supérieur à zéro !");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.WARNING, "Montant invalide", "Veuillez entrer un montant valide !");
        return;
    }
    
    // Récupérer l'ID du client associé à ce paiement
    int clientId = paiementSelectionne.getAbonnement().getClient().getId();
    
    // Récupérer l'ID de l'abonnement associé à ce paiement
    int abonnementId = paiementSelectionne.getAbonnement().getId();
    
    // Calculer le montant total attendu pour cet abonnement
    double montantTotal = getMontantTotalAbonnement(clientId);
    
    // Calculer le montant restant après modification
    // Correct
double ancienMontant = paiementSelectionne.getMontant();
double montantRestant = paiementSelectionne.getMontantRestant() - montant;
   montant+=ancienMontant;
if (montantRestant < 0) {
    montantRestant = 0;
}

    // Déterminer le statut en fonction du montant restant si l'utilisateur n'a pas spécifié
    String nouveauStatut = null;
    if (montantRestant == 0) {
        nouveauStatut = "Payé"; // Si tout est payé, le statut est "Payé"
    } else if (montantRestant == montantTotal) {
        nouveauStatut = "En attente"; // Si rien n'est payé, le statut est "En attente"
    }
    
    // Connexion à la base de données
    Data connectNow = new Data();
    Connection conn = connectNow.getConnexion();
    
    try {
        conn.setAutoCommit(false); // Démarrer une transaction
        
        // Mise à jour du paiement dans la base de données avec le nouveau montant restant
        String query = "UPDATE paiements_abonnement SET montant = ?, montant_restant = ?, mode_paiement = ?, periode = ?, statut = ?, date_paiement = ? WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, montant);
            ps.setDouble(2, montantRestant);
            ps.setString(3, modePaiement);
            ps.setDate(4, java.sql.Date.valueOf(periode));
            ps.setString(5, nouveauStatut);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); // Mettre à jour la date de paiement
            ps.setInt(7, paiementSelectionne.getId());
            
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                conn.commit(); // Valider la transaction
                
                // Mise à jour de l'objet sélectionné
                paiementSelectionne.setMontant(montant);
                paiementSelectionne.setMontantRestant(montantRestant);
                paiementSelectionne.setModePaiement(modePaiement);
                paiementSelectionne.setPeriode(periode);
                paiementSelectionne.setStatut(nouveauStatut);
                paiementSelectionne.setDatePaiement(LocalDateTime.now());
                
                paiementsTable.refresh(); // Rafraîchissement de la table
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement modifié avec succès !");
                
                // Réinitialiser les champs
                clientComboBox.setValue(null);
                modePaiementComboBox.setValue(null);
                periodePicker.setValue(null);
            
                montantField.setText("0.0");
                servicesAbonnement.clear();
                totalLabel.setText("Total: 0.00 €");
            }
        } catch (SQLException e) {
            conn.rollback(); // Annuler la transaction en cas d'erreur
            throw e; // Relancer l'exception pour le bloc catch externe
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du paiement : " + e.getMessage());
    } finally {
        try {
            conn.setAutoCommit(true); // Rétablir l'auto-commit
            conn.close();
        } catch (SQLException closeEx) {
            closeEx.printStackTrace();
        }
    }
    
    // Actualisation des paiements dans la table
    chargerPaiements();
}
    @FXML
    private void supprimerPaiement() {
        PaiementAbonnement paiementSelectionne = paiementsTable.getSelectionModel().getSelectedItem();

        if (paiementSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun paiement sélectionné", "Veuillez sélectionner un paiement à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce paiement ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Connexion à la BDD
                Data connectNow = new Data();
                Connection conn = connectNow.getConnexion();

                try {
                    String sql = "DELETE FROM paiements_abonnement WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, paiementSelectionne.getId());

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        paiements.remove(paiementSelectionne); // Retirer de la liste observable
                        paiementsTable.refresh(); // Rafraîchir l'affichage

                        // Vider les champs
                        clientComboBox.setValue(null);
                        modePaiementComboBox.setValue(null);
                        periodePicker.setValue(null);
                    
                        montantField.setText("0.0");
                        servicesAbonnement.clear();

                        showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "Paiement supprimé avec succès.");
                    }

                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du paiement.");
                }
            }
        });
    }
    
    @FXML
    private void handleAbonnements(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/spa2/Abonnement.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Gestion Abonnements");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}