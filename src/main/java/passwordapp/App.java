package passwordapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
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

            // Help Button for Pop-up Instructions
            Button helpButton = new Button("Help");
            helpButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

            // Add all elements to the root layout
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

            // Bottom-right alignment for the Help button
            HBox helpBox = new HBox(helpButton);
            helpBox.setAlignment(Pos.BOTTOM_RIGHT);
            helpBox.setPadding(new Insets(10));
            root.getChildren().add(helpBox);

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
                    saveStatusLabel.setText("Account and password cannot be empty.");
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
                    retrievedPasswordLabel.setText("Account cannot be empty.");
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

            helpButton.setOnAction(event -> showHelpWindow(primaryStage));

            // Set the scene
            Scene scene = new Scene(root, 500, 600);
            primaryStage.setTitle("Password Strength Analyzer and Manager");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to display the pop-up window
    private void showHelpWindow(Stage parentStage) {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.initOwner(parentStage);
        helpStage.setTitle("Help - Instructions");

        VBox helpRoot = new VBox(10);
        helpRoot.setPadding(new Insets(20));
        helpRoot.setStyle("-fx-background-color: #333; -fx-text-fill: white;");

        Label instructions = new Label("Instructions for using the tool:\n\n" +
                "1. Enter a password to check its strength.\n" +
                "2. Specify a length to generate a secure password.\n" +
                "3. Save and retrieve passwords for different accounts.\n" +
                "4. Ensure passwords are 'Very Strong' before saving.\n\n" +
                "For more information, contact support.");
        instructions.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> helpStage.close());

        helpRoot.getChildren().addAll(instructions, closeButton);
        helpRoot.setAlignment(Pos.CENTER);

        Scene helpScene = new Scene(helpRoot, 400, 300);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
