package com.elearning.fx;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

import com.elearning.db.DBConnection;

public class DashboardFX {

    private BorderPane root;
    private VBox contentArea;
    private int userId;
    private String userName;

    public DashboardFX(Stage stage, String userName, int userId) {

        this.userId = userId;
        this.userName = userName;

        // SIDEBAR
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(200);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #0d1117; -fx-padding: 20;");

        Label title = new Label("E-Learning");
        title.setStyle(
                "-fx-text-fill: #00eaff;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI';"
        );

        Button homeBtn = createButton("Home");
        Button coursesBtn = createButton("Courses");
        Button myCoursesBtn = createButton("My Courses");
        Button quizBtn = createButton("Take Quiz");
        Button resultsBtn = createButton("Results");
        resultsBtn.setOnAction(e -> showResults());
        sidebar.getChildren().addAll(title, homeBtn, coursesBtn, myCoursesBtn, quizBtn, resultsBtn);

        // MAIN AREA
        contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: #0d1117; -fx-padding: 30;");

        homeBtn.setOnAction(e -> showHome());
        coursesBtn.setOnAction(e -> showCourses());
        myCoursesBtn.setOnAction(e -> showMyCourses());
        quizBtn.setOnAction(e -> showQuizCourses());
        
        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        showHome();
    }

    // 🏠 HOME
    private void showHome() {
        contentArea.getChildren().clear();

        Label welcome = new Label("Welcome, " + userName);
        welcome.setStyle(
                "-fx-text-fill: #e6edf3;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, #00eaff, 8, 0.4, 0, 0);"
        );

        contentArea.getChildren().add(welcome);
    }

    // 📚 COURSES
    private void showCourses() {

        contentArea.getChildren().clear();

        Button back = createButton("← Back");
        back.setOnAction(e -> showHome());

        Label title = new Label("Courses");
        title.setStyle(
                "-fx-text-fill: #00eaff;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI';"
        );

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM courses");
            ResultSet rs = ps.executeQuery();

            int col = 0, row = 0;

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseTitle = rs.getString("title");

                VBox card = createCourseCard(courseId, courseTitle);

                grid.add(card, col, row);

                col++;
                if (col == 2) {
                    col = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentArea.getChildren().addAll(back, title, grid);
    }

    // 🎯 COURSE CARD (NEON STYLE)
    private VBox createCourseCard(int courseId, String courseName) {

        Label name = new Label(courseName);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Button enroll = new Button("Enroll");
        enroll.setStyle(
                "-fx-background-color: #00eaff;" +
                "-fx-text-fill: black;" +
                "-fx-background-radius: 6;"
        );

        enroll.setOnAction(e -> enroll(courseId));

        VBox card = new VBox(10, name, enroll);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 120);

        card.setStyle(
                "-fx-background-color: #161b22;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #30363d;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 15;"
        );

        // 🔥 Hover Glow
        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: #161b22;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00eaff;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 15;" +
                        "-fx-effect: dropshadow(gaussian, #00eaff, 15, 0.6, 0, 0);"
                )
        );

        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: #161b22;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #30363d;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 15;"
                )
        );

        return card;
    }

    // 🔥 ENROLL LOGIC
    private void enroll(int courseId) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement check = conn.prepareStatement(
                    "SELECT * FROM enrollments WHERE student_id=? AND course_id=?");
            check.setInt(1, userId);
            check.setInt(2, courseId);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                showAlert("Already enrolled");
                return;
            }

            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)");
            insert.setInt(1, userId);
            insert.setInt(2, courseId);
            insert.executeUpdate();

            showAlert("Enrolled successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    // 🔥 NEON BUTTON STYLE
    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setStyle(
                "-fx-background-color: #161b22;" +
                "-fx-text-fill: #e6edf3;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #00eaff;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 12;"
        );

        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: #1f2933;" +
                        "-fx-text-fill: #00eaff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00eaff;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 12;"
                )
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(
                        "-fx-background-color: #161b22;" +
                        "-fx-text-fill: #e6edf3;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #00eaff;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 12;"
                )
        );

        return btn;
    }
    private void unenrollCourse(int courseId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "DELETE FROM enrollments WHERE student_id=? AND course_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            ps.executeUpdate();

            showAlert("Unenrolled successfully!");

            showMyCourses(); // refresh UI

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private VBox createMyCourseCard(int courseId, String courseName) {

        Label name = new Label(courseName);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Button unenroll = new Button("Unenroll");
        unenroll.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");

        unenroll.setOnAction(e -> unenrollCourse(courseId));

        VBox card = new VBox(10, name, unenroll);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 120);

        card.setStyle(
                "-fx-background-color: #161b22;" +
                "-fx-border-color: #30363d;" +
                "-fx-padding: 15;"
        );

        return card;
    }
    private void showMyCourses() {

        contentArea.getChildren().clear();

        Label title = new Label("My Courses");
        title.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 18px;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT c.course_id, c.title FROM courses c " +
                           "JOIN enrollments e ON c.course_id = e.course_id " +
                           "WHERE e.student_id=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            int col = 0, row = 0;

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseName = rs.getString("title");

                VBox card = createMyCourseCard(courseId, courseName);

                grid.add(card, col, row);

                col++;
                if (col == 2) {
                    col = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentArea.getChildren().addAll(title, grid);
    }
    
    private void showQuizCourses() {

        contentArea.getChildren().clear();

        Label title = new Label("Select Course for Quiz");
        title.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 18px;");

        VBox list = new VBox(10);

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT c.course_id, c.title FROM courses c " +
                           "JOIN enrollments e ON c.course_id = e.course_id " +
                           "WHERE e.student_id=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseName = rs.getString("title");

                Button btn = new Button(courseName);
                btn.setMaxWidth(Double.MAX_VALUE);

                btn.setOnAction(e -> showQuizSelection(courseId, courseName));

                list.getChildren().add(btn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentArea.getChildren().addAll(title, list);
    }
    
    private void showQuizSelection(int courseId, String courseName) {

        contentArea.getChildren().clear();

        Label title = new Label(courseName + " - Select Quiz");
        title.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 18px;");

        ComboBox<String> quizSelector = new ComboBox<>();
        quizSelector.getItems().addAll("Quiz 1", "Quiz 2");
        quizSelector.setPromptText("Select Quiz");

        Button startBtn = new Button("Start Quiz");

        startBtn.setOnAction(e -> {

            if (quizSelector.getValue() == null) {
                showAlert("Please select a quiz first!");
                return;
            }

            int quizNo = quizSelector.getValue().equals("Quiz 1") ? 1 : 2;

            startQuiz(courseId, quizNo);
        });

        VBox box = new VBox(15, title, quizSelector, startBtn);
        box.setAlignment(Pos.CENTER);

        contentArea.getChildren().add(box);
    }
    
    private void startQuiz(int courseId, int quizNo) {

        try {
            Connection conn = DBConnection.getConnection();

            String check = "SELECT * FROM results WHERE student_id=? AND course_id=? AND quiz_no=?";
            PreparedStatement ps = conn.prepareStatement(check);
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setInt(3, quizNo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                showAlert("You have already attempted this quiz!");
                return;
            }

            QuizFx quiz = new QuizFx(userId, courseId, quizNo);
            quiz.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showResults() {

        contentArea.getChildren().clear();

        Label title = new Label("Your Results");
        title.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox list = new VBox(15);

        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT c.title, r.quiz_no, r.score " +
                           "FROM results r " +
                           "JOIN courses c ON r.course_id = c.course_id " +
                           "WHERE r.student_id=?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String course = rs.getString("title");
                int quizNo = rs.getInt("quiz_no");
                int score = rs.getInt("score");

                // 🔥 CARD UI
                VBox card = new VBox(5);

                Label c = new Label(course);
                Label q = new Label("Quiz " + quizNo);
                Label s = new Label("Score: " + score);

                c.setStyle("-fx-text-fill: #00eaff; -fx-font-size: 16px; -fx-font-weight: bold;");
                q.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 13px;");
                s.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 13px;");

                card.getChildren().addAll(c, q, s);

                card.setStyle(
                    "-fx-background-color: #161b22;" +
                    "-fx-padding: 12;" +
                    "-fx-border-color: #30363d;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;"
                );

                list.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        contentArea.getChildren().addAll(title, list);
    }
    public Parent getView() {
        return root;
    }
}