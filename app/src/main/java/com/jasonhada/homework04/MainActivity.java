package com.jasonhada.homework04;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String QUESTIONS_KEY = "QUESTIONS";
    public static ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        init();

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TriviaActivity.class);
                i.putExtra(QUESTIONS_KEY, questions);

                startActivity(i);
            }
        });

    }

    private void init() {

        findViewById(R.id.logo_iv).setVisibility(View.INVISIBLE);
        findViewById(R.id.start_btn).setEnabled(false);

        if(isConnected()) {
            new GetQuestionsAsync().execute();
        }else{
            Toast.makeText(MainActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetQuestionsAsync extends AsyncTask<Void, Void, ArrayList<Question>> {
        private ProgressBar progressBar;

        public GetQuestionsAsync() {
            this.progressBar = findViewById(R.id.main_pb);
        }

        @Override
        protected ArrayList<Question> doInBackground(Void... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            ArrayList<Question> questions = new ArrayList<>();

            try {
                URL url = new URL("http://dev.theappsdr.com/apis/trivia_json/trivia_text.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {

                        String[] array = line.split(";");
                        Question question = new Question();
                        ArrayList<String> possibleAnswers = new ArrayList<>();

                        for(int i=0; i<array.length; i++) {
                            if(i==0)
                                question.setIndex(Integer.parseInt(array[i]));
                            else if (i==1)
                                question.setQuestion(array[i]);
                            else if (i==2)
                                question.setUrl(array[2]);
                            else if(i == array.length - 1 )
                                question.setAnswerIndex(Integer.parseInt(array[i]));
                            else
                                possibleAnswers.add(array[i]);
                        }
                        question.setOptions(possibleAnswers);
                        questions.add(question);
                    }
                    return questions;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }

                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return questions;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Question> results) {
            if (results != null){

                progressBar.setVisibility(View.INVISIBLE);
                findViewById(R.id.start_btn).setEnabled(true);
                findViewById(R.id.logo_iv).setVisibility(View.VISIBLE);

                questions = results;

            }else {
                Toast.makeText(MainActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected()){
            return false;
        }

        return true;
    }
}
