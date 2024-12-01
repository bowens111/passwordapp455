package passwordapp;

import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private PasswordManager passwordManager = new PasswordManager();

    @Override
    public void start(Stage primaryStage) {
        // Add some test data for passwordManager to work with
        try {
            passwordManager.addPassword("TestAccount", "VeryStrongPassword123!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Password Strength Analyzer and Manager");
        primaryStage.setScene(createMainAppScene());
        primaryStage.show();
    }

    private Scene createMainAppScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title Label
        Label titleLabel = new Label("Password Strength Analyzer and Manager");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Sections for different functionalities
        VBox strengthAnalyzerSection = createLabeledSection("Password Strength Analyzer", createPasswordStrengthControls());
        VBox passwordGeneratorSection = createLabeledSection("Password Generator", createPasswordGeneratorControls());
        VBox passwordManagerSection = createLabeledSection("Password Manager", createPasswordManagerControls());

        // New button to view hashed passwords
        Button viewHashedPasswordsButton = new Button("View Hashed Passwords");
        viewHashedPasswordsButton.setOnAction(event -> {
            System.out.println("Switching to Hashed Password Viewer...");
            Stage stage = (Stage) viewHashedPasswordsButton.getScene().getWindow();
            stage.setScene(createHashedPasswordsScene(stage));
            stage.sizeToScene(); // Resize the stage to fit the new scene
        });

        VBox viewPasswordsSection = new VBox(5, viewHashedPasswordsButton);

        // Add all sections to the main layout
        root.getChildren().addAll(titleLabel, strengthAnalyzerSection, passwordGeneratorSection, passwordManagerSection, viewPasswordsSection);

        return new Scene(root, 600, 700);
    }

    @SuppressWarnings("unchecked")
    private Scene createHashedPasswordsScene(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title Label
        Label titleLabel = new Label("View Hashed Passwords");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table to display accounts and hashed passwords
        TableView<Map.Entry<String, String>> tableView = new TableView<>();
        TableColumn<Map.Entry<String, String>, String> accountColumn = new TableColumn<>("Account");
        accountColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

        TableColumn<Map.Entry<String, String>, String> hashedPasswordColumn = new TableColumn<>("Hashed Password");
        hashedPasswordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        // Add columns to the table
        tableView.getColumns().addAll(accountColumn, hashedPasswordColumn);

        // Populate the table with data
        tableView.getItems().addAll(passwordManager.getPasswordStoreEntries());

        // Debugging log
        System.out.println("Entries added to table: " + passwordManager.getPasswordStoreEntries().size());

        // Back button to return to the main menu
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> stage.setScene(createMainAppScene()));

        // Add components to the layout
        root.getChildren().addAll(titleLabel, tableView, backButton);

        // Debugging
        System.out.println("Created hashed passwords scene");

        return new Scene(root, 600, 400);
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

        Button verifyPasswordButton = new Button("Verify Password");
        TextField verifyPasswordField = new TextField();
        verifyPasswordField.setPromptText("Enter password to verify");
        Label verifyStatusLabel = new Label();

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

        verifyPasswordButton.setOnAction(event -> {
            String account = accountField.getText();
            String password = verifyPasswordField.getText();

            if (account.isEmpty() || password.isEmpty()) {
                verifyStatusLabel.setText("Account and password cannot be empty.");
                return;
            }

            try {
                boolean isValid = passwordManager.verifyAccountPassword(account, password);
                verifyStatusLabel.setText(isValid ? "Password is valid!" : "Invalid password.");
            } catch (Exception e) {
                // Handle the exception and provide feedback
                verifyStatusLabel.setText("Error verifying password: " + e.getMessage());
            }
        });

        return new VBox(5,
            accountField, newPasswordField, savePasswordButton, saveStatusLabel,
            new Label("Verify Password for an Account:"),
            verifyPasswordField, verifyPasswordButton, verifyStatusLabel
        );
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

    public static void main(String[] args) {
        launch(args);
    }
}
