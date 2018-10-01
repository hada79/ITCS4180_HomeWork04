package com.jasonhada.homework04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        if(getIntent() != null && getIntent().getExtras() != null) {
            int percent = (int) getIntent().getExtras().getSerializable(TriviaActivity.SCORE_KEY);
            Log.d("demo", percent + "");
            TextView score = (TextView) findViewById(R.id.correctPercent_tv);
            score.setText(percent + "%");

            ProgressBar pb = (ProgressBar) findViewById(R.id.correctPercent_pb);
            pb.setProgress(percent);

        }

        findViewById(R.id.triviaQuit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.tryAgain_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, TriviaActivity.class);
                i.putExtra(MainActivity.QUESTIONS_KEY, MainActivity.questions);

                startActivity(i);
            }
        });
    }
}
