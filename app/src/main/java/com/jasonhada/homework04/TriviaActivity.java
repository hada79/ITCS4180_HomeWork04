package com.jasonhada.homework04;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity {

    static String SCORE_KEY = "SCORE";
    ArrayList<Question> questions;
    int currentQuestionIndex;
    RadioButton selectedRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        if(getIntent() != null && getIntent().getExtras() != null) {
            questions = (ArrayList<Question>) getIntent().getExtras().getSerializable(MainActivity.QUESTIONS_KEY);

            final TextView timer_tv = findViewById(R.id.timer_tv);

            new CountDownTimer(121000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timer_tv.setText("Time Left:" + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    endGame();

                }
            }.start();

            currentQuestionIndex = 0;

            displayQuestion(questions.get(currentQuestionIndex));
        }

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question = questions.get(currentQuestionIndex);
//                for(String string : question.getOptions()) {
//                    if(selectedRb != null) {
//                        if (string.matches(selectedRb.getText().toString())) {
//                            question.setAnswerIndex(question.getOptions().indexOf(string));
//                        }
//                    }
//                }

                // if more questions, next question, else end game
                if(currentQuestionIndex < questions.size() -1) {
                    currentQuestionIndex++;
                    displayQuestion(questions.get(currentQuestionIndex));
                } else {
                    endGame();
                }
            }
        });
    }

    private void endGame() {
        int totalScore = 0;
        for (Question q : questions) {
            if(q.getAnswerIndex() == q.getUserAnswerIndex()){
                totalScore++;
            }
        }

        int percent = (int) ( totalScore * 100.0f / questions.size());
        Intent i = new Intent(TriviaActivity.this, StatsActivity.class);
        i.putExtra(SCORE_KEY, percent);
        Log.d("demo", totalScore + " "+percent);
        startActivity(i);
    }

    private void displayQuestion(final Question question) {

        if(question.getUrl() != null) {
            new GetImageAsync().execute(question.getUrl());
        }

        question.setUserAnswerIndex(-1);

        TextView questionNo_tv = findViewById(R.id.questionNo_tv);
        questionNo_tv.setText("Q" + (question.getIndex()+1));

        TextView question_tv = findViewById(R.id.question_tv);
        question_tv.setText(question.getQuestion());

        final ArrayList<String> options = question.getOptions();

        RadioGroup rg = findViewById(R.id.options_rg);

        rg.removeAllViews();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //selectedRb = (RadioButton)findViewById(checkedId);
                question.setUserAnswerIndex(checkedId - 100);
            }
        });

        int parentId = R.id.question_tv;

        for(int i=0; i < options.size(); i++) {

            RadioButton[] rb = new RadioButton[options.size()];
            rg.setOrientation(RadioGroup.VERTICAL);

            rb[i] = new RadioButton(this);
            rb[i].setId(100+i);
            rb[i].setText(options.get(i));
            rg.addView(rb[i]);
        }

    }

    private class GetImageAsync extends AsyncTask<String, Void, Bitmap> {
        private ProgressBar progressBar;

        private GetImageAsync() {
            progressBar = findViewById(R.id.trivia_pb);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(View.INVISIBLE);

            ImageView image = findViewById(R.id.question_iv);
            image.setImageBitmap(bitmap);

            super.onPostExecute(bitmap);
        }
    }
}
