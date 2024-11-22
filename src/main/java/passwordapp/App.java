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
            SecretKey key = EncryptionUtils.generateKey();
            passwordManager = new PasswordManager(key);

            // Main layout
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.TOP_CENTER);

            // Title Label
            Label titleLabel = new Label("Password Strength Analyzer and Manager");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Sections for different functionalities
            VBox strengthAnalyzerSection = createLabeledSection("Password Strength Analyzer",
                createPasswordStrengthControls());
            VBox passwordGeneratorSection = createLabeledSection("Password Generator",
                createPasswordGeneratorControls());
            VBox passwordManagerSection = createLabeledSection("Password Manager",
                createPasswordManagerControls());

            // Help Button
            Button helpButton = new Button("Help");
            helpButton.setOnAction(event -> showHelpWindow(primaryStage));
            helpButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

            // Bottom-right alignment for Help button
            HBox helpButtonContainer = new HBox(helpButton);
            helpButtonContainer.setAlignment(Pos.BOTTOM_RIGHT);

            // Add all sections to the main layout
            root.getChildren().addAll(
                titleLabel,
                strengthAnalyzerSection,
                passwordGeneratorSection,
                passwordManagerSection,
                helpButtonContainer
            );

            // Set up the Scene
            Scene scene = new Scene(root, 500, 600);
            primaryStage.setTitle("Password Strength Analyzer and Manager");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Creates controls for the password strength analyzer section */
    private VBox createPasswordStrengthControls() {
        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter your password");

        Button checkStrengthButton = new Button("Check Strength");
        Label strengthLabel = new Label();

        checkStrengthButton.setOnAction(event -> {
            String password = passwordField.getText();
            String strength = PasswordStrengthChecker.checkStrength(password);
            strengthLabel.setText("Strength: " + strength);
        });

        return new VBox(5, passwordField, checkStrengthButton, strengthLabel);
    }

    /** Creates controls for the password generator section */
    private VBox createPasswordGeneratorControls() {
        TextField passwordLengthField = new TextField();
        passwordLengthField.setPromptText("Enter desired password length");

        Button generatePasswordButton = new Button("Generate Password");
        Label generatedPasswordLabel = new Label();

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

        return new VBox(5, passwordLengthField, generatePasswordButton, generatedPasswordLabel);
    }

    /** Creates controls for the password manager section */
    private VBox createPasswordManagerControls() {
        TextField accountField = new TextField();
        accountField.setPromptText("Enter account name");

        TextField newPasswordField = new TextField();
        newPasswordField.setPromptText("Enter password to save");

        Button savePasswordButton = new Button("Save Password");
        Label saveStatusLabel = new Label();

        Button retrievePasswordButton = new Button("Retrieve Password");
        Label retrievedPasswordLabel = new Label();

        savePasswordButton.setOnAction(event -> {
            String account = accountField.getText();
            String password = newPasswordField.getText();

            if (account.isEmpty() || password.isEmpty()) {
                saveStatusLabel.setText("Account and password cannot be empty.");
                return;
            }

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
                retrievedPasswordLabel.setText(password == null
                    ? "No password found for the account."
                    : "Password: " + password);
            } catch (Exception e) {
                retrievedPasswordLabel.setText("Error retrieving password: " + e.getMessage());
            }
        });

        return new VBox(5, accountField, newPasswordField, savePasswordButton, saveStatusLabel,
            retrievePasswordButton, retrievedPasswordLabel);
    }

    /** Creates a reusable section with a label and content */
    private VBox createLabeledSection(String title, VBox content) {
        Label sectionLabel = new Label(title);
        sectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox section = new VBox(5, sectionLabel, content);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5;");
        return section;
    }

    /** Displays the help window */
    private void showHelpWindow(Stage parentStage) {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.initOwner(parentStage);
        helpStage.setTitle("Help - Instructions");

        VBox helpRoot = new VBox(10);
        helpRoot.setPadding(new Insets(20));
        helpRoot.setAlignment(Pos.CENTER);
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

        Scene helpScene = new Scene(helpRoot, 400, 300);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
