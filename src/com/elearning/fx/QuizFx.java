package com.elearning.fx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.elearning.db.DBConnection;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QuizFx {
	Stage stage;
    int userId, courseId, quizNo;
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String[]> options = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();

    int current = 0;
    int score = 0;

    Label questionLabel;
    RadioButton o1, o2, o3, o4;
    ToggleGroup group;

    public QuizFx(int userId, int courseId, int quizNo) {
        this.userId = userId;
        this.courseId = courseId;
        this.quizNo = quizNo;
        stage = new Stage();
        loadQuestions();
        
        if (questions.isEmpty()) {
            Label noQ = new Label("No questions available for this quiz.");
            VBox box = new VBox(noQ);
            stage.setScene(new Scene(box, 300, 200));
            return;
        }

        questionLabel = new Label();
        o1 = new RadioButton();
        o2 = new RadioButton();
        o3 = new RadioButton();
        o4 = new RadioButton();

        group = new ToggleGroup();
        o1.setToggleGroup(group);
        o2.setToggleGroup(group);
        o3.setToggleGroup(group);
        o4.setToggleGroup(group);

        Button next = new Button("Next");

        next.setOnAction(e -> {

            Toggle selectedToggle = group.getSelectedToggle();

            if (selectedToggle != null) {
                RadioButton selected = (RadioButton) selectedToggle;

                if (selected.getText().equals(answers.get(current))) {
                    score++;
                }
            }

            current++;

            if (current < questions.size()) {
                loadQuestion();
            } else {
                showResult();
            }
        });

        VBox root = new VBox(15, questionLabel, o1, o2, o3, o4, next);
        root.setStyle("-fx-padding: 20;");

        loadQuestion();

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Quiz");
       
    }

    private void loadQuestions() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM questions WHERE course_id=? AND quiz_no=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, courseId);
            ps.setInt(2, quizNo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                questions.add(rs.getString("question"));

                options.add(new String[]{
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4")
                });

                answers.add(rs.getString("correct_answer"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestion() {
        questionLabel.setText(questions.get(current));

        String[] opt = options.get(current);

        o1.setText(opt[0]);
        o2.setText(opt[1]);
        o3.setText(opt[2]);
        o4.setText(opt[3]);

        group.selectToggle(null);
        
    }

    
    private void showResult() {

        VBox resultBox = new VBox(20);
        resultBox.setStyle("-fx-padding: 30;");
        
        Label result = new Label("Your Score: " + score + " / " + questions.size());
        result.setStyle("-fx-font-size: 18px; -fx-text-fill: #00eaff;");

        Button close = new Button("Close");

        close.setOnAction(e -> {
            saveResult();
            stage.close();
        });

        resultBox.getChildren().addAll(result, close);

        stage.getScene().setRoot(resultBox);
    }
    private void saveResult() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO results(student_id, course_id, quiz_no, score) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setInt(3, quizNo);
            ps.setInt(4, score);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void show() {
        stage.show();
    }
}