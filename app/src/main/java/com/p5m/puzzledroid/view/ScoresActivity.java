package com.p5m.puzzledroid.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.p5m.puzzledroid.R;
import com.p5m.puzzledroid.database.Score;
import com.p5m.puzzledroid.database.ScoreDao;
import com.p5m.puzzledroid.database.ScoreDatabase;
import com.p5m.puzzledroid.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * View that displays the user scores.
 */
public class ScoresActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Score> scores;
    private TextView scoresView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        // Set the reset scores button
        findViewById(R.id.button_reset_scores).setOnClickListener(this);
        scoresView = findViewById(R.id.scores_list);

        // Retrieve the scores list and then show them
        retrieveScores();
    }

    /**
     * Retrieve the scores form the database.
     */
    private void retrieveScores() {
        final ScoreDao scoreDao = ScoreDatabase.getInstance(this).scoreDao();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                scores = scoreDao.getAllScores();
                showScores();
            }
        });
    }

    /**
     * Set the scores from the database to the view.
     */
    private void showScores() {
        if (scores.isEmpty()) {
            scoresView.setText("There are no scores yet.");
        } else {
            scoresView.setText(Score.formatScores(scores));
        }
    }

    /**
     * Reset the database of scores when the button is clicked.
     * @param v
     */
    @Override
    public void onClick(View v) {
        final ScoreDao scoreDao = ScoreDatabase.getInstance(this).scoreDao();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                scoreDao.clear();
            }
        });
        scores = new ArrayList<Score>();
        showScores();
    }
}
