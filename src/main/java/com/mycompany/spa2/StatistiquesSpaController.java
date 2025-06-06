package com.mycompany.spa2;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StatistiquesSpaController implements Initializable {

    // Connexion à la base de données
    private Connection conn;
    private String url = "jdbc:mysql://localhost:3306/spa_database";
    private String user = "root";
    private String password = "Omixa2004";

    @FXML
    private ComboBox<String> periodComboBox;
    
    @FXML
    private Button btnActualiser;
    
    @FXML
    private Button btnRetour;
    
    // Labels pour les indicateurs
    @FXML
    private Label lblTotalClients;
    
    @FXML
    private Label lblNouveauxClients;
    
    @FXML
    private Label lblTotalRdv;
    
    @FXML
    private Label lblRdvCompletes;
    
    @FXML
    private Label lblRevenuTotal;
    
    @FXML
    private Label lblRevenuMensuel;
    
    @FXML
    private Label lblTotalAbonnements;
    
    @FXML
    private Label lblNouveauxAbonnements;
    
    // Graphiques
    @FXML
    private PieChart servicesChart;
    
    @FXML
    private BarChart<String, Number> rdvChart;
    
    @FXML
    private CategoryAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private BarChart<String, Number> revenusChart;
    
    @FXML
    private CategoryAxis xAxisRevenus;
    
    @FXML
    private NumberAxis yAxisRevenus;
    
    /**
     * Initialisation du contrôleur
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation de la connexion à la base de données
        connectToDatabase();
        
        // Configuration des périodes disponibles
        periodComboBox.setItems(FXCollections.observableArrayList(
                "Dernier mois", 
                "Derniers 3 mois", 
                "Derniers 6 mois",
                "Dernière année", 
                "Tout"
        ));
        periodComboBox.setValue("Dernier mois");
        
        // Chargement initial des statistiques
        actualiserStatistiques(null);
    }
    
    /**
     * Établit la connexion à la base de données
     */
    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            showErrorAlert("Erreur de connexion", "Impossible de se connecter à la base de données", e.getMessage());
        }
    }
    
    /**
     * Actualise toutes les statistiques
     */
    @FXML
    private void actualiserStatistiques(ActionEvent event) {
        // Vérification de la connexion à la base de données
        if (conn == null) {
            connectToDatabase();
            if (conn == null) {
                return;
            }
        }
        
        // Récupération de la période sélectionnée
        String periode = periodComboBox.getValue();
        int mois = 1;
        
        switch (periode) {
            case "Derniers 3 mois":
                mois = 3;
                break;
            case "Derniers 6 mois":
                mois = 6;
                break;
            case "Dernière année":
                mois = 12;
                break;
            case "Tout":
                mois = 100; // Valeur arbitraire pour représenter "tout"
                break;
            default:
                mois = 1;
                break;
        }
        
        // Mise à jour des statistiques
        chargerStatsClients(mois);
        chargerStatsRendezVous(mois);
        chargerStatsRevenus(mois);
        chargerStatsAbonnements(mois);
        chargerGraphiqueServices(mois);
        chargerGraphiqueRendezVous(mois);
        chargerGraphiqueRevenus(mois);
    }
    
    /**
     * Charge les statistiques des clients
     */
    private void chargerStatsClients(int mois) {
        try {
            // Total des clients
            String query = "SELECT COUNT(*) as total FROM clients";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                lblTotalClients.setText(rs.getString("total"));
            }
            
            // Nouveaux clients durant la période
            LocalDate dateDebut = LocalDate.now().minusMonths(mois);
            if (mois < 100) { // Si ce n'est pas "Tout"
                query = "SELECT COUNT(*) as nouveaux FROM clients WHERE id IN (SELECT DISTINCT client_id FROM rendez_vous WHERE date_heure >= ?)";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblNouveauxClients.setText(rs.getString("nouveaux") + " nouveaux");
                }
            } else {
                lblNouveauxClients.setText("Total");
            }
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement des statistiques des clients", e.getMessage());
        }
    }
    
    /**
     * Charge les statistiques des rendez-vous
     */
    private void chargerStatsRendezVous(int mois) {
        try {
            // Préparation de la date de début pour la période
            LocalDate dateDebut = LocalDate.now().minusMonths(mois);
            String query;
            PreparedStatement pst;
            ResultSet rs;
            
            if (mois < 100) { // Si ce n'est pas "Tout"
                // Total des rendez-vous pour la période
                query = "SELECT COUNT(*) as total FROM rendez_vous WHERE date_heure >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblTotalRdv.setText(rs.getString("total"));
                }
                
                // Rendez-vous complétés durant la période
                query = "SELECT COUNT(*) as completes FROM rendez_vous WHERE statut = 'effectué' AND date_heure >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblRdvCompletes.setText(rs.getString("completes") + " complétés");
                }
            } else {
                // Total de tous les rendez-vous
                query = "SELECT COUNT(*) as total FROM rendez_vous";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblTotalRdv.setText(rs.getString("total"));
                }
                
                // Total de tous les rendez-vous complétés
                query = "SELECT COUNT(*) as completes FROM rendez_vous WHERE statut = 'effectué'";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblRdvCompletes.setText(rs.getString("completes") + " complétés");
                }
            }
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement des statistiques des rendez-vous", e.getMessage());
        }
    }
    
    /**
     * Charge les statistiques des revenus
     */
    private void chargerStatsRevenus(int mois) {
        try {
            // Préparation de la date de début pour la période
            LocalDate dateDebut = LocalDate.now().minusMonths(mois);
            String query;
            PreparedStatement pst;
            ResultSet rs;
            
            if (mois < 100) { // Si ce n'est pas "Tout"
                // Revenus totaux pour la période
                query = "SELECT SUM(montant) as total FROM paiements WHERE date_paiement >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next() && rs.getString("total") != null) {
                    lblRevenuTotal.setText(rs.getString("total") + " €");
                } else {
                    lblRevenuTotal.setText("0 €");
                }
                
                // Revenus du mois courant
                LocalDate debutMoisCourant = LocalDate.now().withDayOfMonth(1);
                query = "SELECT SUM(montant) as mensuel FROM paiements WHERE date_paiement >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, debutMoisCourant.toString());
                rs = pst.executeQuery();
                
                if (rs.next() && rs.getString("mensuel") != null) {
                    lblRevenuMensuel.setText(rs.getString("mensuel") + " € ce mois");
                } else {
                    lblRevenuMensuel.setText("0 € ce mois");
                }
                
                // Ajout des revenus des abonnements
                query = "SELECT SUM(montant) as total FROM paiements_abonnement WHERE date_paiement >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next() && rs.getString("total") != null) {
                    double totalAbonnements = Double.parseDouble(rs.getString("total"));
                    double totalActuel = Double.parseDouble(lblRevenuTotal.getText().replace(" €", ""));
                    lblRevenuTotal.setText((totalActuel + totalAbonnements) + " €");
                }
                
                // Revenus d'abonnements du mois courant
                query = "SELECT SUM(montant) as mensuel FROM paiements_abonnement WHERE date_paiement >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, debutMoisCourant.toString());
                rs = pst.executeQuery();
                
                if (rs.next() && rs.getString("mensuel") != null) {
                    double mensuelAbonnements = Double.parseDouble(rs.getString("mensuel"));
                    double mensuelActuel = Double.parseDouble(lblRevenuMensuel.getText().replace(" € ce mois", ""));
                    lblRevenuMensuel.setText((mensuelActuel + mensuelAbonnements) + " € ce mois");
                }
            } else {
                // Tous les revenus
                query = "SELECT SUM(montant) as total FROM paiements";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                
                double totalRdv = 0;
                if (rs.next() && rs.getString("total") != null) {
                    totalRdv = Double.parseDouble(rs.getString("total"));
                }
                
                // Tous les revenus d'abonnements
                query = "SELECT SUM(montant) as total FROM paiements_abonnement";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                
                double totalAbo = 0;
                if (rs.next() && rs.getString("total") != null) {
                    totalAbo = Double.parseDouble(rs.getString("total"));
                }
                
                lblRevenuTotal.setText((totalRdv + totalAbo) + " €");
                lblRevenuMensuel.setText("Total");
            }
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement des statistiques des revenus", e.getMessage());
        }
    }
    
    /**
     * Charge les statistiques des abonnements
     */
    private void chargerStatsAbonnements(int mois) {
        try {
            // Préparation de la date de début pour la période
            LocalDate dateDebut = LocalDate.now().minusMonths(mois);
            String query;
            PreparedStatement pst;
            ResultSet rs;
            
            if (mois < 100) { // Si ce n'est pas "Tout"
                // Total des abonnements actifs
                query = "SELECT COUNT(*) as total FROM abonnements WHERE date_fin >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, LocalDate.now().toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblTotalAbonnements.setText(rs.getString("total"));
                }
                
                // Nouveaux abonnements durant la période
                query = "SELECT COUNT(*) as nouveaux FROM abonnements WHERE date_debut >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblNouveauxAbonnements.setText(rs.getString("nouveaux") + " nouveaux");
                }
            } else {
                // Total de tous les abonnements
                query = "SELECT COUNT(*) as total FROM abonnements";
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblTotalAbonnements.setText(rs.getString("total"));
                }
                
                // Abonnements actifs
                query = "SELECT COUNT(*) as actifs FROM abonnements WHERE date_fin >= ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, LocalDate.now().toString());
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    lblNouveauxAbonnements.setText(rs.getString("actifs") + " actifs");
                }
            }
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement des statistiques des abonnements", e.getMessage());
        }
    }
    
    /**
     * Charge le graphique des services les plus populaires
     */
    private void chargerGraphiqueServices(int mois) {
        try {
            // Préparation de la date de début pour la période
            LocalDate dateDebut = LocalDate.now().minusMonths(mois);
            String query;
            PreparedStatement pst;
            ResultSet rs;
            
            // Vider le graphique existant
            servicesChart.getData().clear();
            
            // Récupération des services les plus populaires
            if (mois < 100) { // Si ce n'est pas "Tout"
                query = "SELECT s.nom, COUNT(r.id) as popularite " +
                       "FROM services s " +
                       "JOIN rendez_vous r ON s.id = r.service_id " +
                       "WHERE r.date_heure >= ? " +
                       "GROUP BY s.id " +
                       "ORDER BY popularite DESC " +
                       "LIMIT 5";
                pst = conn.prepareStatement(query);
                pst.setString(1, dateDebut.toString());
            } else {
                query = "SELECT s.nom, COUNT(r.id) as popularite " +
                       "FROM services s " +
                       "JOIN rendez_vous r ON s.id = r.service_id " +
                       "GROUP BY s.id " +
                       "ORDER BY popularite DESC " +
                       "LIMIT 5";
                pst = conn.prepareStatement(query);
            }
            
            rs = pst.executeQuery();
            
            // Création des données pour le graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while (rs.next()) {
                pieChartData.add(new PieChart.Data(
                    rs.getString("nom") + " (" + rs.getInt("popularite") + ")",
                    rs.getInt("popularite")
                ));
            }
            
            // Ajout des données au graphique
            servicesChart.setData(pieChartData);
            servicesChart.setTitle("Top 5 des services les plus demandés");
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement du graphique des services", e.getMessage());
        }
    }
    
    /**
     * Charge le graphique d'évolution des rendez-vous
     */
    private void chargerGraphiqueRendezVous(int mois) {
    try {
        // Préparation des dates pour la période
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut = dateFin.minusMonths(mois);
        
        // Vider le graphique existant
        rdvChart.getData().clear();
        
        // Créer la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de rendez-vous");
        
        // Format de date pour l'affichage
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        // Récupération des données par mois
        String query;
        PreparedStatement pst;
        
        if (mois < 100) { // Si ce n'est pas "Tout"
            query = "SELECT DATE_FORMAT(date_heure, '%Y-%m') as mois, COUNT(*) as total " +
                   "FROM rendez_vous " +
                   "WHERE date_heure >= ? AND date_heure <= ? " +
                   "GROUP BY DATE_FORMAT(date_heure, '%Y-%m') " +
                   "ORDER BY mois";
            pst = conn.prepareStatement(query);
            pst.setString(1, dateDebut.toString());
            pst.setString(2, dateFin.toString());
        } else {
            // Pour "Tout", on limite aux 12 derniers mois pour éviter un graphique trop chargé
            query =  "SELECT DATE_FORMAT(date_heure, '%Y-%m') as mois, COUNT(*) as total " +
               "FROM rendez_vous " +
               "WHERE statut = 'effectué' " +
               "GROUP BY DATE_FORMAT(date_heure, '%Y-%m') " +
               "ORDER BY mois DESC " +
               "LIMIT 12";
            pst = conn.prepareStatement(query);
        }
        
        // Exécution de la requête
        ResultSet rs = pst.executeQuery();
        
        // Vérifier si des données sont récupérées
        boolean hasData = false;
        
        // Ajouter les données au graphique
        if (mois < 100) {
            while (rs.next()) {
                hasData = true;
                String moisAnnee = rs.getString("mois");
                LocalDate date = LocalDate.parse(moisAnnee + "-01");
                String moisFormate = date.format(formatter);
                int total = rs.getInt("total");
                
                // Débogage
                System.out.println("Mois: " + moisFormate + ", Total: " + total);
                
                series.getData().add(new XYChart.Data<>(moisFormate, total));
            }
        } else {
            // Pour "Tout", on stocke d'abord les données pour les afficher dans l'ordre chronologique
            List<XYChart.Data<String, Number>> tempData = new ArrayList<>();
            while (rs.next()) {
                hasData = true;
                String moisAnnee = rs.getString("mois");
                LocalDate date = LocalDate.parse(moisAnnee + "-01");
                String moisFormate = date.format(formatter);
                int total = rs.getInt("total");
                
                // Débogage
                System.out.println("Mois: " + moisFormate + ", Total: " + total);
                
                tempData.add(new XYChart.Data<>(moisFormate, total));
            }
            
            // Ajouter les données dans l'ordre chronologique
            for (int i = tempData.size() - 1; i >= 0; i--) {
                series.getData().add(tempData.get(i));
            }
        }
        
        if (!hasData) {
            System.out.println("Aucune donnée trouvée pour la période sélectionnée");
        }
        
        // Configuration des axes
        xAxis.setLabel("Mois");
        yAxis.setLabel("Nombre de rendez-vous");
        
        // Ajout de la série au graphique
        rdvChart.getData().add(series);
        
        // Rendre le graphique visible
        rdvChart.setVisible(true);
        
    } catch (SQLException e) {
        e.printStackTrace();
        showErrorAlert("Erreur", "Erreur lors du chargement du graphique des rendez-vous", e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showErrorAlert("Erreur", "Erreur inattendue", e.getMessage());
    }
}
    
    /**
     * Charge le graphique des revenus mensuels
     */
    private void chargerGraphiqueRevenus(int mois) {
        try {
            // Préparation des dates pour la période
            LocalDate dateFin = LocalDate.now();
            LocalDate dateDebut = dateFin.minusMonths(mois);
            
            // Vider le graphique existant
            revenusChart.getData().clear();
            
            // Créer la série de données pour les paiements de rendez-vous
            XYChart.Series<String, Number> seriesRdv = new XYChart.Series<>();
            seriesRdv.setName("Revenus des rendez-vous");
            
            // Créer la série de données pour les paiements d'abonnements
            XYChart.Series<String, Number> seriesAbo = new XYChart.Series<>();
            seriesAbo.setName("Revenus des abonnements");
            
            // Format de date pour l'affichage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            
            // Récupération des données par mois
            if (mois < 100) { // Si ce n'est pas "Tout"
                // Revenus des rendez-vous
                String queryRdv = "SELECT DATE_FORMAT(date_paiement, '%Y-%m') as mois, SUM(montant) as total " +
                                 "FROM paiements " +
                                 "WHERE date_paiement >= ? AND date_paiement <= ? " +
                                 "GROUP BY DATE_FORMAT(date_paiement, '%Y-%m') " +
                                 "ORDER BY mois";
                PreparedStatement pstRdv = conn.prepareStatement(queryRdv);
                pstRdv.setString(1, dateDebut.toString());
                pstRdv.setString(2, dateFin.toString());
                ResultSet rsRdv = pstRdv.executeQuery();
                
                // Ajout des données au graphique
                while (rsRdv.next()) {
                    String moisAnnee = rsRdv.getString("mois");
                    LocalDate date = LocalDate.parse(moisAnnee + "-01");
                    String moisFormate = date.format(formatter);
                    
                    seriesRdv.getData().add(new XYChart.Data<>(moisFormate, rsRdv.getDouble("total")));
                }
                
                // Revenus des abonnements
                String queryAbo = "SELECT DATE_FORMAT(date_paiement, '%Y-%m') as mois, SUM(montant) as total " +
                                 "FROM paiements_abonnement " +
                                 "WHERE date_paiement >= ? AND date_paiement <= ? " +
                                 "GROUP BY DATE_FORMAT(date_paiement, '%Y-%m') " +
                                 "ORDER BY mois";
                PreparedStatement pstAbo = conn.prepareStatement(queryAbo);
                pstAbo.setString(1, dateDebut.toString());
                pstAbo.setString(2, dateFin.toString());
                ResultSet rsAbo = pstAbo.executeQuery();
                
                // Ajout des données au graphique
                while (rsAbo.next()) {
                    String moisAnnee = rsAbo.getString("mois");
                    LocalDate date = LocalDate.parse(moisAnnee + "-01");
                    String moisFormate = date.format(formatter);
                    
                    seriesAbo.getData().add(new XYChart.Data<>(moisFormate, rsAbo.getDouble("total")));
                }
            } else {
                // Pour "Tout", on limite aux 12 derniers mois pour éviter un graphique trop chargé
                String queryRdv = "SELECT DATE_FORMAT(date_paiement, '%Y-%m') as mois, SUM(montant) as total " +
                                 "FROM paiements " +
                                 "GROUP BY DATE_FORMAT(date_paiement, '%Y-%m') " +
                                 "ORDER BY mois DESC " +
                                 "LIMIT 12";
                PreparedStatement pstRdv = conn.prepareStatement(queryRdv);
                ResultSet rsRdv = pstRdv.executeQuery();
                
                // Ajout des données au graphique (inverser l'ordre pour l'affichage chronologique)
                ObservableList<XYChart.Data<String, Number>> tempDataRdv = FXCollections.observableArrayList();
                while (rsRdv.next()) {
                    String moisAnnee = rsRdv.getString("mois");
                    LocalDate date = LocalDate.parse(moisAnnee + "-01");
                    String moisFormate = date.format(formatter);
                    
                    tempDataRdv.add(new XYChart.Data<>(moisFormate, rsRdv.getDouble("total")));
                }
                
                // Inverser l'ordre pour l'affichage chronologique
                for (int i = tempDataRdv.size() - 1; i >= 0; i--) {
                    seriesRdv.getData().add(tempDataRdv.get(i));
                }
                
                // Revenus des abonnements
                String queryAbo = "SELECT DATE_FORMAT(date_paiement, '%Y-%m') as mois, SUM(montant) as total " +
                                 "FROM paiements_abonnement " +
                                 "GROUP BY DATE_FORMAT(date_paiement, '%Y-%m') " +
                                 "ORDER BY mois DESC " +
                                 "LIMIT 12";
                PreparedStatement pstAbo = conn.prepareStatement(queryAbo);
                ResultSet rsAbo = pstAbo.executeQuery();
                
                // Ajout des données au graphique (inverser l'ordre pour l'affichage chronologique)
                ObservableList<XYChart.Data<String, Number>> tempDataAbo = FXCollections.observableArrayList();
                while (rsAbo.next()) {
                    String moisAnnee = rsAbo.getString("mois");
                    LocalDate date = LocalDate.parse(moisAnnee + "-01");
                    String moisFormate = date.format(formatter);
                    
                    tempDataAbo.add(new XYChart.Data<>(moisFormate, rsAbo.getDouble("total")));
                }
                
                // Inverser l'ordre pour l'affichage chronologique
                for (int i = tempDataAbo.size() - 1; i >= 0; i--) {
                    seriesAbo.getData().add(tempDataAbo.get(i));
                }
            }
            
            // Configuration des axes
            xAxisRevenus.setLabel("Mois");
            yAxisRevenus.setLabel("Montant (€)");
            
            // Ajout des séries au graphique
            revenusChart.getData().addAll(seriesRdv, seriesAbo);
            
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors du chargement du graphique des revenus", e.getMessage());
        }
    }
    
    /**
     * Méthode pour retourner au dashboard
     */
    @FXML
    private void retourDashboard(ActionEvent event) {
        try {
            // Fermeture de la connexion à la base de données
            if (conn != null) {
                conn.close();
            }
            
            // Chargement du dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            
            // Récupération de la scène actuelle
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            showErrorAlert("Erreur", "Erreur lors du retour au dashboard", e.getMessage());
        }
    }
    
    /**
     * Affiche une alerte d'erreur
     */
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}