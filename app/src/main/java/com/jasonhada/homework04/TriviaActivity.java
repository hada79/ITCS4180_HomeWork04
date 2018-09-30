package com.jasonhada.homework04;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity {

    ArrayList<Question> questions;
    int currentQuestionIndex;

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


    }

    private void endGame() {

        // TODO
    }

    private void displayQuestion(Question question) {

        if(question.getUrl() != null) {
            new GetImageAsync().execute(question.getUrl());
        }

        TextView questionNo_tv = findViewById(R.id.questionNo_tv);
        questionNo_tv.setText("Q" + question.getIndex()+1);

        TextView question_tv = findViewById(R.id.question_tv);
        question_tv.setText(question.getQuestion());

        ArrayList<String> options = question.getOptions();

        // TODO: this needs to be able to handle variable number of options. Right now it is static.
        TextView option1_tv = findViewById(R.id.option1_tv);
        option1_tv.setText(options.get(0));

        TextView option2_tv = findViewById(R.id.option2_tv);
        option2_tv.setText(options.get(1));

        TextView option3_tv = findViewById(R.id.option3_tv);
        option3_tv.setText(options.get(2));

        TextView option4_tv = findViewById(R.id.option4_tv);
        option4_tv.setText(options.get(3));

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
