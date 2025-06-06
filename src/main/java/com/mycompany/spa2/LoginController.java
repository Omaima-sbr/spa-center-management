package com.mycompany.spa2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException; // Pour IOException
import javafx.scene.Parent; 
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane; 
import javafx.scene.control.Alert; // Pour Alert
import javafx.scene.control.Alert.AlertType; 



public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button submitButton;

    @FXML
    private ImageView backgroundImage;

   @FXML
private AnchorPane rootPane;

    @FXML
    public void initialize() {
      backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
      backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

 
    @FXML
private void handleLogin() {
    String login = loginField.getText();
    String password = passwordField.getText();

    // Vérification des champs vides
    if (login.isEmpty() || password.isEmpty()) {
        showAlert("Erreur de connexion", 
                 "Champs manquants", 
                 "Veuillez saisir votre nom d'utilisateur et mot de passe",
                 Alert.AlertType.WARNING);
        return;
    }

    // Vérification des identifiants
    if ("omaima".equals(login) && "2004".equals(password)) {
        try {
            // Chargement du dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/spa2/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            // Configuration de la nouvelle scène
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage currentStage = (Stage) submitButton.getScene().getWindow();
            
            // Centrer la fenêtre après redimensionnement (optionnel)
            currentStage.centerOnScreen();
            
            // Transition vers le dashboard
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("Dashboard");
            currentStage.show();
            
        } catch (IOException e) {
            showAlert("Erreur technique", 
                     "Problème de chargement", 
                     "Impossible d'ouvrir le dashboard: " + e.getMessage(),
                     Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    } else {
        showAlert("Échec de connexion", 
                 "Identifiants incorrects", 
                 "Le nom d'utilisateur ou le mot de passe est incorrect",
                 Alert.AlertType.ERROR);
        
        // Vidage des champs pour une nouvelle tentative
        passwordField.clear();
        loginField.requestFocus();  // Remet le focus sur le champ login
    }
}

// Méthode utilitaire pour afficher des alertes
private void showAlert(String title, String header, String content, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
}
}
