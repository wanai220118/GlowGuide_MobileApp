package com.example.glowguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glowguide.R;
import java.util.Locale;

public class Question3Activity extends AppCompatActivity {
    private int[] answers = new int[5];
    private Button nextButton, backButton;
    private RadioGroup radioGroup3;
    private ProgressBar progressBar;
    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question3);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Go back to the previous activity

        // Initialize the RadioGroup, Button, and ProgressBar
        radioGroup3 = findViewById(R.id.radioGroup3);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        // Set the progress bar to reflect the third question (60%)
        progressBar.setProgress(60);

        TextView questionNumberText = findViewById(R.id.questionNumberText);
        questionNumberText.setText("Question 3/5");

        // Next button to navigate through the questions
        nextButton.setOnClickListener(v -> {
            int selectedId = radioGroup3.getCheckedRadioButtonId();
            if (selectedId != -1) {
                // Save the answer based on the selected radio button
                if (selectedId == R.id.answerA) {
                    answers[3] = 1;
                } else if (selectedId == R.id.answerB) {
                    answers[3] = 2;
                } else if (selectedId == R.id.answerC) {
                    answers[3] = 3;
                } else if (selectedId == R.id.answerD) {
                    answers[3] = 4;
                } else if (selectedId == R.id.answerE) {
                    answers[3] = 5;
                }

                // Move to the next question (Question4Activity)
                Intent intent = new Intent(Question3Activity.this, Question4Activity.class);
                intent.putExtra("answers", answers);  // Pass the answers array
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);  // Slide transition
            } else {
                // Prompt user to select an answer
                Toast.makeText(Question3Activity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize Text-to-Speech engine
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set the language to English (can change to other languages if needed)
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(Question3Activity.this, "Text-to-Speech language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    // Speak the question text when the activity is created
                    textToSpeech.speak("How does your skin react to new skincare or beauty products?", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                Toast.makeText(Question3Activity.this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
