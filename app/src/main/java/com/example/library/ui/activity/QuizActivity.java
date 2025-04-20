//package com.example.library;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.library.model.Quiz;
//import com.example.library.util.FirebaseUtil;
//import com.google.android.material.button.MaterialButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuizActivity extends AppCompatActivity {
//    public static final String EXTRA_BOOK_ID = "book_id";
//
//    private TextView quizTitle;
//    private TextView questionNumber;
//    private TextView questionText;
//    private RadioGroup optionsGroup;
//    private MaterialButton prevButton;
//    private MaterialButton nextButton;
//    private View progressBar;
//
//    private Quiz quiz;
//    private String bookId;
//    private String userId;
//    private int currentQuestionIndex = 0;
//    private List<Integer> userAnswers;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_quiz);
//
//        // Get book ID from intent
//        bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
//        if (bookId == null) {
//            finish();
//            return;
//        }
//
//        // Get current user ID
//        userId = FirebaseUtil.getCurrentUserId();
//
//        // Initialize views
//        initializeViews();
//        setupClickListeners();
//        loadQuiz();
//    }
//
//    private void initializeViews() {
//        quizTitle = findViewById(R.id.quiz_title);
//        questionNumber = findViewById(R.id.question_number);
//        questionText = findViewById(R.id.question_text);
//        optionsGroup = findViewById(R.id.options_group);
//        prevButton = findViewById(R.id.prev_button);
//        nextButton = findViewById(R.id.next_button);
//        progressBar = findViewById(R.id.progress);
//    }
//
//    private void setupClickListeners() {
//        prevButton.setOnClickListener(v -> showPreviousQuestion());
//        nextButton.setOnClickListener(v -> showNextQuestion());
//    }
//
//    private void loadQuiz() {
//        setLoading(true);
//       // FirebaseUtil.getQuiz(bookId, new FirebaseUtil.OnQuizLoadedListener() {
//            @Override
//            public void onQuizLoaded(Quiz loadedQuiz) {
//                quiz = loadedQuiz;
//                userAnswers = new ArrayList<>();
//                for (int i = 0; i < quiz.getQuestions().size(); i++) {
//                    userAnswers.add(-1); // -1 means no answer selected
//                }
//                setLoading(false);
//                showCurrentQuestion();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                setLoading(false);
//                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
//
//    private void showCurrentQuestion() {
//        if (quiz == null || quiz.getQuestions().isEmpty()) {
//            return;
//        }
//
//        Quiz.Question question = quiz.getQuestions().get(currentQuestionIndex);
//        quizTitle.setText(quiz.getTitle());
//        questionNumber.setText(String.format("Question %d of %d", currentQuestionIndex + 1, quiz.getQuestions().size()));
//        questionText.setText(question.getText());
//
//        // Set options
//        List<String> options = question.getOptions();
//        for (int i = 0; i < options.size(); i++) {
//            RadioButton radioButton = (RadioButton) optionsGroup.getChildAt(i);
//            radioButton.setText(options.get(i));
//            radioButton.setChecked(userAnswers.get(currentQuestionIndex) == i);
//        }
//
//        // Update navigation buttons
//        prevButton.setEnabled(currentQuestionIndex > 0);
//        nextButton.setText(currentQuestionIndex == quiz.getQuestions().size() - 1 ? "Finish" : "Next");
//    }
//
//    private void showPreviousQuestion() {
//        saveCurrentAnswer();
//        if (currentQuestionIndex > 0) {
//            currentQuestionIndex--;
//            showCurrentQuestion();
//        }
//    }
//
//    private void showNextQuestion() {
//        saveCurrentAnswer();
//        if (currentQuestionIndex < quiz.getQuestions().size() - 1) {
//            currentQuestionIndex++;
//            showCurrentQuestion();
//        } else {
//            submitQuiz();
//        }
//    }
//
//    private void saveCurrentAnswer() {
//        int selectedId = optionsGroup.getCheckedRadioButtonId();
//        if (selectedId != -1) {
//            int selectedIndex = optionsGroup.indexOfChild(findViewById(selectedId));
//            userAnswers.set(currentQuestionIndex, selectedIndex);
//        }
//    }
//
//    private void submitQuiz() {
//        // Check if all questions are answered
//        for (int answer : userAnswers) {
//            if (answer == -1) {
//                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        setLoading(true);
//        FirebaseUtil.submitQuiz(bookId, userId, userAnswers, new FirebaseUtil.OnQuizSubmittedListener() {
//            @Override
//            public void onSubmitted(int score) {
//                setLoading(false);
//                Toast.makeText(QuizActivity.this,
//                    String.format("Quiz completed! Score: %d/%d", score, quiz.getQuestions().size()),
//                    Toast.LENGTH_LONG).show();
//                finish();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                setLoading(false);
//                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setLoading(boolean isLoading) {
//        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        prevButton.setEnabled(!isLoading);
//        nextButton.setEnabled(!isLoading);
//        optionsGroup.setEnabled(!isLoading);
//    }
//}