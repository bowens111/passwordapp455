package passwordapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Layouts
        VBox root = new VBox(10); // Vertical layout with spacing
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // UI Elements
        Label titleLabel = new Label("Password Strength Analyzer and Manager");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter your password");

        Button checkStrengthButton = new Button("Check Strength");
        Label strengthLabel = new Label();

        TextField passwordLengthField = new TextField();
        passwordLengthField.setPromptText("Enter desired password length");

        Button generatePasswordButton = new Button("Generate Password");
        Label generatedPasswordLabel = new Label();

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

        // Add elements to the layout
        root.getChildren().addAll(
            titleLabel,
            passwordField,
            checkStrengthButton,
            strengthLabel,
            passwordLengthField,
            generatePasswordButton,
            generatedPasswordLabel
        );

        // Set the scene
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Password Strength Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
