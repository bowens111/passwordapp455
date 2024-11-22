package passwordapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.crypto.SecretKey;

public class App extends Application {
    private PasswordManager passwordManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize PasswordManager with a SecretKey
            SecretKey key = EncryptionUtils.generateKey(); // For demonstration, generate a new key
            passwordManager = new PasswordManager(key);

            // Layouts
            VBox root = new VBox(10); // Vertical layout with spacing
            root.setPadding(new Insets(20));
            root.setStyle("-fx-alignment: center;");

            // UI Elements
            Label titleLabel = new Label("Password Strength Analyzer and Manager");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Password Strength Analyzer Section
            TextField passwordField = new TextField();
            passwordField.setPromptText("Enter your password");

            Button checkStrengthButton = new Button("Check Strength");
            Label strengthLabel = new Label();

            // Password Generator Section
            TextField passwordLengthField = new TextField();
            passwordLengthField.setPromptText("Enter desired password length");

            Button generatePasswordButton = new Button("Generate Password");
            Label generatedPasswordLabel = new Label();

            // Password Manager Section
            TextField accountField = new TextField();
            accountField.setPromptText("Enter account name");

            TextField newPasswordField = new TextField();
            newPasswordField.setPromptText("Enter password to save");

            Button savePasswordButton = new Button("Save Password");
            Label saveStatusLabel = new Label();

            Button retrievePasswordButton = new Button("Retrieve Password");
            Label retrievedPasswordLabel = new Label();

            // Event Handlers
            checkStrengthButton.setOnAction(event -> {
                String password = passwordField.getText();
                String strength = PasswordStrengthChecker.checkStrength(password);
                strengthLabel.setText("Strength: " + strength);
            });

            generatePasswordButton.setOnAction(event -> {
                try {
                    int length = Integer.parseInt(passwordLengthField.getText());
                    String password = PasswordManager.generatePassword(length);
                    generatedPasswordLabel.setText("Generated: " + password);
                } catch (NumberFormatException e) {
                    generatedPasswordLabel.setText("Invalid length: Enter a number");
                } catch (IllegalArgumentException e) {
                    generatedPasswordLabel.setText(e.getMessage());
                }
            });

            savePasswordButton.setOnAction(event -> {
                String account = accountField.getText();
                String password = newPasswordField.getText();
            
                if (account.isEmpty() || password.isEmpty()) {
                    saveStatusLabel.setText("Account and password fields cannot be empty.");
                    return;
                }
            
                // Check password strength before saving
                String strength = PasswordStrengthChecker.checkStrength(password);
                if (!"Very Strong".equalsIgnoreCase(strength)) {
                    saveStatusLabel.setText("Password must be 'Very Strong' to save.");
                    return;
                }
            
                try {
                    passwordManager.addPassword(account, password);
                    saveStatusLabel.setText("Password saved successfully!");
                } catch (Exception e) {
                    saveStatusLabel.setText("Error saving password: " + e.getMessage());
                }
            });
            

            retrievePasswordButton.setOnAction(event -> {
                String account = accountField.getText();

                if (account.isEmpty()) {
                    retrievedPasswordLabel.setText("Account field cannot be empty.");
                    return;
                }

                try {
                    String password = passwordManager.getPassword(account);
                    if (password == null) {
                        retrievedPasswordLabel.setText("No password found for the account.");
                    } else {
                        retrievedPasswordLabel.setText("Password: " + password);
                    }
                } catch (Exception e) {
                    retrievedPasswordLabel.setText("Error retrieving password: " + e.getMessage());
                }
            });

            // Add elements to the layout
            root.getChildren().addAll(
                titleLabel,
                new Label("Password Strength Analyzer"),
                passwordField,
                checkStrengthButton,
                strengthLabel,
                new Label("Password Generator"),
                passwordLengthField,
                generatePasswordButton,
                generatedPasswordLabel,
                new Label("Password Manager"),
                accountField,
                newPasswordField,
                savePasswordButton,
                saveStatusLabel,
                retrievePasswordButton,
                retrievedPasswordLabel
            );

            // Set the scene
            Scene scene = new Scene(root, 500, 600);
            primaryStage.setTitle("Password Strength Analyzer and Manager");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
