package com.mazegame.logic;

import com.mazegame.model.ScoreRecord;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {
    private static final String FILE_NAME = "leaderboard.dat";

    public static void saveScore(ScoreRecord record) {
        List<ScoreRecord> scores = loadScores();
        scores.add(record);
        // Sort by Weight descending (The Best Records)
        scores.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
        
        saveAll(scores);
    }

    public static void deleteScore(ScoreRecord record) {
        List<ScoreRecord> scores = loadScores();
        scores.removeIf(s -> s.getTimestamp().equals(record.getTimestamp()) && s.getUsername().equals(record.getUsername()));
        saveAll(scores);
    }

    private static void saveAll(List<ScoreRecord> scores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getDataFile()))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<ScoreRecord> loadScores() {
        File file = getDataFile();
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ScoreRecord>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private static File getDataFile() {
        try {
            Path path = AppData.resolve(FILE_NAME);
            Path legacyPath = Path.of(FILE_NAME);
            if (!Files.exists(path) && Files.exists(legacyPath)) {
                Files.move(legacyPath, path);
            }
            return path.toFile();
        } catch (IOException e) {
            return new File(FILE_NAME);
        }
    }
}
