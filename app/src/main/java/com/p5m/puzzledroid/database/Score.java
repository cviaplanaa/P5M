package com.p5m.puzzledroid.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Class that represents a table from the database.
 */
@Entity(tableName = "scores")
public class Score {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "puzzle_name")
    private String puzzleName;
    @ColumnInfo(name = "time_of_score")
    private String timeOfScore;
    @ColumnInfo(name = "score_seconds")
    private int scoreSeconds;

    public Score(int id, String puzzleName, String timeOfScore, int scoreSeconds) {
        this.id = id;
        this.puzzleName = puzzleName;
        this.timeOfScore = timeOfScore;
        this.scoreSeconds = scoreSeconds;
    }

    @Ignore
    public Score() { }

    @Ignore
    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", puzzleName='" + puzzleName + '\'' +
                ", timeOfScore='" + timeOfScore + '\'' +
                ", scoreSeconds=" + scoreSeconds +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPuzzleName() {
        return puzzleName;
    }

    public void setPuzzleName(String puzzleName) {
        this.puzzleName = puzzleName;
    }

    public String getTimeOfScore() {
        return timeOfScore;
    }

    public void setTimeOfScore(String timeOfScore) {
        this.timeOfScore = timeOfScore;
    }

    public int getScoreSeconds() {
        return scoreSeconds;
    }

    public void setScoreSeconds(int scoreSeconds) {
        this.scoreSeconds = scoreSeconds;
    }
}
