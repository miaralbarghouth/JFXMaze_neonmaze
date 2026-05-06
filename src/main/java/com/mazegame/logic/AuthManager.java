package com.mazegame.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class AuthManager {
    private static final String USERS_FILE = "users.properties";
    private static final String DEFAULT_USERNAME = "mk";
    private static final String DEFAULT_PASSWORD = "mk";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Properties users = new Properties();

    public AuthManager() {
        loadUsers();
        if (!users.containsKey(DEFAULT_USERNAME)) {
            register(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        }
    }

    public boolean login(String username, String password) {
        String stored = users.getProperty(normalize(username));
        if (stored == null || password == null) {
            return false;
        }

        if (!stored.contains(":")) {
            return stored.equals(password);
        }

        String[] parts = stored.split(":", 2);
        return stored.equals(parts[0] + ":" + hash(password, parts[0]));
    }

    public boolean register(String username, String password) {
        String key = normalize(username);
        if (key.isEmpty() || password == null || password.isBlank() || users.containsKey(key)) {
            return false;
        }

        users.setProperty(key, encodePassword(password));
        saveUsers();
        return true;
    }

    public boolean renameUser(String currentUsername, String newUsername) {
        String currentKey = normalize(currentUsername);
        String newKey = normalize(newUsername);
        if (currentKey.isEmpty() || newKey.isEmpty() || !users.containsKey(currentKey) || users.containsKey(newKey)) {
            return false;
        }

        String passwordHash = users.getProperty(currentKey);
        users.remove(currentKey);
        users.setProperty(newKey, passwordHash);
        saveUsers();
        return true;
    }

    public boolean changePassword(String username, String newPassword) {
        String key = normalize(username);
        if (key.isEmpty() || newPassword == null || newPassword.isBlank() || !users.containsKey(key)) {
            return false;
        }

        users.setProperty(key, encodePassword(newPassword));
        saveUsers();
        return true;
    }

    public void deleteUser(String username) {
        users.remove(normalize(username));
        saveUsers();
    }

    private void loadUsers() {
        try {
            Path path = AppData.resolve(USERS_FILE);
            if (Files.exists(path)) {
                try (InputStream input = Files.newInputStream(path)) {
                    users.load(input);
                }
            }
            if (users.isEmpty()) {
                migrateLegacyUsers();
            }
        } catch (IOException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private void migrateLegacyUsers() {
        Path legacyPath = Path.of("users.dat");
        if (!Files.exists(legacyPath)) {
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(legacyPath))) {
            Object loaded = input.readObject();
            if (loaded instanceof Map<?, ?> legacyUsers) {
                for (Map.Entry<?, ?> entry : legacyUsers.entrySet()) {
                    if (entry.getKey() instanceof String username && entry.getValue() instanceof String password) {
                        users.setProperty(username, encodePassword(password));
                    }
                }
                saveUsers();
                Files.deleteIfExists(legacyPath);
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    private void saveUsers() {
        try {
            Path path = AppData.resolve(USERS_FILE);
            try (OutputStream output = Files.newOutputStream(path)) {
                users.store(output, "JFXMaze user accounts");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encodePassword(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String saltText = Base64.getEncoder().encodeToString(salt);
        return saltText + ":" + hash(password, saltText);
    }

    private String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim();
    }
}
