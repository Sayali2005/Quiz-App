import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;

class QuizApp extends JFrame {
    private int i = -1; // Start from -1 so that first call to loadNextQuestion sets i = 0
    private java.util.List<Questions> questionList;
    private JLabel questionLabel;
    private JTextArea questionText;
    private JRadioButton option1, option2, option3, option4;
    private ButtonGroup optionsGroup;
    private JButton nextButton;
    private int score = 0;

    QuizApp() {

        loadJSON();

        Font questionFont = new Font("Futura", Font.BOLD, 32);
        Font optionsFont = new Font("Calibri", Font.PLAIN, 28);

        questionLabel = new JLabel("Question", JLabel.CENTER);
        questionLabel.setFont(questionFont);
        questionLabel.setForeground(new Color(255, 255, 240));

        questionText = new JTextArea();
        questionText.setFont(optionsFont);
        questionText.setForeground(new Color(255, 255, 240));
        questionText.setBackground(new Color(25, 40, 112));
        questionText.setWrapStyleWord(true);
        questionText.setLineWrap(true);
        questionText.setOpaque(false);
        questionText.setEditable(false);
        questionText.setFocusable(false);
        questionText.setHighlighter(null);
        questionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionText.setAlignmentY(Component.CENTER_ALIGNMENT);


        option1 = new JRadioButton();
        option2 = new JRadioButton();
        option3 = new JRadioButton();
        option4 = new JRadioButton();

        option1.setFont(optionsFont);
        option2.setFont(optionsFont);
        option3.setFont(optionsFont);
        option4.setFont(optionsFont);

        option1.setBackground(new Color(100, 180, 250));
        option2.setBackground(new Color(65, 105, 225));
        option3.setBackground(new Color(100, 180, 250));
        option4.setBackground(new Color(65, 105, 225));

        option1.setForeground(Color.WHITE);
        option2.setForeground(Color.WHITE);
        option3.setForeground(Color.WHITE);
        option4.setForeground(Color.WHITE);

        optionsGroup = new ButtonGroup();
        optionsGroup.add(option1);
        optionsGroup.add(option2);
        optionsGroup.add(option3);
        optionsGroup.add(option4);

        // Trigger answer check when user selects any option
        ActionListener optionListener = e -> checkAnswer();
        option1.addActionListener(optionListener);
        option2.addActionListener(optionListener);
        option3.addActionListener(optionListener);
        option4.addActionListener(optionListener);

        nextButton = new JButton("Next");
        nextButton.setFont(optionsFont);
        nextButton.setBackground(new Color(50, 205, 50));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));
        nextButton.setEnabled(false);

        Container c = getContentPane();
        c.setLayout(null);
        c.setBackground(new Color(25, 40, 112));

        questionLabel.setBounds(110, 30, 600, 50);
        questionText.setBounds(110, 80, 600, 80);
        option1.setBounds(150, 160, 500, 50);
        option2.setBounds(150, 220, 500, 50);
        option3.setBounds(150, 280, 500, 50);
        option4.setBounds(150, 340, 500, 50);
        nextButton.setBounds(250, 420, 300, 50);

        c.add(questionLabel);
        c.add(questionText);
        c.add(option1);
        c.add(option2);
        c.add(option3);
        c.add(option4);
        c.add(nextButton);

        nextButton.addActionListener(
                a -> {
                    loadNextQuestion();
                }
        );

        loadNextQuestion();

        setVisible(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Quiz Application");
    }

    void checkAnswer() {
        // Check the answer of the current question
        String selected = null;
        JRadioButton selectedButton = null;
        if (option1.isSelected()) {
            selected = option1.getText();
            selectedButton = option1;
        } else if (option2.isSelected()) {
            selected = option2.getText();
            selectedButton = option2;
        } else if (option3.isSelected()) {
            selected = option3.getText();
            selectedButton = option3;
        } else if (option4.isSelected()) {
            selected = option4.getText();
            selectedButton = option4;
        }

        if (selected == null) return;

        String correctAnswer = questionList.get(i).correctAnswer;

        if (selected.equals(correctAnswer)) {
            score++;
            // Optional: Show correct as green
            selectedButton.setBackground(new Color(0, 200, 100)); // Green
        } else {
            selectedButton.setBackground(Color.RED); // Wrong → red
            // Optional: Show correct option in green
            if (option1.getText().equals(correctAnswer)) option1.setBackground(new Color(0, 200, 100));
            else if (option2.getText().equals(correctAnswer)) option2.setBackground(new Color(0, 200, 100));
            else if (option3.getText().equals(correctAnswer)) option3.setBackground(new Color(0, 200, 100));
            else if (option4.getText().equals(correctAnswer)) option4.setBackground(new Color(0, 200, 100));
        }

        // Disable all options
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);

        // Wait for user to click Next
        nextButton.setEnabled(true);
    }

    void loadNextQuestion() {
        i++;
        if (i < questionList.size()) {
            Questions obj = questionList.get(i);

            questionLabel.setText("Question " + (i + 1));
            questionText.setText(obj.question);
            option1.setText(obj.options[0]);
            option2.setText(obj.options[1]);
            option3.setText(obj.options[2]);
            option4.setText(obj.options[3]);

            // Reset colors
            option1.setBackground(new Color(100, 180, 250));
            option2.setBackground(new Color(65, 105, 225));
            option3.setBackground(new Color(100, 180, 250));
            option4.setBackground(new Color(65, 105, 225));

            optionsGroup.clearSelection(); // Clear selected option

            // Enable options again
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            nextButton.setEnabled(false); // Disable until user answers this one

        } else {
            JOptionPane.showMessageDialog(null, "Congratulations, Quiz Completed!\n Your Score : " + score + "/" + questionList.size());
            System.exit(0);
        }
    }

    void loadJSON() {
        String filename = "quiz.json";
        Path p = Paths.get(filename);

        try {
            if (Files.exists(p)) {
                String result = new String(Files.readAllBytes(p));
                Gson gson = new Gson();
                Questions[] obj1 = gson.fromJson(result, Questions[].class);
                questionList = new ArrayList<>(Arrays.asList(obj1));
            } else {
                JOptionPane.showMessageDialog(null, "JSON File doesn't exists!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizApp();
    }
}

class Questions {
    String question;
    String[] options;
    String correctAnswer;
}
