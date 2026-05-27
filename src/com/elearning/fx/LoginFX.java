package com.elearning.fx;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.elearning.db.DBConnection;

public class LoginFX {

    private StackPane root;

    public LoginFX(Stage stage) {

        // LEFT PANEL
        VBox left = new VBox(10);
        left.setAlignment(Pos.CENTER);

        Label appName = new Label("E-Learning");
        appName.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00eaff;");

        Label tagline = new Label("Learn. Practice. Grow.");
        tagline.setStyle("-fx-text-fill: rgba(0,234,255,0.7);");

        left.getChildren().addAll(appName, tagline);
        left.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364); -fx-padding: 40;");
        left.setPrefWidth(260);

        // RIGHT PANEL
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER);
        right.setStyle("-fx-background-color: #0d1117;");
        right.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Welcome Back");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleInput(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #00eaff; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10;");
        
        Button signupBtn = new Button("Sign Up");
        signupBtn.setMaxWidth(Double.MAX_VALUE);
        signupBtn.setStyle("-fx-background-color: #00eaff; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10;");
        signupBtn.setOnAction(e -> {
            SignUpFX signup = new SignUpFX(stage);
            stage.getScene().setRoot(signup.getView());
        });
        
        // ✅ LOGIN LOGIC
        loginBtn.setOnAction(e -> {

            String email = emailField.getText().trim();
            String pass = passwordField.getText().trim();

            try {
                Connection conn = DBConnection.getConnection();

                // 🔍 CHECK IF EMAIL EXISTS
                String checkQuery = "SELECT * FROM users WHERE email=?";
                PreparedStatement ps1 = conn.prepareStatement(checkQuery);
                ps1.setString(1, email);

                ResultSet rs1 = ps1.executeQuery();

                if (!rs1.next()) {
                    showAlert("Email not found. Please sign up first.");
                    return;
                }

                // 🔐 CHECK PASSWORD
                String dbPass = rs1.getString("password");

                if (!dbPass.equals(pass)) {
                    showAlert("Incorrect password.");
                    return;
                }

                // ✅ LOGIN SUCCESS
                int userId = rs1.getInt("id");
                String name = rs1.getString("name");

                DashboardFX dashboard = new DashboardFX(stage, name, userId);
                stage.getScene().setRoot(dashboard.getView());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox form = new VBox(15, title, emailField, passwordField, loginBtn, signupBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(300);

        right.getChildren().add(form);

        HBox layout = new HBox(left, right);
        HBox.setHgrow(right, Priority.ALWAYS);

        root = new StackPane(layout);
    }

    private void styleInput(TextField field) {
        field.setStyle("-fx-background-color: #161b22; -fx-text-fill: white; -fx-border-color: #30363d;");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    public Parent getView() {
        return root;
    }
}