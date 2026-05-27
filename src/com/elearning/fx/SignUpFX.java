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

public class SignUpFX {

    private VBox root;

    public SignUpFX(Stage stage) {

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");

        Label title = new Label("Create Account");
        title.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 20px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button signupBtn = new Button("Sign Up");

        signupBtn.setOnAction(e -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();

            try {
                Connection conn = DBConnection.getConnection();

                // 🔍 CHECK IF EMAIL EXISTS
                String check = "SELECT * FROM users WHERE email=?";
                PreparedStatement ps1 = conn.prepareStatement(check);
                ps1.setString(1, email);

                ResultSet rs = ps1.executeQuery();

                if (rs.next()) {
                    showAlert("Email already exists!");
                    return;
                }

                // ✅ INSERT USER
                String insert = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'student')";
                PreparedStatement ps2 = conn.prepareStatement(insert);
                ps2.setString(1, name);
                ps2.setString(2, email);
                ps2.setString(3, pass);
                ps2.executeUpdate();

                showAlert("Account created! Please login.");

                // go back to login
                LoginFX login = new LoginFX(stage);
                stage.getScene().setRoot(login.getView());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button back = new Button("← Back to Login");
        back.setOnAction(e -> {
            LoginFX login = new LoginFX(stage);
            stage.getScene().setRoot(login.getView());
        });

        root.getChildren().addAll(title, nameField, emailField, passField, signupBtn, back);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    public Parent getView() {
        return root;
    }
}