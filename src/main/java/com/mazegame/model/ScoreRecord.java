package com.mazegame.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final int score;
    private final int steps;
    private final long timeSeconds;
    private final int mazeSize;
    private final String timestamp;

    public ScoreRecord(String username, int score, int steps, long timeSeconds, int mazeSize) {
        this.username = username;
        this.score = score;
        this.steps = steps;
        this.timeSeconds = timeSeconds;
        this.mazeSize = mazeSize;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getSteps() { return steps; }
    public long getTimeSeconds() { return timeSeconds; }
    public int getMazeSize() { return mazeSize; }
    public String getTimestamp() { return timestamp; }

    public double getWeight() {
        // Higher weight is better. More points, bigger maze, less time.
        // +1 to time to avoid division by zero.
        return (double) (score * mazeSize) / (timeSeconds + 1);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %dx%d | %dP | %ds", timestamp, username, mazeSize, mazeSize, score, timeSeconds);
    }
}
